package nl.stevenbontenbal.chorister.domain.rites

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MassPartTests {
    @Test
    fun `labels resolve for the exact language tag`() {
        MassPart.READING_1.label("en") shouldBe "First reading"
        MassPart.READING_2.label("nl") shouldBe "Tweede lezing"
    }

    @Test
    fun `labels resolve via the primary subtag for regional tags`() {
        MassPart.ENTRANCE.label("en-GB") shouldBe "Entrance hymn"
        MassPart.RECESSIONAL.label("nl-NL") shouldBe "Slotlied"
    }

    @Test
    fun `labels fall back to the enum name for unknown languages`() {
        MassPart.GOSPEL.label("xx") shouldBe "GOSPEL"
    }

    @Test
    fun `every part declares a label for at least en and nl`() {
        MassPart.entries.forEach { part ->
            part.label("en") shouldBe part.label("en")
            part.label("nl").isBlank() shouldBe false
        }
    }
}
