package nl.stevenbontenbal.chorister.domain.rites

/**
 * The particular liturgical calendar a day belongs to. For now, only the
 * calendar of England and Wales is supported. The [key] is the first part of
 * a [LiturgicalDayReference].
 */
enum class LiturgicalCalendarRegion(val key: String) {
    ENGLAND_WALES("england-wales"),
}
