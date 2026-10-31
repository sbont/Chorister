package nl.stevenbontenbal.chorister.domain.rites

import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * The liturgical calendar of England and Wales (Roman rite, Novus Ordo, as
 * published for the province of Westminster): resolves season, colour, day
 * name and the type of Mass for a date, which in turn determines which
 * elements belong to the liturgy (Gloria, Creed, second reading).
 *
 * Rules specific to England and Wales:
 * - the Epiphany of the Lord is transferred to the Sunday between 2 and 8
 *   January (the Baptism of the Lord follows one week later);
 * - the Ascension of the Lord is kept on the 7th Sunday of Easter;
 * - Corpus Christi (the Most Holy Body and Blood of Christ) is kept on the
 *   Sunday after the Most Holy Trinity, and the Sacred Heart on the Friday
 *   after that Sunday;
 * - Saints Peter and Paul, the Assumption and All Saints are transferred to
 *   the adjacent Sunday when they fall on a Saturday or Monday;
 * - Saint George (23 April), patron of England, is kept as a solemnity.
 *
 * Remaining simplifications, to be refined when readings move to our own
 * repository: other feasts and memorials are treated as ordinary weekdays,
 * and the Sunday precedence rules for solemnities falling in Advent and Lent
 * are not applied (a solemnity always wins).
 */
@Component
class LiturgicalCalendar {

    fun resolve(date: LocalDate): LiturgicalDayInfo {
        val easter = easterSunday(date.year)
        val ashWednesday = easter.minusDays(46)
        val palmSunday = easter.minusDays(7)
        val holyThursday = easter.minusDays(3)
        val goodFriday = easter.minusDays(2)
        val holySaturday = easter.minusDays(1)
        val pentecost = easter.plusDays(49)
        val baptismOfTheLord = baptismOfTheLord(date.year)

        return when (date) {
            holyThursday -> LiturgicalDayInfo(
                "Holy Thursday", Season.LENT, LiturgicalColor.WHITE, MassType.HOLY_THURSDAY
            )
            goodFriday -> LiturgicalDayInfo(
                "Good Friday", Season.LENT, LiturgicalColor.RED, MassType.GOOD_FRIDAY
            )
            holySaturday -> LiturgicalDayInfo(
                "Holy Saturday", Season.LENT, LiturgicalColor.PURPLE, MassType.EASTER_VIGIL
            )
            baptismOfTheLord -> LiturgicalDayInfo(
                "The Baptism of the Lord", Season.CHRISTMAS, LiturgicalColor.WHITE, MassType.SUNDAY
            )
            else -> {
                val season = seasonOf(date, easter, ashWednesday, pentecost, baptismOfTheLord)
                val solemnity = solemnityName(date, easter)
                if (solemnity != null) {
                    LiturgicalDayInfo(
                        solemnity, season,
                        if (date == pentecost) LiturgicalColor.RED else LiturgicalColor.WHITE,
                        MassType.SOLEMNITY
                    )
                } else if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                    LiturgicalDayInfo(
                        sundayName(date, easter, ashWednesday, palmSunday, season),
                        season, seasonColor(season), MassType.SUNDAY
                    )
                } else {
                    LiturgicalDayInfo(
                        weekdayName(date, easter, ashWednesday, palmSunday, season),
                        season, seasonColor(season), MassType.FERIA
                    )
                }
            }
        }
    }

    private fun seasonOf(
        date: LocalDate,
        easter: LocalDate,
        ashWednesday: LocalDate,
        pentecost: LocalDate,
        baptismOfTheLord: LocalDate,
    ): Season {
        return when {
            date in easter..pentecost -> Season.EASTER
            date in ashWednesday..easter.minusDays(4) -> Season.LENT
            date.monthValue == 12 && date.dayOfMonth >= 25 -> Season.CHRISTMAS
            date.monthValue == 1 && date <= baptismOfTheLord -> Season.CHRISTMAS
            date >= firstAdventSunday(date.year) && date.monthValue == 12 && date.dayOfMonth <= 24 -> Season.ADVENT
            else -> Season.ORDINARY_TIME
        }
    }

    private fun seasonColor(season: Season): LiturgicalColor = when (season) {
        Season.ADVENT, Season.LENT -> LiturgicalColor.PURPLE
        Season.CHRISTMAS, Season.EASTER -> LiturgicalColor.WHITE
        Season.ORDINARY_TIME -> LiturgicalColor.GREEN
    }

    private fun solemnityName(date: LocalDate, easter: LocalDate): String? {
        transferredHolydayName(date)?.let { return it }
        holydayName(date)?.let { return it }
        if (date == epiphanySunday(date.year)) return "The Epiphany of the Lord"
        movableSolemnities[date.toEpochDay() - easter.toEpochDay()]?.let { return it }
        return fixedSolemnities[date.monthValue to date.dayOfMonth]
    }

    /** A holyday of obligation kept on its own date, i.e. not transferred to a Sunday. */
    private fun holydayName(date: LocalDate): String? {
        val name = transferableHolydays[date.monthValue to date.dayOfMonth] ?: return null
        val dayOfWeek = LocalDate.of(date.year, date.monthValue, date.dayOfMonth).dayOfWeek
        return if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.MONDAY) null else name
    }

    /**
     * England and Wales: Saints Peter and Paul, the Assumption and All Saints
     * are transferred to the adjacent Sunday when they fall on a Saturday or
     * Monday. Returns the solemnity name when [date] is such a Sunday.
     */
    private fun transferredHolydayName(date: LocalDate): String? {
        if (date.dayOfWeek != DayOfWeek.SUNDAY) return null
        for ((monthDay, name) in transferableHolydays) {
            val holyday = LocalDate.of(date.year, monthDay.first, monthDay.second)
            if (holyday.dayOfWeek == DayOfWeek.SATURDAY && holyday.plusDays(1) == date) return name
            if (holyday.dayOfWeek == DayOfWeek.MONDAY && holyday.minusDays(1) == date) return name
        }
        return null
    }

    private fun sundayName(
        date: LocalDate,
        easter: LocalDate,
        ashWednesday: LocalDate,
        palmSunday: LocalDate,
        season: Season,
    ): String {
        val firstSundayOfLent = ashWednesday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return when {
            date == palmSunday -> "Palm Sunday of the Passion of the Lord"
            season == Season.LENT -> "${ordinal(weeksBetween(firstSundayOfLent, date) + 1)} Sunday of Lent"
            season == Season.ADVENT ->
                "${ordinal(weeksBetween(firstAdventSunday(date.year), date) + 1)} Sunday of Advent"
            season == Season.EASTER -> "${ordinal(weeksBetween(easter, date) + 1)} Sunday of Easter"
            season == Season.CHRISTMAS -> "Sunday of the Christmas Season"
            else -> {
                val number = ordinaryTimeSundayNumber(date, easter, ashWednesday, season)
                if (number != null) "${ordinal(number)} Sunday in Ordinary Time" else "Sunday"
            }
        }
    }

    private fun weekdayName(
        date: LocalDate,
        easter: LocalDate,
        ashWednesday: LocalDate,
        palmSunday: LocalDate,
        season: Season,
    ): String {
        val weekday = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val firstSundayOfLent = ashWednesday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return when {
            date == ashWednesday -> "Ash Wednesday"
            season == Season.LENT && date < firstSundayOfLent -> "$weekday after Ash Wednesday"
            season == Season.LENT && date > palmSunday -> "$weekday of Holy Week"
            season == Season.LENT ->
                "$weekday of the ${ordinal(sundaysSince(ashWednesday, date))} Week of Lent"
            season == Season.ADVENT ->
                "$weekday of the ${ordinal(sundaysSince(firstAdventSunday(date.year), date) + 1)} Week of Advent"
            season == Season.EASTER && weeksBetween(easter, date) == 0 ->
                "$weekday of the Octave of Easter"
            season == Season.EASTER ->
                "$weekday of the ${ordinal(weeksBetween(easter, date))} Week of Easter"
            season == Season.CHRISTMAS -> "$weekday of the Christmas Season"
            else -> {
                val weekNumber = ordinaryTimeWeekNumber(date, easter, ashWednesday)
                if (weekNumber != null)
                    "$weekday of the ${ordinal(weekNumber)} Week in Ordinary Time"
                else
                    "$weekday in Ordinary Time"
            }
        }
    }

    /**
     * The number of an Ordinary Time Sunday: 1..34, or null when the Sunday is
     * outside Ordinary Time or carries a solemnity.
     */
    private fun ordinaryTimeSundayNumber(
        date: LocalDate,
        easter: LocalDate,
        ashWednesday: LocalDate,
        season: Season,
    ): Int? {
        if (season != Season.ORDINARY_TIME || solemnityName(date, easter) != null) return null
        val pentecost = easter.plusDays(49)
        val baptismOfTheLord = baptismOfTheLord(date.year)
        return when {
            date in easter..pentecost -> null
            date in ashWednesday..easter -> null
            date <= baptismOfTheLord -> null
            date > pentecost ->
                (34 - (firstAdventSunday(date.year).toEpochDay() - 1 - date.toEpochDay()) / 7).toInt()
            else -> (weeksBetween(baptismOfTheLord, date) + 1).toInt()
        }
    }

    /**
     * The Ordinary Time week number of a weekday. Weekdays take the number of
     * the preceding numbered Sunday; when that Sunday carried a solemnity
     * (e.g. Trinity or Corpus Christi in England and Wales), the number is
     * derived from the next numbered Sunday minus the Sundays in between.
     */
    private fun ordinaryTimeWeekNumber(date: LocalDate, easter: LocalDate, ashWednesday: LocalDate): Int? {
        val previousSunday = date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
        ordinaryTimeSundayNumber(previousSunday, easter, ashWednesday, season(previousSunday, easter))
            ?.let { return it }

        var sunday = date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        var sundaysBetween = 0
        repeat(5) {
            sundaysBetween++
            val number = ordinaryTimeSundayNumber(sunday, easter, ashWednesday, season(sunday, easter))
            if (number != null) return number - sundaysBetween
            sunday = sunday.plusWeeks(1)
        }
        return null
    }

    private fun season(date: LocalDate, easter: LocalDate): Season {
        val ashWednesday = easter.minusDays(46)
        return seasonOf(date, easter, ashWednesday, easter.plusDays(49), baptismOfTheLord(date.year))
    }

    private fun weeksBetween(start: LocalDate, date: LocalDate): Int =
        ChronoUnit.WEEKS.between(start, date).toInt()

    /** Sundays in (start, date] — for weekdays following a Sunday of the season. */
    private fun sundaysSince(start: LocalDate, date: LocalDate): Int {
        val firstSunday = start.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        if (date < firstSunday) return 0
        return weeksBetween(firstSunday, date) + 1
    }

    fun easterSunday(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day)
    }

    /** The First Sunday of Advent: the Sunday closest to 30 November. */
    fun firstAdventSunday(year: Int): LocalDate {
        val november30 = LocalDate.of(year, 11, 30)
        val before = november30.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val after = november30.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
        return if (before == november30 ||
            ChronoUnit.DAYS.between(before, november30) <= ChronoUnit.DAYS.between(november30, after)
        ) before else after
    }

    /** The Baptism of the Lord: the Sunday after the Epiphany. */
    private fun baptismOfTheLord(year: Int): LocalDate =
        epiphanySunday(year).plusDays(7)

    /** England and Wales: the Epiphany is transferred to the Sunday between 2 and 8 January. */
    private fun epiphanySunday(year: Int): LocalDate =
        LocalDate.of(year, 1, 2).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    /** Easter-based solemnities, keyed by the number of days after Easter. */
    private val movableSolemnities: Map<Long, String> = mapOf(
        0L to "Easter Sunday",
        // England and Wales: the Ascension is kept on the following Sunday.
        42L to "The Ascension of the Lord",
        49L to "Pentecost",
        56L to "The Most Holy Trinity",
        // England and Wales: Corpus Christi is kept on the Sunday after Trinity,
        // and the Sacred Heart on the Friday after that Sunday.
        63L to "The Most Holy Body and Blood of Christ",
        68L to "The Most Sacred Heart of Jesus",
    )

    /** Solemnities with fixed dates that are transferred to a Sunday in England and Wales. */
    private val transferableHolydays: Map<Pair<Int, Int>, String> = mapOf(
        6 to 29 to "Saints Peter and Paul",
        8 to 15 to "The Assumption of the Blessed Virgin Mary",
        11 to 1 to "All Saints",
    )

    private val fixedSolemnities: Map<Pair<Int, Int>, String> = mapOf(
        1 to 1 to "Mary, the Mother of God",
        // Patron of England: solemnity in England, feast in Wales.
        4 to 23 to "Saint George",
        3 to 19 to "Saint Joseph",
        12 to 8 to "The Immaculate Conception of the Blessed Virgin Mary",
        12 to 25 to "The Nativity of the Lord",
    )

    companion object {
        private val ORDINALS = listOf(
            "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth",
            "Eleventh", "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth", "Sixteenth", "Seventeenth",
            "Eighteenth", "Nineteenth", "Twentieth", "Twenty-First", "Twenty-Second", "Twenty-Third",
            "Twenty-Fourth", "Twenty-Fifth", "Twenty-Sixth", "Twenty-Seventh", "Twenty-Eighth",
            "Twenty-Ninth", "Thirtieth", "Thirty-First", "Thirty-Second", "Thirty-Third", "Thirty-Fourth",
        )

        fun ordinal(n: Int): String =
            if (n in 1..ORDINALS.size) ORDINALS[n - 1] else n.toString()
    }
}
