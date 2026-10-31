package nl.stevenbontenbal.chorister.domain.rites

enum class MassType {
    /** Sunday outside the Christmas octave: Creed and second reading; Gloria outside Advent and Lent. */
    SUNDAY,

    /** Solemnity: Gloria, Creed and second reading, also when falling in Advent or Lent. */
    SOLEMNITY,

    /** Weekday (feria or memorial): one reading, no Gloria, no Creed. */
    FERIA,

    /** Thursday of the Lord's Supper: Gloria and Creed. */
    HOLY_THURSDAY,

    /** Friday of the Passion of the Lord: no Gloria, no Creed, but two readings before the Passion. */
    GOOD_FRIDAY,

    /** Saturday of the Easter Vigil: Gloria at the Gospel, additional readings come from the readings provider. */
    EASTER_VIGIL,
}

enum class Season { ADVENT, CHRISTMAS, LENT, EASTER, ORDINARY_TIME }

enum class LiturgicalColor { PURPLE, WHITE, GREEN, RED }

data class LiturgicalDayInfo(
    val name: String? = null,
    val season: Season? = null,
    val color: LiturgicalColor? = null,
    val massType: MassType? = null,
) {
    val includesGloria: Boolean
        get() = when (massType) {
            MassType.SUNDAY -> season != Season.ADVENT && season != Season.LENT
            MassType.SOLEMNITY, MassType.HOLY_THURSDAY, MassType.EASTER_VIGIL -> true
            else -> false
        }

    val includesCreed: Boolean
        get() = massType in setOf(MassType.SUNDAY, MassType.SOLEMNITY, MassType.HOLY_THURSDAY)

    val includesSecondReading: Boolean
        get() = massType in setOf(
            MassType.SUNDAY, MassType.SOLEMNITY, MassType.HOLY_THURSDAY, MassType.GOOD_FRIDAY
        )
}
