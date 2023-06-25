package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.configuration.ZitadelProperties
import nl.stevenbontenbal.chorister.exceptions.InvalidInputException
import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.interfaces.UserAuthorizationService
import nl.stevenbontenbal.chorister.model.dto.*
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

class ZitadelUserService(
    private val zitadelConfiguration: ZitadelProperties,
    private val webClient: WebClient
) : UserAuthorizationService {
    override fun postUser(registrationRequest: RegistrationRequest): Result<String?> {
        val request = createUserPostRequest(registrationRequest)
        val response = webClient
            .post()
            .uri(
                UriComponentsBuilder
                    .fromHttpUrl(zitadelConfiguration.baseUrl)
                    .path("/users/human/_import")
                    .build()
                    .toUri()
            )
            .headers { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
//            .attributes(clientRegistrationId("zitadel"))
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

    private fun createUserPostRequest(registrationRequest: RegistrationRequest): ZitadelUserPostRequest =
        ZitadelUserPostRequest(
            userName = registrationRequest.email,
            email = Email(
                email = registrationRequest.email
            ),
            password = registrationRequest.password,
            profile = Profile(
                firstName = registrationRequest.displayName.substring(0,1),
                lastName = registrationRequest.displayName.substring(1),
                displayName = registrationRequest.displayName
            )
        )
}