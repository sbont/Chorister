package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.configuration.KeycloakConfiguration
import nl.stevenbontenbal.chorister.configuration.ZitadelConfiguration
import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.interfaces.UserAuthorizationService
import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.lang.RuntimeException

class ZitadelUserService(
    private val zitadelConfiguration: ZitadelConfiguration,
    private val webClient: WebClient
) : UserAuthorizationService {
    override fun postUser(registrationRequest: RegistrationRequest): ResponseEntity<UserPostResponse>? {
        val request =
        webClient
            .post()
            .uri(
                UriComponentsBuilder
                .fromHttpUrl(zitadelConfiguration.baseUrl)
                .path("/users/human/_import")
                .build()
                .toUri())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({s: HttpStatus -> s.value() == HttpStatus.CONFLICT.value()}) {
                Mono.error(UsernameAlreadyExistingException("Username already exists in authentication server"))
            }
            .onStatus(HttpStatus::is4xxClientError) {
                Mono.error(RuntimeException("Keycloak client error: ${it.statusCode()}"))
            }
            .onStatus(HttpStatus::is5xxServerError) {
                Mono.error(RuntimeException("Keycloak server error: ${it.statusCode()}"))
            }
            .toEntity(UserPostResponse::class.java)
            .block()
    }
}