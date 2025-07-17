package nl.stevenbontenbal.chorister.authorization

import nl.stevenbontenbal.chorister.application.RegistrationRequest
import nl.stevenbontenbal.chorister.authorization.models.Email
import nl.stevenbontenbal.chorister.authorization.models.Profile
import nl.stevenbontenbal.chorister.authorization.models.ZitadelError
import nl.stevenbontenbal.chorister.authorization.models.ZitadelProjectRolesPostRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelResourceDetails
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserEmailGetResponse
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserEmailPutRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserGrantsRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserPostRequest
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUserPostResponse
import nl.stevenbontenbal.chorister.authorization.models.ZitadelUsernamePutRequest
import nl.stevenbontenbal.chorister.application.config.ZitadelProperties
import nl.stevenbontenbal.chorister.application.InvalidInputException
import nl.stevenbontenbal.chorister.application.UsernameAlreadyExistingException
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
        val rolesClaim = jwt?.getClaim<Map<String, String>>("urn:zitadel:iam:org:project:roles")
        val roles = rolesClaim?.keys?.map { UserRole.parse(it) }
        return roles?.toSet() ?: setOf()
    }

    override fun createRolesForTenant(tenantId: Long) {
        val body = createRolesRequest(tenantId, AccessLevel.MANAGER)
        webClient
            .post()
            .uri("/projects/${zitadelConfiguration.projectId}/roles")
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

    private fun createRolesRequest(tenantId: Long, accessLevel: AccessLevel): ZitadelProjectRolesPostRequest =
        ZitadelProjectRolesPostRequest(
            roleKey = roleKey(tenantId, accessLevel),
            displayName = "Tenant $tenantId ${accessLevel.name.lowercase().replaceFirstChar { it.uppercase() }}",
            group = "tenant.$tenantId"
        )

    private fun roleKey(tenantId: Long, accessLevel: AccessLevel): String {
        return "tenant.$tenantId.${accessLevel.name.lowercase()}"
    }

    override fun addRoleToUser(userId: ZitadelUserId, tenantId: Long) {
        val body = createAddRolesRequest(tenantId)
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

    private fun createAddRolesRequest(tenantId: Long): ZitadelUserGrantsRequest =
        ZitadelUserGrantsRequest(
            projectId = zitadelConfiguration.projectId,
            roleKeys = listOf(roleKey(tenantId, AccessLevel.MANAGER))
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
                firstName = registrationRequest.displayName.substring(0, 1),
                lastName = registrationRequest.displayName.substring(1),
                displayName = registrationRequest.displayName
            )
        )
}

typealias ZitadelUserId = String
