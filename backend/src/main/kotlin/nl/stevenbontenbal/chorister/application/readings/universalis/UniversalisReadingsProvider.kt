package nl.stevenbontenbal.chorister.application.readings.universalis

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import io.netty.handler.logging.LogLevel
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.serialization.json.Json
import nl.stevenbontenbal.chorister.application.readings.Copyright
import nl.stevenbontenbal.chorister.application.readings.IReadingsProvider
import nl.stevenbontenbal.chorister.application.readings.Reading
import nl.stevenbontenbal.chorister.application.readings.Readings
import nl.stevenbontenbal.chorister.shared.Failure
import org.slf4j.LoggerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class UniversalisReadingsProvider : IReadingsProvider {
    private val httpClient: HttpClient = HttpClient
        .create()
        .baseUrl("https://universalis.com")
        .wiretap(
            "reactor.netty.http.client.HttpClient",
            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL
        )
    private val webClient: WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .baseUrl("https://universalis.com")
        .build()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val logger = LoggerFactory.getLogger(UniversalisReadingsProvider::class.java)

    override suspend fun getReadings(date: LocalDate): Either<Failure, Readings> {
        val englandWales = "europe.england.westminster"
        val dateParam = date.format(dateFormatter)
        return Either.Companion.catch {
            webClient
                .get()
                .uri("/${englandWales}/${dateParam}/jsonpmass.js")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36") // Well, yes, I'm not proud of this either
                .retrieve()
                .bodyToMono<String>()
                .awaitSingle()
        }
            .mapLeft { it.toFailure() }
            .flatMap { retrieveJsonFromResponse(it) }
            .flatMap { parseReadings(it) }
            .map { map(it) }
    }

    private fun Throwable.toFailure(): Failure {
        if (this is WebClientResponseException && statusCode.value() == 404) {
            logger.warn("Universalis returned 404 for the requested date")
            return Failure.DateNotInNearFuture
        }
        return logFailure(this)
    }

    private fun logFailure(t: Throwable): Failure.Unexpected {
        logger.error("Failed to retrieve readings from Universalis: $t")
        return Failure.Unexpected(t.message ?: "Error while retrieving readings from Universalis" )
    }

    private fun logRequest(request: ClientRequest) {
        logger.info("Outgoing request: {} {}", request.method(), request.url())
        request.headers().forEach { (name, values) ->
            logger.info("Request header: {}: {}", name, values.joinToString(", "))
        }
    }

    private fun logResponse(response: ClientResponse) {
        logger.info("Response status: {}", response.statusCode())
        response.headers().asHttpHeaders().forEach { (name, values) ->
            logger.info("Response header: {}: {}", name, values.joinToString(", "))
        }
    }

    private fun retrieveJsonFromResponse(rawResponse: String): Either<Failure.Unexpected, String> {
        val trimmed = rawResponse.trim()
        val startPos = trimmed.indexOf(RESPONSE_WRAPPER_START)
        val endPos = trimmed.indexOf(RESPONSE_WRAPPER_END)

        if (startPos == -1) return Failure.Unexpected("Response wrapper start not found").left()
        if (endPos == -1) return Failure.Unexpected("Response wrapper end not found").left()

        val startIndex = startPos + RESPONSE_WRAPPER_START.length
        val jsObjectLiteral = trimmed.substring(startIndex, endPos)
        return deobfuscateObjectLiteral(jsObjectLiteral).right()
    }

    private fun deobfuscateObjectLiteral(objLiteral: String): String {
        var result = objLiteral
        var lastEnd = 0
        
        while (true) {
            val splitMarker = ".split(\""
            val splitPos = result.indexOf(splitMarker, lastEnd)
            if (splitPos == -1) break
            
            val quoteEnd = findStringEnd(result, splitPos - 1)
            if (quoteEnd == -1) {
                lastEnd = splitPos + 1
                continue
            }
            
            val quoteStart = result.lastIndexOf('"', quoteEnd - 1)
            if (quoteStart == -1) {
                lastEnd = splitPos + 1
                continue
            }
            
            val baseString = result.substring(quoteStart + 1, quoteEnd)
            
            val transformations = mutableListOf<Pair<String, String>>()
            var currentPos = splitPos
            var chainEnd = splitPos

            while (result.startsWith(splitMarker, currentPos)) {
                val splitCharStart = currentPos + splitMarker.length
                val splitCharEnd = result.indexOf('"', splitCharStart)
                if (splitCharEnd == -1) break
                val splitChar = result.substring(splitCharStart, splitCharEnd)

                val joinMarker = ".join(\""
                val splitCloseParen = result.indexOf(')', splitCharEnd)
                if (splitCloseParen == -1 || !result.startsWith(joinMarker, splitCloseParen + 1)) break

                val joinCharStart = splitCloseParen + 1 + joinMarker.length
                val joinCharEnd = result.indexOf('"', joinCharStart)
                if (joinCharEnd == -1) break
                val joinChar = result.substring(joinCharStart, joinCharEnd)

                transformations.add(splitChar to joinChar)

                val closeParen = result.indexOf(')', joinCharEnd)
                if (closeParen == -1) {
                    chainEnd = joinCharEnd
                    currentPos = joinCharEnd + 1
                    break
                }
                chainEnd = closeParen
                currentPos = closeParen + 1
            }
            
            var deobfuscated = unescapeJavaScriptString(baseString)
            for ((splitChar, joinChar) in transformations) {
                deobfuscated = deobfuscated.split(splitChar).joinToString(joinChar)
            }
            
            val replacement = "\"${escapeForJson(deobfuscated)}\""
            result = result.substring(0, quoteStart) + replacement + result.substring(chainEnd + 1)
            lastEnd = quoteStart + replacement.length
        }
        
        return result
    }

    private fun findStringEnd(s: String, startIndex: Int): Int {
        var i = startIndex
        var escaped = false
        
        while (i < s.length) {
            when (s[i]) {
                '\\' -> {
                    escaped = !escaped
                    i++
                }
                '"' -> {
                    if (!escaped) {
                        return i
                    }
                    escaped = false
                    i++
                }
                else -> {
                    escaped = false
                    i++
                }
            }
        }
        
        return -1
    }

    private fun unescapeJavaScriptString(s: String): String {
        return s.replace("\\b", "\u0008")
            .replace("\\f", "\u000C")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\\", "\\")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
            .replace("\\/", "/")
    }

    private fun escapeForJson(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun map(dto: UniversalisReadings): Readings {
        return Readings(
            date = dto.date,
            day = dto.day,
            reading1 = mapReading(dto.Mass_R1),
            psalm = mapReading(dto.Mass_Ps),
            reading2 = dto.Mass_R2?.let { mapReading(it) },
            psalm2 = dto.Mass_Ps2?.let { mapReading(it) },
            reading3 = dto.Mass_R3?.let { mapReading(it) },
            psalm3 = dto.Mass_Ps3?.let { mapReading(it) },
            reading4 = dto.Mass_R4?.let { mapReading(it) },
            psalm4 = dto.Mass_Ps4?.let { mapReading(it) },
            reading5 = dto.Mass_R5?.let { mapReading(it) },
            psalm5 = dto.Mass_Ps5?.let { mapReading(it) },
            reading6 = dto.Mass_R6?.let { mapReading(it) },
            psalm6 = dto.Mass_Ps6?.let { mapReading(it) },
            reading7 = dto.Mass_R7?.let { mapReading(it) },
            psalm7 = dto.Mass_Ps7?.let { mapReading(it) },
            reading8 = dto.Mass_R8?.let { mapReading(it) },
            psalm8 = dto.Mass_Ps8?.let { mapReading(it) },
            gospelAcclamation = dto.Mass_GA?.let { mapReading(it) },
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

    fun parseReadings(json: String): Either<Failure.Unexpected, UniversalisReadings> = Either.catch {
            Json.Default.decodeFromString(UniversalisReadings.serializer(), json)
        }.mapLeft { e -> Failure.Unexpected("Error while parsing readings: ${e.message}") }

    companion object {
        private const val RESPONSE_WRAPPER_START = "universalisCallback("
        private const val RESPONSE_WRAPPER_END = ");"
    }
}