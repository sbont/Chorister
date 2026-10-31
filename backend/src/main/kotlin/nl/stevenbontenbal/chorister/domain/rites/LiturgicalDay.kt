package nl.stevenbontenbal.chorister.domain.rites

import java.time.LocalDate

/**
 * The liturgical day for a date, always determined on the fly by the
 * [LiturgicalCalendar]: its resolved [info] and its canonical [reference].
 * Not persisted; persisted liturgical content refers to the day by reference.
 */
data class LiturgicalDay(
    val date: LocalDate,
    val info: LiturgicalDayInfo,
    val reference: LiturgicalDayReference,
) {
    val calendar: LiturgicalCalendarRegion
        get() = reference.calendar
}
