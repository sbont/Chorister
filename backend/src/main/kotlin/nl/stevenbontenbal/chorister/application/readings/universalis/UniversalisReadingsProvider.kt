package nl.stevenbontenbal.chorister.application.readings.universalis

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.serialization.json.Json
import nl.stevenbontenbal.chorister.application.readings.Copyright
import nl.stevenbontenbal.chorister.application.readings.Reading
import nl.stevenbontenbal.chorister.application.readings.Readings
import nl.stevenbontenbal.chorister.shared.Failure
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UniversalisReadingsProvider {
    private val webClient: WebClient = WebClient.builder()
        .baseUrl("https://universalis.com")
        .build()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    public suspend fun getReadings(date: LocalDate): Either<Failure.Unexpected, Readings> {
        val englandWales = "/Europe.England.Westminster"
        val dateParam = date.format(dateFormatter)
        return Either.Companion.catch {
            webClient
                .get()
                .uri("/${englandWales}/${dateParam}/jsonpmass.js")
                .retrieve()
                .bodyToMono<String>()
                .awaitSingle()
        }
            .mapLeft { Failure.Unexpected(it.message ?: "Error while retrieving readings from Universalis" ) }
            .flatMap { retrieveJsonFromResponse(it) }
            .map { Json.Default.decodeFromString(UniversalisReadings.serializer(), it) }
            .map { map(it) }

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

    private fun map(dto: UniversalisReadings): Readings {
        return Readings(
            date = dto.date,
            day = dto.day,
            reading1 = mapReading(dto.Mass_R1),
            psalm = mapReading(dto.Mass_Ps),
            reading2 = dto.Mass_R2?.let { mapReading(it) },
            gospelAcclamation = mapReading(dto.Mass_GA),
            gospelReading = mapReading(dto.Mass_G),
            copyright = mapCopyright(dto.copyright)
        )
    }

    private fun mapReading(readingDto: nl.stevenbontenbal.chorister.application.readings.universalis.Reading): Reading {
        return Reading(
            source = readingDto.source,
            text = readingDto.text
        )
    }

    private fun mapCopyright(copyrightDto: nl.stevenbontenbal.chorister.application.readings.universalis.Copyright): Copyright {
        return Copyright(
            text = copyrightDto.text
        )
    }

    companion object {
        private const val RESPONSE_WRAPPER_START = "universalisCallback("
        private const val RESPONSE_WRAPPER_END = ");"
    }
}