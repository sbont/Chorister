import io.netty.handler.logging.LogLevel
import nl.stevenbontenbal.chorister.application.InvalidInputException
import nl.stevenbontenbal.chorister.application.users.RegistrationRequest
import nl.stevenbontenbal.chorister.application.users.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.application.config.ZitadelProperties
import nl.stevenbontenbal.chorister.authorization.AuthException
import nl.stevenbontenbal.chorister.authorization.UserRole
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.*
import nl.stevenbontenbal.chorister.domain.users.AccessLevel
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

class ZitadelV1Client(
    private val zitadelConfiguration: ZitadelProperties,
) {
    private val httpClient: HttpClient = HttpClient
        .create()
        .baseUrl(zitadelConfiguration.baseUrl + "/management/v1")
        .wiretap(
            "reactor.netty.http.client.HttpClient",
            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL
        )

    private val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .defaultHeaders { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
        .build()

    fun postUser(registrationRequest: RegistrationRequest): ZitadelUserPostResponse? {
        val request = createUserPostRequest(registrationRequest)
        val response = webClient
            .post()
            .uri("/users/human/_import")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it.value() == HttpStatus.CONFLICT.value() }) {
                Mono.error(UsernameAlreadyExistingException("Username already exists in authentication server"))
            }
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while creating user: ${err.message}"))
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Zitadel server error: ${it.statusCode()}"))
            }
            .toEntity(ZitadelUserPostResponse::class.java)
            .block()
        return response?.body
    }

    fun createRolesForTenant(tenantId: Long): ZitadelResourceDetails? {
        val body = createRolesBulkRequest(tenantId)
        val response = webClient
            .post()
            .uri("/projects/${zitadelConfiguration.projectId}/roles/_bulk")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while creating roles: ${err.message}"))
                }
            }
            .toEntity(ZitadelResourceDetails::class.java)
            .block()

        return response?.body
    }

    private fun createRolesBulkRequest(tenantId: Long): ZitadelProjectRolesBulkPostRequest =
        ZitadelProjectRolesBulkPostRequest(
            roles = AccessLevel.entries.map {
                Role(
                    key = roleKey(tenantId, it),
                    displayName = "Tenant $tenantId ${it.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    group = "tenant.$tenantId"
                )
            }
        )

    private fun roleKey(tenantId: Long, accessLevel: AccessLevel): String {
        return "tenant.$tenantId.${accessLevel.name.lowercase()}"
    }

    fun addRoleToUser(userId: ZitadelUserId, tenantId: Long, accessLevel: AccessLevel): ZitadelResourceDetails? {
        val body = createAddRolesRequest(tenantId, accessLevel)
        val response = webClient
            .post()
            .uri("/users/$userId/grants")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(body))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while adding grants to user: ${err.message}"))
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Zitadel server error: ${it.statusCode()}"))
            }
            .toEntity(ZitadelResourceDetails::class.java)
            .block()

        return response?.body
    }

    // Zitadel contains a bug here - don't use
    fun retrieveUserRolesOld(tenantId: Long): Map<ZitadelUserId, Set<UserRole>> {
        val searchString = "${UserRole.TENANT_ROLE_PREFIX}.$tenantId."
        val requestBody = ZitadelGrantsSearchRequest.roleKeyQuery(searchString)
        val grants = findUserGrants(requestBody)
        return grants.associate { grant -> grant.userId to grant.roleKeys.map { UserRole.parse(it) }.toSet() }
    }

    fun retrieveUserRoles(tenantId: Long): Map<ZitadelUserId, Set<UserRole>> {
        val searchString = "${UserRole.TENANT_ROLE_PREFIX}.$tenantId."
        val requestBody = ZitadelGrantsSearchRequest.roleKeyQuery(searchString)
        val grants = findUserGrants(requestBody)
        return grants.associate { grant -> grant.userId to grant.roleKeys.map { UserRole.parse(it) }.toSet() }
    }

    fun putGrantRoles(userId: ZitadelUserId, grantId: String, roleKeys: List<String>) {
        val requestBody = ZitadelPutUserGrantsRequest(roleKeys)
        webClient
            .put()
            .uri("/users/$userId/grants/$grantId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while updating user grant: ${err.message}"))
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Zitadel server error: ${it.statusCode()}"))
            }
            .toBodilessEntity()
            .block()
    }

    fun findUserGrantId(userId: ZitadelUserId): String {
        val requestBody = ZitadelGrantsSearchRequest.userIdQuery(userId)
        val grants = findUserGrants(requestBody)
        val grantId = grants.firstOrNull { it.projectId == zitadelConfiguration.projectId }?.id

        return grantId ?: throw AuthException("No user grant found")
    }

    fun findUserGrants(zitadelRoleName: String): List<ZitadelUserGrant> {
        val requestBody = ZitadelGrantsSearchRequest.roleKeyQuery(zitadelRoleName)
        return findUserGrants(requestBody)
    }

    fun findUserGrants(requestBody: ZitadelGrantsSearchRequest): List<ZitadelUserGrant> {
        val response = webClient
            .post()
            .uri("/users/grants/_search")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while retrieving user grants: ${err.message}"))
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Zitadel server error: ${it.statusCode()}"))
            }
            .toEntity(ZitadelGrantsSearchResponse::class.java)
            .block()

        return response?.body?.result ?: listOf()
    }

    private fun createAddRolesRequest(tenantId: Long, accessLevel: AccessLevel): ZitadelAddUserGrantsRequest =
        ZitadelAddUserGrantsRequest(
            projectId = zitadelConfiguration.projectId,
            roleKeys = listOf(roleKey(tenantId, accessLevel))
        )

    fun changeUserName(
        userId: String,
        request: ZitadelUsernamePutRequest
    ): ZitadelResourceDetails? {
        val response = WebClient.create()
            .put()
            .uri("/users/$userId/username")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while updating user: ${err.message}"))
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Zitadel server error: ${it.statusCode()}"))
            }
            .toEntity(ZitadelResourceDetails::class.java)
            .block()
        return response?.body
    }

    fun changeEmail(
        userId: String,
        request: ZitadelUserEmailPutRequest
    ): ZitadelUserEmailGetResponse? {
        val response = WebClient.create()
            .put()
            .uri("/users/$userId/email")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) {
                it.bodyToMono(ZitadelError::class.java).flatMap { err ->
                    Mono.error(InvalidInputException("Error while updating user: ${err.message}"))
                }
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Zitadel server error: ${it.statusCode()}"))
            }
            .toEntity(ZitadelUserEmailGetResponse::class.java)
            .block()
        return response?.body
    }

    fun getCurrentEmail(userId: String): ZitadelUserEmailGetResponse? {
        val response = webClient
            .get()
            .uri("/users/$userId/email")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
            .retrieve()
            .toEntity(ZitadelUserEmailGetResponse::class.java)
            .block()
        return response?.body
    }

    private fun createUserPostRequest(registrationRequest: RegistrationRequest): ZitadelUserPostRequest =
        ZitadelUserPostRequest(
            userName = registrationRequest.email,
            email = Email(
                email = registrationRequest.email,
                isEmailVerified = true,
            ),
            password = registrationRequest.password,
            passwordChangeRequired = false,
            profile = Profile(
                firstName = registrationRequest.firstName,
                lastName = registrationRequest.lastName,
                displayName = registrationRequest.firstName
            )
        )
}

typealias ZitadelUserId = String
