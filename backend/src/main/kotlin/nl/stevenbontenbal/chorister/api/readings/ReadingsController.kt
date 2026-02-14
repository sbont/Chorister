package nl.stevenbontenbal.chorister.api.readings

import arrow.core.Either
import nl.stevenbontenbal.chorister.application.readings.IReadingsProvider
import nl.stevenbontenbal.chorister.application.readings.Readings
import nl.stevenbontenbal.chorister.shared.Failure
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDate
import java.time.format.DateTimeParseException

@BasePathAwareController
@ExposesResourceFor(Readings::class)
class ReadingsController(
    private val readingsProvider: IReadingsProvider
) {
    @GetMapping("/readings/{date}")
    suspend fun getReadings(@PathVariable date: String): ResponseEntity<Any> {
        return try {
            val localDate = LocalDate.parse(date)
            when (val result = readingsProvider.getReadings(localDate)) {
                is Either.Left<*> -> when (val failure = result.value) {
                    is Failure.Unexpected -> ResponseEntity.internalServerError().body(failure.message)
                    else -> ResponseEntity.internalServerError().build()
                }
                is Either.Right<*> -> ResponseEntity.ok(result.value)
            }
        } catch (_: DateTimeParseException) {
            ResponseEntity.badRequest().body("Invalid date format. Please use YYYY-MM-DD format.")
        }
    }
}