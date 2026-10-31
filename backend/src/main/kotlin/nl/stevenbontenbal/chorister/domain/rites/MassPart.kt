package nl.stevenbontenbal.chorister.domain.rites

enum class ContentKind { SONG, ORDINARY_TEXT, PROPER_TEXT, READING }

enum class MassPart(
    val contentKind: ContentKind,
    val order: Int,
    val sungByDefault: Boolean = false,
) {
    ENTRANCE(ContentKind.SONG, 10, sungByDefault = true),
    PENITENTIAL_ACT(ContentKind.PROPER_TEXT, 20),
    KYRIE(ContentKind.ORDINARY_TEXT, 30),
    GLORIA(ContentKind.ORDINARY_TEXT, 40),
    COLLECT(ContentKind.PROPER_TEXT, 50),
    READING_1(ContentKind.READING, 60),
    PSALM_1(ContentKind.READING, 70),
    READING_2(ContentKind.READING, 80),
    PSALM_2(ContentKind.READING, 90),
    READING_3(ContentKind.READING, 100),
    PSALM_3(ContentKind.READING, 110),
    READING_4(ContentKind.READING, 120),
    PSALM_4(ContentKind.READING, 130),
    READING_5(ContentKind.READING, 140),
    PSALM_5(ContentKind.READING, 150),
    READING_6(ContentKind.READING, 160),
    PSALM_6(ContentKind.READING, 170),
    READING_7(ContentKind.READING, 180),
    PSALM_7(ContentKind.READING, 190),
    READING_8(ContentKind.READING, 200),
    PSALM_8(ContentKind.READING, 210),
    GOSPEL_ACCLAMATION(ContentKind.READING, 220),
    GOSPEL(ContentKind.READING, 230),
    CREED(ContentKind.ORDINARY_TEXT, 240),
    UNIVERSAL_PRAYER(ContentKind.PROPER_TEXT, 250),
    OFFERTORY(ContentKind.SONG, 260, sungByDefault = true),
    SANCTUS(ContentKind.ORDINARY_TEXT, 270),
    MEMORIAL_ACCLAMATION(ContentKind.ORDINARY_TEXT, 280),
    PATER_NOSTER(ContentKind.ORDINARY_TEXT, 290),
    AGNUS_DEI(ContentKind.ORDINARY_TEXT, 300),
    COMMUNION(ContentKind.SONG, 310, sungByDefault = true),
    RECESSIONAL(ContentKind.SONG, 320, sungByDefault = true);

    fun label(languageTag: String): String {
        val labels = LABELS[this] ?: emptyMap()
        return labels[languageTag.lowercase()]
            ?: labels[languageTag.lowercase().substringBefore('-')]
            ?: name
    }

    companion object {
        private val LABELS: Map<MassPart, Map<String, String>> = mapOf(
            ENTRANCE to mapOf("en" to "Entrance hymn", "nl" to "Intredelied"),
            PENITENTIAL_ACT to mapOf("en" to "Penitential act", "nl" to "Schuldbelijdenis"),
            KYRIE to mapOf("en" to "Kyrie", "nl" to "Kyrie"),
            GLORIA to mapOf("en" to "Gloria", "nl" to "Gloria"),
            COLLECT to mapOf("en" to "Collect", "nl" to "Openingsgebed"),
            READING_1 to mapOf("en" to "First reading", "nl" to "Eerste lezing"),
            PSALM_1 to mapOf("en" to "Responsorial psalm", "nl" to "Psalm"),
            READING_2 to mapOf("en" to "Second reading", "nl" to "Tweede lezing"),
            PSALM_2 to mapOf("en" to "Second psalm", "nl" to "Tweede psalm"),
            READING_3 to mapOf("en" to "Third reading", "nl" to "Derde lezing"),
            PSALM_3 to mapOf("en" to "Third psalm", "nl" to "Derde psalm"),
            READING_4 to mapOf("en" to "Fourth reading", "nl" to "Vierde lezing"),
            PSALM_4 to mapOf("en" to "Fourth psalm", "nl" to "Vierde psalm"),
            READING_5 to mapOf("en" to "Fifth reading", "nl" to "Vijfde lezing"),
            PSALM_5 to mapOf("en" to "Fifth psalm", "nl" to "Vijfde psalm"),
            READING_6 to mapOf("en" to "Sixth reading", "nl" to "Zesde lezing"),
            PSALM_6 to mapOf("en" to "Sixth psalm", "nl" to "Zesde psalm"),
            READING_7 to mapOf("en" to "Seventh reading", "nl" to "Zevende lezing"),
            PSALM_7 to mapOf("en" to "Seventh psalm", "nl" to "Zevende psalm"),
            READING_8 to mapOf("en" to "Eighth reading", "nl" to "Achtste lezing"),
            PSALM_8 to mapOf("en" to "Eighth psalm", "nl" to "Achtste psalm"),
            GOSPEL_ACCLAMATION to mapOf("en" to "Gospel acclamation", "nl" to "Acclamatie voor het evangelie"),
            GOSPEL to mapOf("en" to "Gospel", "nl" to "Evangelie"),
            CREED to mapOf("en" to "Creed", "nl" to "Geloofsbelijdenis"),
            UNIVERSAL_PRAYER to mapOf("en" to "Universal prayer", "nl" to "Voorbede"),
            OFFERTORY to mapOf("en" to "Offertory", "nl" to "Offertorium"),
            SANCTUS to mapOf("en" to "Sanctus", "nl" to "Sanctus"),
            MEMORIAL_ACCLAMATION to mapOf("en" to "Memorial acclamation", "nl" to "Mysterium fidei"),
            PATER_NOSTER to mapOf("en" to "Our Father", "nl" to "Onze Vader"),
            AGNUS_DEI to mapOf("en" to "Agnus Dei", "nl" to "Agnus Dei"),
            COMMUNION to mapOf("en" to "Communion hymn", "nl" to "Communielied"),
            RECESSIONAL to mapOf("en" to "Recessional hymn", "nl" to "Slotlied"),
        )
    }
}
