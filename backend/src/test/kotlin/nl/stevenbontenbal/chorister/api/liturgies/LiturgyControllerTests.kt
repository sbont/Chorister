package nl.stevenbontenbal.chorister.api.liturgies

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import nl.stevenbontenbal.chorister.application.liturgy.LiturgyComposer
import nl.stevenbontenbal.chorister.domain.rites.LiturgicalDayInfo
import nl.stevenbontenbal.chorister.domain.rites.Liturgy
import nl.stevenbontenbal.chorister.domain.rites.LiturgyItem
import nl.stevenbontenbal.chorister.domain.rites.MassPart
import nl.stevenbontenbal.chorister.shared.Failure
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDate

class LiturgyControllerTests {
    private val liturgyComposer: LiturgyComposer = mockk()
    private val controller = LiturgyController(liturgyComposer)

    @Test
    fun `returns the composed liturgy for a date`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        val liturgy = Liturgy(
            date = date,
            languageTag = "en",
            dayInfo = LiturgicalDayInfo(name = "All Saints"),
            items = listOf(LiturgyItem(part = MassPart.ENTRANCE)),
        )
        coEvery { liturgyComposer.getLiturgy(date, "en") } returns Either.Right(liturgy)

        val response = controller.getLiturgy("2026-11-01", null)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe liturgy
    }

    @Test
    fun `passes the requested language to the composer`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        val liturgy = Liturgy(
            date = date,
            languageTag = "nl",
            dayInfo = LiturgicalDayInfo(name = "Allerheiligen"),
            items = emptyList(),
        )
        coEvery { liturgyComposer.getLiturgy(date, "nl") } returns Either.Right(liturgy)

        val response = controller.getLiturgy("2026-11-01", "nl")

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe liturgy
    }

    @Test
    fun `rejects an invalid date`() = runTest {
        val response = controller.getLiturgy("01-11-2026", null)

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
    }

    @Test
    fun `maps provider failures to a client error`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { liturgyComposer.getLiturgy(date, "en") } returns Either.Left(Failure.DateNotInNearFuture)

        val response = controller.getLiturgy("2026-11-01", null)

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
    }

    @Test
    fun `maps unexpected failures to a server error`() = runTest {
        val date = LocalDate.of(2026, 11, 1)
        coEvery { liturgyComposer.getLiturgy(date, "en") } returns Either.Left(Failure.Unexpected("boom"))

        val response = controller.getLiturgy("2026-11-01", null)

        response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
    }
}
