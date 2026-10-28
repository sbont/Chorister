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
                    is Failure.Unexpected -> ResponseEntity.internalServerError()
                        .body(ReadingsError(ReadingsError.CODE_UNEXPECTED, failure.message))
                    is Failure.DateNotInNearFuture -> ResponseEntity.badRequest()
                        .body(
                            ReadingsError(
                                ReadingsError.CODE_DATE_NOT_IN_NEAR_FUTURE,
                                "Readings can only be retrieved for dates in the near future."
                            )
                        )
                    else -> ResponseEntity.internalServerError()
                        .body(ReadingsError(ReadingsError.CODE_UNEXPECTED, "An unexpected error occurred."))
                }
                is Either.Right<*> -> ResponseEntity.ok(result.value)
            }
        } catch (_: DateTimeParseException) {
            ResponseEntity.badRequest()
                .body(ReadingsError(ReadingsError.CODE_INVALID_DATE, "Invalid date format. Please use YYYY-MM-DD format."))
        }
    }
}