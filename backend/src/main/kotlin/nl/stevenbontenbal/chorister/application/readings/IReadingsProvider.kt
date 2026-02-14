package nl.stevenbontenbal.chorister.application.readings

import arrow.core.Either
import nl.stevenbontenbal.chorister.shared.Failure
import java.time.LocalDate

interface IReadingsProvider {
    suspend fun getReadings(date: LocalDate): Either<Failure.Unexpected, Readings>
}