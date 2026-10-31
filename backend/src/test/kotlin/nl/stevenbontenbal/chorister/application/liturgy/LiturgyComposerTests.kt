package nl.stevenbontenbal.chorister.application.liturgy

import arrow.core.Either
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nl.stevenbontenbal.chorister.application.readings.Copyright
import nl.stevenbontenbal.chorister.application.readings.IReadingsProvider
import nl.stevenbontenbal.chorister.application.readings.Reading
import nl.stevenbontenbal.chorister.application.readings.Readings
import nl.stevenbontenbal.chorister.domain.rites.LiturgicalCalendar
import nl.stevenbontenbal.chorister.domain.rites.MassPart
import nl.stevenbontenbal.chorister.shared.Failure
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LiturgyComposerTests {
    private val readingsProvider: IReadingsProvider = mockk()
    private val composer = LiturgyComposer(readingsProvider, LiturgicalCalendar())

    private fun readings(
        reading2: Reading? = null,
        reading3: Reading? = null,
        psalm3: Reading? = null,
        reading4: Reading? = null,
        psalm4: Reading? = null,
    ) = Readings(
        date = "20261101",
        day = "unused",
        reading1 = Reading(source = "Revelation 7:2-4", text = "I, John, saw another angel ascending from the rising of the sun."),
        psalm = Reading(source = "Psalm 23", text = "The Lord is my shepherd; there is nothing I shall want."),
        reading2 = reading2,
        gospelAcclamation = Reading(source = "cf. Matthew 11:25", text = "Alleluia, alleluia!"),
        gospelReading = Reading(source = "Matthew 5:1-12", text = "Blessed are the poor in spirit, for theirs is the kingdom of heaven."),
        psalm2 = null,
        reading3 = reading3, psalm3 = psalm3,
        reading4 = reading4, psalm4 = psalm4,
        reading5 = null, psalm5 = null,
        reading6 = null, psalm6 = null,
        reading7 = null, psalm7 = null,
        reading8 = null, psalm8 = null,
        copyright = Copyright("Copyright text"),
    )

    @Test
    fun `composes a Sunday liturgy with Gloria, Creed and second reading`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(
            readings(reading2 = Reading(source = "1 John 3:1-2", text = "See what kind of love the Father has given us."))
        )

        val liturgy = (composer.getLiturgy(date, "en") as Either.Right).value

        val parts = liturgy.items.map { it.part }
        parts shouldBe parts.sortedBy { it.order }
        parts shouldContainAll listOf(
            MassPart.ENTRANCE, MassPart.KYRIE, MassPart.GLORIA, MassPart.COLLECT,
            MassPart.READING_1, MassPart.PSALM_1, MassPart.READING_2,
            MassPart.GOSPEL_ACCLAMATION, MassPart.GOSPEL, MassPart.CREED,
            MassPart.SANCTUS, MassPart.AGNUS_DEI, MassPart.COMMUNION, MassPart.RECESSIONAL,
        )
        liturgy.dayInfo.name shouldBe "All Saints"
        liturgy.dayInfo.massType shouldBe nl.stevenbontenbal.chorister.domain.rites.MassType.SOLEMNITY
        liturgy.dayReference shouldBe "england-wales:day:all-saints"
    }

    @Test
    fun `composes a Lent weekday without Gloria, Creed or second reading`() = runTest {
        val date = LocalDate.of(2026, 2, 24)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(readings())

        val liturgy = (composer.getLiturgy(date, "en") as Either.Right).value

        val parts = liturgy.items.map { it.part }
        (MassPart.GLORIA in parts) shouldBe false
        (MassPart.CREED in parts) shouldBe false
        (MassPart.READING_2 in parts) shouldBe false
        parts shouldContainAll listOf(
            MassPart.READING_1, MassPart.PSALM_1, MassPart.GOSPEL_ACCLAMATION, MassPart.GOSPEL,
            MassPart.SANCTUS, MassPart.PATER_NOSTER,
        )
        liturgy.dayInfo.name shouldBe "Tuesday of the First Week of Lent"
        liturgy.dayReference shouldBe "england-wales:weekday:lent:1:2"
    }

    @Test
    fun `composes the Easter vigil with the additional readings from the provider`() = runTest {
        val date = LocalDate.of(2026, 4, 4)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(
            readings(
                reading3 = Reading(source = "Exodus 14:15-15:1", text = "The Lord said to Moses..."),
                psalm3 = Reading(source = "Exodus 15", text = "I will sing to the Lord..."),
                reading4 = Reading(source = "Ezekiel 36:16-28", text = "The word of the Lord came to me..."),
            )
        )

        val liturgy = (composer.getLiturgy(date, "en") as Either.Right).value

        val parts = liturgy.items.map { it.part }
        parts shouldContainAll listOf(MassPart.READING_3, MassPart.PSALM_3, MassPart.READING_4)
        (MassPart.PSALM_4 in parts) shouldBe false
        (MassPart.CREED in parts) shouldBe false
        liturgy.dayInfo.massType shouldBe nl.stevenbontenbal.chorister.domain.rites.MassType.EASTER_VIGIL
    }

    @Test
    fun `fills readings with reference and text`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(readings())

        val liturgy = (composer.getLiturgy(date, "en") as Either.Right).value

        val reading1 = liturgy.items.first { it.part == MassPart.READING_1 }
        reading1.reference shouldBe "Revelation 7:2-4"
        reading1.text shouldStartWith "I, John, saw another angel"
        val gospel = liturgy.items.first { it.part == MassPart.GOSPEL }
        gospel.reference shouldBe "Matthew 5:1-12"
        gospel.text shouldStartWith "Blessed are the poor in spirit"
    }

    @Test
    fun `fills ordinary parts from the catalog with translation provenance`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(readings())

        val liturgy = (composer.getLiturgy(date, "en") as Either.Right).value

        val kyrie = liturgy.items.first { it.part == MassPart.KYRIE }
        kyrie.text shouldStartWith "Kyrie eleison"
        kyrie.translation shouldBe "la"
        val gloria = liturgy.items.first { it.part == MassPart.GLORIA }
        gloria.text shouldStartWith "Gloria in excelsis Deo"
    }

    @Test
    fun `leaves song slots empty`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(readings())

        val liturgy = (composer.getLiturgy(date, "en") as Either.Right).value

        val entrance = liturgy.items.first { it.part == MassPart.ENTRANCE }
        entrance.text shouldBe null
        entrance.reference shouldBe null
        entrance.translation shouldBe null
    }

    @Test
    fun `labels parts in the requested language`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { readingsProvider.getReadings(date) } returns Either.Right(readings())

        val liturgy = (composer.getLiturgy(date, "nl") as Either.Right).value

        liturgy.label(MassPart.ENTRANCE) shouldBe "Intredelied"
        liturgy.label(MassPart.RECESSIONAL) shouldBe "Slotlied"
    }

    @Test
    fun `propagates provider failures`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { readingsProvider.getReadings(date) } returns Either.Left(Failure.DateNotInNearFuture)

        val result = composer.getLiturgy(date, "en")

        (result as Either.Left).value shouldBe Failure.DateNotInNearFuture
    }
}
