package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.configuration.ZitadelProperties
import nl.stevenbontenbal.chorister.exceptions.InvalidInputException
import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.interfaces.UserAuthorizationService
import nl.stevenbontenbal.chorister.model.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Component
class ZitadelUserService(
    private val zitadelConfiguration: ZitadelProperties,
    private val webClient: WebClient
) : UserAuthorizationService {
    override fun postUser(registrationRequest: RegistrationRequest): Result<String?> {
        val request = createUserPostRequest(registrationRequest)
        val response = webClient
            .post()
            .uri(createUri("/users/human/_import"))
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

    fun setEmailAddress(userId: String, email: String): Result<String> {
        val response = getCurrentEmail(userId)
        if (response?.body?.email?.email == email)
            return Result.success(email)

        val emailRequest = ZitadelUserEmailPutRequest(email, false)
        changeEmail(userId, emailRequest)

        val userNameRequest = ZitadelUsernamePutRequest(email)
        changeUserName(userId, userNameRequest)

        return Result.success(email)
    }

    private fun changeUserName(
        userId: String,
        request: ZitadelUsernamePutRequest
    ) {
        webClient
            .put()
            .uri(createUri("/users/$userId/username"))
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
        webClient
            .put()
            .uri(createUri("/users/$userId/email"))
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
        .uri(createUri("/users/$userId/email"))
        .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
        .retrieve()
        .toEntity(ZitadelUserEmailGetResponse::class.java)
        .block()

    private fun createUserPostRequest(registrationRequest: RegistrationRequest): ZitadelUserPostRequest =
        ZitadelUserPostRequest(
            userName = registrationRequest.email,
            email = Email(
                email = registrationRequest.email
            ),
            password = registrationRequest.password,
            profile = Profile(
                firstName = registrationRequest.displayName.substring(0, 1),
                lastName = registrationRequest.displayName.substring(1),
                displayName = registrationRequest.displayName
            )
        )

    private fun createUri(path: String): URI =
        UriComponentsBuilder
            .fromHttpUrl(zitadelConfiguration.baseUrl)
            .path(path)
            .build()
        .toUri()
}