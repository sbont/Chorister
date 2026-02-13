package nl.stevenbontenbal.chorister.application.readings

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import nl.stevenbontenbal.chorister.shared.Failure
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UniversalisReadingsProvider {
    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://universalis.com")
        .build()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    public suspend fun getReadings(date: LocalDate) {
        val englandWales = "/Europe.England.Westminster"
        val dateParam = date.format(dateFormatter)
        val response = Either.catch {
            webClient
                .get()
                .uri("/${englandWales}/${dateParam}/jsonpmass.js")
                .retrieve()
                .bodyToMono<String>()
                .awaitSingle()
        }
            .mapLeft { Failure.Unexpected(it.message ?: "Error while retrieving readings from Universalis" ) }
            .flatMap { retrieveJsonFromResponse(it) }
            .map { Json.decodeFromString(UniversalisReadings.serializer(), it) }

    }

    private fun retrieveJsonFromResponse(rawResponse: String): Either<Failure.Unexpected, String> {
        val trimmed = rawResponse.trim()
        val startIndex = if (trimmed.indexOf(RESPONSE_WRAPPER_START) == -1) Failure.Unexpected("Response wrapper start not found")
            .left() else RESPONSE_WRAPPER_START.length.right()
        val endIndex = trimmed.indexOf(RESPONSE_WRAPPER_END)
            .let { if (it == -1) Failure.Unexpected("Response wrapper end not found").left() else it.right() }
        return either {
            trimmed.substring(startIndex.bind(), endIndex.bind())
        }
    }

    private fun map(dto: UniversalisReadings): Readings  {

    }

    companion object {
        private const val RESPONSE_WRAPPER_START = "universalisCallback("
        private const val RESPONSE_WRAPPER_END = ");"
    }
}