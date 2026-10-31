package nl.stevenbontenbal.chorister.domain.rites

/**
 * The canonical reference that uniquely identifies a liturgical calendar day:
 * `{calendar key}:{day key}`, e.g. `england-wales:sunday:year-c:ordinary-time:23`.
 *
 * A liturgical day is fully determined by the calendar and the date, so the
 * day itself is always resolved on the fly with [LiturgicalCalendar.day];
 * days are not persisted. Persisted liturgical content (readings, propers)
 * stores this reference to stitch liturgies together later.
 *
 * The day key identifies the day as the lectionary does, not by natural date:
 *
 * - `day:{slug}` — days with fixed readings, e.g. `day:all-saints`,
 *   `day:holy-thursday`, `day:ash-wednesday`, `day:pentecost`
 * - `day:year-{a|b|c}:{slug}` — days whose readings follow the Sunday cycle,
 *   e.g. `day:year-a:good-friday`, `day:year-b:ascension`,
 *   `day:year-a:baptism-of-the-lord`
 * - `sunday:year-{a|b|c}:{season}:{ordinal}` — Sundays of a season, e.g.
 *   `sunday:year-c:ordinary-time:23`, `sunday:year-a:lent:6` (Palm Sunday)
 * - `weekday:{season}:{week}:{dow}` — weekdays of Advent, Lent, Easter and
 *   Christmas, whose readings are the same every year, e.g.
 *   `weekday:lent:3:1` (Monday of the Third Week of Lent); `dow` is the ISO
 *   day of week (1 = Monday), `week` 0 denotes the days between Ash Wednesday
 *   and the First Sunday of Lent
 * - `weekday:year-{i|ii}:ordinary-time:{week}:{dow}` — weekdays of Ordinary
 *   Time, whose readings follow the two-year weekday cycle (Year I in
 *   odd-numbered years, Year II in even-numbered years), e.g.
 *   `weekday:year-ii:ordinary-time:12:1`
 *
 * References group by calendar and order chronologically within a calendar
 * for the season and week-based keys, so prefix and range comparisons work
 * as plain string comparisons.
 */
data class LiturgicalDayReference(
    val calendar: LiturgicalCalendarRegion,
    val dayKey: String,
) {
    val value: String
        get() = "${calendar.key}:$dayKey"

    override fun toString(): String = value

    companion object {
        /** Returns null when the reference is not in `{calendar key}:{day key}` form. */
        fun parse(reference: String): LiturgicalDayReference? {
            val calendarKey = reference.substringBefore(':', "")
            val dayKey = reference.substringAfter(':', "")
            if (dayKey.isEmpty()) return null
            val calendar = LiturgicalCalendarRegion.entries.firstOrNull { it.key == calendarKey }
                ?: return null
            return LiturgicalDayReference(calendar, dayKey)
        }
    }
}
