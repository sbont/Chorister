package nl.stevenbontenbal.chorister.api.songs

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.domain.songs.ScoreService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ScoreControllerTests {
    private val scoreService: ScoreService = mockk(relaxed = true)
    private val target: ScoreController = ScoreController(scoreService)

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Test
    fun `when put file it return 200`() {
        val actual = target.putFile(123L, 456L)

        verify { scoreService.saveFileToScore(123L, 456L) }
        actual.statusCode shouldBe HttpStatus.OK
    }

    @Test
    fun `when put file with null id it returns 400`() {
        val actual = target.putFile(null, 456L)

        verify(exactly = 0) { scoreService.saveFileToScore(any(), any()) }
        actual.statusCode shouldBe HttpStatus.BAD_REQUEST
    }

    @Test
    fun `when put file with null file id it returns 400`() {
        val actual = target.putFile(123L, null)

        verify(exactly = 0) { scoreService.saveFileToScore(any(), any()) }
        actual.statusCode shouldBe HttpStatus.BAD_REQUEST
    }

}