package nl.stevenbontenbal.chorister.domain.rites

import java.time.LocalDate

/**
 * The order of Mass for a date, as computed by the [LiturgyComposer]. This is
 * a non-persisted value object: it is always the same for a given date and
 * language. The choir's own plan for the same date lives in an Event and its
 * entries; this object is what informs the creation of that event.
 */
data class Liturgy(
    val date: LocalDate,
    val rite: Rite = Rite.ROMAN_NOVUS_ORDO,
    val languageTag: String,
    val dayInfo: LiturgicalDayInfo,
    val items: List<LiturgyItem>,
) {
    fun label(part: MassPart): String = part.label(languageTag)
}
