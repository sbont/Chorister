package nl.stevenbontenbal.chorister.application.liturgy

import arrow.core.Either
import arrow.core.flatMap
import nl.stevenbontenbal.chorister.application.readings.IReadingsProvider
import nl.stevenbontenbal.chorister.application.readings.Readings
import nl.stevenbontenbal.chorister.application.readings.Reading
import nl.stevenbontenbal.chorister.domain.rites.ContentKind
import nl.stevenbontenbal.chorister.domain.rites.LiturgicalCalendar
import nl.stevenbontenbal.chorister.domain.rites.LiturgicalDayInfo
import nl.stevenbontenbal.chorister.domain.rites.Liturgy
import nl.stevenbontenbal.chorister.domain.rites.LiturgyItem
import nl.stevenbontenbal.chorister.domain.rites.MassPart
import nl.stevenbontenbal.chorister.shared.Failure
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Composes the order of Mass for a date. The liturgy is a non-persisted value
 * object: the same date and language always produce the same liturgy. The
 * [LiturgicalCalendar] determines which elements exist (Gloria, Creed, second
 * reading); readings come from the [IReadingsProvider] (until replaced by our
 * own persisted readings repository) and ordinary texts from
 * [OrdinaryTextCatalog].
 */
@Component
class LiturgyComposer(
    private val readingsProvider: IReadingsProvider,
    private val calendar: LiturgicalCalendar,
) {
    suspend fun getLiturgy(date: LocalDate, languageTag: String): Either<Failure, Liturgy> =
        readingsProvider.getReadings(date).flatMap { readings ->
            Either.catch {
                buildLiturgy(date, languageTag, readings)
            }.mapLeft { t -> t.toFailure() }
        }

    private fun buildLiturgy(date: LocalDate, languageTag: String, readings: Readings): Liturgy {
        val day = calendar.day(date)
        return Liturgy(
            date = date,
            languageTag = languageTag,
            dayReference = day.reference.value,
            dayInfo = day.info,
            items = partsFor(day.info, readings).map { part -> itemFor(part, languageTag, readings) },
        )
    }

    private fun partsFor(dayInfo: LiturgicalDayInfo, readings: Readings): List<MassPart> =
        buildList {
            add(MassPart.ENTRANCE)
            add(MassPart.PENITENTIAL_ACT)
            add(MassPart.KYRIE)
            if (dayInfo.includesGloria) add(MassPart.GLORIA)
            add(MassPart.COLLECT)
            add(MassPart.READING_1)
            add(MassPart.PSALM_1)
            if (dayInfo.includesSecondReading) add(MassPart.READING_2)
            if (readings.reading3 != null) {
                add(MassPart.READING_3)
                if (readings.psalm3 != null) add(MassPart.PSALM_3)
            }
            if (readings.reading4 != null) {
                add(MassPart.READING_4)
                if (readings.psalm4 != null) add(MassPart.PSALM_4)
            }
            if (readings.reading5 != null) {
                add(MassPart.READING_5)
                if (readings.psalm5 != null) add(MassPart.PSALM_5)
            }
            if (readings.reading6 != null) {
                add(MassPart.READING_6)
                if (readings.psalm6 != null) add(MassPart.PSALM_6)
            }
            if (readings.reading7 != null) {
                add(MassPart.READING_7)
                if (readings.psalm7 != null) add(MassPart.PSALM_7)
            }
            if (readings.reading8 != null) {
                add(MassPart.READING_8)
                if (readings.psalm8 != null) add(MassPart.PSALM_8)
            }
            add(MassPart.GOSPEL_ACCLAMATION)
            add(MassPart.GOSPEL)
            if (dayInfo.includesCreed) add(MassPart.CREED)
            add(MassPart.UNIVERSAL_PRAYER)
            add(MassPart.OFFERTORY)
            add(MassPart.SANCTUS)
            add(MassPart.MEMORIAL_ACCLAMATION)
            add(MassPart.PATER_NOSTER)
            add(MassPart.AGNUS_DEI)
            add(MassPart.COMMUNION)
            add(MassPart.RECESSIONAL)
        }

    private fun itemFor(part: MassPart, languageTag: String, readings: Readings): LiturgyItem =
        when (part.contentKind) {
            ContentKind.READING -> {
                val reading = reading(part, readings)
                LiturgyItem(part = part, text = reading?.text, reference = reading?.source)
            }
            ContentKind.ORDINARY_TEXT -> {
                val ordinaryText = OrdinaryTextCatalog.resolve(part, languageTag)
                LiturgyItem(
                    part = part,
                    text = ordinaryText?.text,
                    translation = ordinaryText?.languageTag,
                )
            }
            else -> LiturgyItem(part = part)
        }

    private fun reading(part: MassPart, readings: Readings): Reading? =
        when (part) {
            MassPart.READING_1 -> readings.reading1
            MassPart.PSALM_1 -> readings.psalm
            MassPart.READING_2 -> readings.reading2
            MassPart.READING_3 -> readings.reading3
            MassPart.READING_4 -> readings.reading4
            MassPart.READING_5 -> readings.reading5
            MassPart.READING_6 -> readings.reading6
            MassPart.READING_7 -> readings.reading7
            MassPart.READING_8 -> readings.reading8
            MassPart.GOSPEL_ACCLAMATION -> readings.gospelAcclamation
            MassPart.GOSPEL -> readings.gospelReading
            else -> null
        }

    private fun Throwable.toFailure(): Failure.Unexpected = Failure.Unexpected(
        "Error while composing liturgy: ${this.message ?: this::class.simpleName}"
    )
}
