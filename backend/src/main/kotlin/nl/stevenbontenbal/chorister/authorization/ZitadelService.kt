package nl.stevenbontenbal.chorister.authorization

import nl.stevenbontenbal.chorister.application.RegistrationRequest
import nl.stevenbontenbal.chorister.authorization.models.Email
import nl.stevenbontenbal.chorister.authorization.models.Profile
import nl.stevenbontenbal.chorister.authorization.models.ZitadelError
import nl.stevenbontenbal.chorister.authorization.models.ZitadelResourceDetails
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserEmailGetResponse
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserEmailPutRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelAddUserGrantsRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserPostRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserPostResponse
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUsernamePutRequest
import nl.stevenbontenbal.chorister.application.config.ZitadelProperties
import nl.stevenbontenbal.chorister.application.InvalidInputException
import nl.stevenbontenbal.chorister.application.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.authorization.models.Role
import nl.stevenbontenbal.chorister.authorization.models.ZitadelGrantsSearchRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelGrantsSearchResponse
import nl.stevenbontenbal.chorister.authorization.models.ZitadelProjectRolesBulkPostRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelPutUserGrantsRequest
import nl.stevenbontenbal.chorister.domain.users.AccessLevel
import nl.stevenbontenbal.chorister.domain.users.IUserAuthorizationService
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private const val RolesClaim = "urn:zitadel:iam:org:project:roles"

@Component
class ZitadelService(
    private val zitadelConfiguration: ZitadelProperties,
) : IUserAuthorizationService {
    private val webClient: WebClient = WebClient.create(zitadelConfiguration.baseUrl)

    override fun postUser(registrationRequest: RegistrationRequest): Result<String?> {
        val request = createUserPostRequest(registrationRequest)
        val response = webClient
            .post()
            .uri("/users/human/_import")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
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
        return Result.success(response?.body?.userId)
    }

    override fun setEmailAddress(userId: ZitadelUserId, email: String): Result<String> {
        val response = getCurrentEmail(userId)
        if (response?.body?.email?.email == email)
            return Result.success(email)

        val emailRequest = ZitadelUserEmailPutRequest(email, false)
        changeEmail(userId, emailRequest)

        val userNameRequest = ZitadelUsernamePutRequest(email)
        changeUserName(userId, userNameRequest)

        return Result.success(email)
    }

    override fun getExternalUserId(): ZitadelUserId {
        val jwt = getAuthToken()
        val userId = jwt?.subject
        return userId ?: throw AuthException("Zitadel user ID unknown.")
    }

    override fun getTenantId(): Long? {
        val roles = getRoles()
        return roles.filterIsInstance<TenantUser>().firstOrNull()?.tenantId
    }

    private fun getAuthToken(): Jwt? = SecurityContextHolder.getContext().authentication?.principal as Jwt?

    private fun getRoles(): Set<UserRole> {
        val jwt = getAuthToken()
        return if (jwt == null) setOf() else getRolesFromJwt(jwt)
    }

    override fun getRolesFromJwt(jwt: Jwt): Set<UserRole> {
        val rolesClaim = jwt.getClaim<Map<String, String>>(RolesClaim)
        val roles = rolesClaim?.keys?.map { UserRole.parse(it) }
        return roles?.toSet() ?: setOf()
    }

    override fun createRolesForTenant(tenantId: Long) {
        val body = createRolesBulkRequest(tenantId)
        webClient
            .post()
            .uri("/projects/${zitadelConfiguration.projectId}/roles/_bulk")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
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

    override fun addRoleToUser(userId: ZitadelUserId, tenantId: Long, accessLevel: AccessLevel) {
        val body = createAddRolesRequest(tenantId, accessLevel)
        webClient
            .post()
            .uri("/users/$userId/grants")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
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
    }

    override fun replaceUserRoles(userId: ZitadelUserId, tenantId: Long, accessLevel: AccessLevel) {
        val grantId = findUserGrantId(userId)
        val roleKeys = listOf(roleKey(tenantId, accessLevel))
        val requestBody = ZitadelPutUserGrantsRequest(roleKeys)
        webClient
            .put()
            .uri("/users/$userId/grants/$grantId")
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
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

    private fun findUserGrantId(userId: ZitadelUserId): String {
        val requestBody = ZitadelGrantsSearchRequest.userIdQuery(userId)
        val grants = webClient
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
        val grantId = grants?.body?.result
            ?.firstOrNull { it.projectId == zitadelConfiguration.projectId }
            ?.id

        if (grantId == null)
            throw AuthException("No user grants found")

        return grantId
    }

    private fun createAddRolesRequest(tenantId: Long, accessLevel: AccessLevel): ZitadelAddUserGrantsRequest =
        ZitadelAddUserGrantsRequest(
            projectId = zitadelConfiguration.projectId,
            roleKeys = listOf(roleKey(tenantId, accessLevel))
        )

    private fun changeUserName(
        userId: String,
        request: ZitadelUsernamePutRequest
    ) {
        WebClient.create()
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
    }

    private fun changeEmail(
        userId: String,
        request: ZitadelUserEmailPutRequest
    ) {
        WebClient.create()
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
    }

    private fun getCurrentEmail(userId: String) = webClient
        .get()
        .uri("/users/$userId/email")
        .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
        .retrieve()
        .toEntity(ZitadelUserEmailGetResponse::class.java)
        .block()

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
