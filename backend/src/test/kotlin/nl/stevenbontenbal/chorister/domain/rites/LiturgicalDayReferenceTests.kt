package nl.stevenbontenbal.chorister.domain.rites

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LiturgicalDayReferenceTests {
    @Test
    fun `formats a day reference as calendar key plus day key`() {
        val reference = LiturgicalDayReference(LiturgicalCalendarRegion.ENGLAND_WALES, "sunday:year-c:ordinary-time:23")

        reference.value shouldBe "england-wales:sunday:year-c:ordinary-time:23"
        reference.toString() shouldBe reference.value
    }

    @Test
    fun `parses a valid day reference`() {
        val reference = LiturgicalDayReference.parse("england-wales:day:all-saints")

        reference shouldBe LiturgicalDayReference(LiturgicalCalendarRegion.ENGLAND_WALES, "day:all-saints")
    }

    @Test
    fun `round trips through value and parse`() {
        val original = LiturgicalDayReference(LiturgicalCalendarRegion.ENGLAND_WALES, "weekday:lent:1:2")

        LiturgicalDayReference.parse(original.value) shouldBe original
    }

    @Test
    fun `returns null for an unknown calendar`() {
        LiturgicalDayReference.parse("rome:day:all-saints") shouldBe null
    }

    @Test
    fun `returns null for a reference without a day key`() {
        LiturgicalDayReference.parse("england-wales") shouldBe null
        LiturgicalDayReference.parse("england-wales:") shouldBe null
    }
}
