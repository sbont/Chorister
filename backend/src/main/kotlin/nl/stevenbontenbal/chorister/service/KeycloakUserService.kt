package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.configuration.KeycloakConfiguration
import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.interfaces.UserAuthorizationService
import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class KeycloakUserService(
    private val keycloakConfiguration: KeycloakConfiguration,
    private val webClient: WebClient
) : UserAuthorizationService {

    override fun postUser(registrationRequest: RegistrationRequest): Result<String> {
        val request = createUserPostRequest(registrationRequest)
        webClient
            .post()
            .uri(
                UriComponentsBuilder
                    .fromHttpUrl(keycloakConfiguration.url)
                    .path("/users")
                    .build()
                    .toUri()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ s -> s.value() == HttpStatus.CONFLICT.value() }) {
                Mono.error(UsernameAlreadyExistingException("Username already exists in authentication server"))
            }
            .onStatus(HttpStatusCode::is4xxClientError) {
                Mono.error(RuntimeException("Keycloak client error: ${it.statusCode()}"))
            }
            .onStatus(HttpStatusCode::is5xxServerError) {
                Mono.error(RuntimeException("Keycloak server error: ${it.statusCode()}"))
            }
            .toBodilessEntity()
            .block()
        return Result.success("")
    }

    private fun createUserPostRequest(registrationRequest: RegistrationRequest): UserPostRequest =
        UserPostRequest(
            username = registrationRequest.email,
            email = registrationRequest.email,
            credentials = mutableListOf(
                UserPostRequest.Credential(
                    type = "password",
                    value = registrationRequest.password
                )
            ),
            firstName = registrationRequest.displayName,
            lastName = null
        )
}