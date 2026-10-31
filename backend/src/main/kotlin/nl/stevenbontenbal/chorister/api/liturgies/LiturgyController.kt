package nl.stevenbontenbal.chorister.api.liturgies

import arrow.core.Either
import nl.stevenbontenbal.chorister.api.liturgies.models.LiturgyError
import nl.stevenbontenbal.chorister.application.liturgy.LiturgyComposer
import nl.stevenbontenbal.chorister.domain.rites.Liturgy
import nl.stevenbontenbal.chorister.shared.Failure
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.time.format.DateTimeParseException

@BasePathAwareController
@ExposesResourceFor(Liturgy::class)
class LiturgyController(
    private val liturgyComposer: LiturgyComposer
) {
    @GetMapping("/liturgies/{date}")
    suspend fun getLiturgy(
        @PathVariable date: String,
        @RequestParam(required = false) language: String?,
    ): ResponseEntity<Any> {
        return try {
            val localDate = LocalDate.parse(date)
            when (val result = liturgyComposer.getLiturgy(localDate, language ?: "en")) {
                is Either.Left -> failureResponse(result.value)
                is Either.Right -> ResponseEntity.ok(result.value)
            }
        } catch (_: DateTimeParseException) {
            ResponseEntity.badRequest()
                .body(LiturgyError(LiturgyError.CODE_INVALID_DATE, "Invalid date format. Please use YYYY-MM-DD format."))
        }
    }

    private fun failureResponse(failure: Failure): ResponseEntity<Any> = when (failure) {
        is Failure.DateNotInNearFuture -> ResponseEntity.badRequest()
            .body(
                LiturgyError(
                    LiturgyError.CODE_DATE_NOT_IN_NEAR_FUTURE,
                    "Readings can only be retrieved for dates in the near future."
                )
            )
        is Failure.Unexpected -> ResponseEntity.internalServerError()
            .body(LiturgyError(LiturgyError.CODE_UNEXPECTED, failure.message))
        else -> ResponseEntity.internalServerError()
            .body(LiturgyError(LiturgyError.CODE_UNEXPECTED, "An unexpected error occurred."))
    }
}
