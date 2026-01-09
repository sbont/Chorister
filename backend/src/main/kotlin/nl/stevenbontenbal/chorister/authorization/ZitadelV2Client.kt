package nl.stevenbontenbal.chorister.authorization

import io.netty.handler.logging.LogLevel
import nl.stevenbontenbal.chorister.application.InvalidInputException
import nl.stevenbontenbal.chorister.application.config.ZitadelProperties
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelError
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelGrantsSearchRequest
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelGrantsSearchResponse
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUserGrant
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

class ZitadelV2Client(
    private val zitadelConfiguration: ZitadelProperties,
) {
    private val httpClient: HttpClient = HttpClient
        .create()
        .baseUrl(zitadelConfiguration.baseUrl)
        .wiretap(
            "reactor.netty.http.client.HttpClient",
            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL
        )

    private val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .defaultHeaders { it.setBearerAuth(zitadelConfiguration.adminAccessToken) }
        .build()

    // TODO: Work in progress - however, it seems to contain the same bug as the v1 API
    fun findAuthorizations(requestBody: ZitadelGrantsSearchRequest): List<ZitadelUserGrant> {
        val response = webClient
            .post()
            .uri("/zitadel.authorization.v2.AuthorizationService/ListAuthorizations")
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

        return response?.body?.result ?: throw AuthException("Something went wrong while retrieving user grants")
    }
}