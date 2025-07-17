package nl.stevenbontenbal.chorister.api.songs

import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.test.runTest
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.songs.File
import nl.stevenbontenbal.chorister.application.FileService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class FileControllerTests {
    private val fileService: FileService = mockk()
    private val mockFile = File(id = 123L, s3Key = "123", choir = Choir.create())
    private val target: FileController = FileController(fileService)

    @BeforeEach
    fun init() {
        clearAllMocks()
        every { fileService.createFile() } returns mockFile
        every { fileService.getFile(123L) } returns mockFile
        coEvery { fileService.getUploadUrl(mockFile) } returns "https://upload/123"
        coEvery { fileService.getDownloadUrl("123") } returns "https://download/123"
    }

    @Nested
    inner class GetUploadUrl {
        @Test
        fun `when no id then it returns 200`() = runTest {
            val response = target.getUploadUrl()

            response.statusCode shouldBe HttpStatus.OK
            response.body?.uploadUrl shouldBe "https://upload/123"
            coVerify { fileService.getUploadUrl(mockFile) }
        }

        @Test
        fun `when file id then it returns 200`() = runTest {
            val response = target.getUploadUrl(123L)

            response.statusCode shouldBe HttpStatus.OK
            response.body?.uploadUrl shouldBe "https://upload/123"
            coVerify { fileService.getUploadUrl(mockFile) }
        }

        @Test
        fun `when non-existing file id then it returns 404`() = runTest {
            every { fileService.getFile(567L) } returns null

            val response = target.getUploadUrl(567L)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }

    @Nested
    inner class GetFile {
        @Test
        fun `when file id then it returns 201`() = runTest {
            val response = target.getFile(123L)

            response.statusCode shouldBe HttpStatus.CREATED
            response.headers["Location"]?.first() shouldBe "https://download/123"
            coVerify { fileService.getDownloadUrl("123") }
        }

        @Test
        fun `when non-existing file id then it returns 404`() = runTest {
            every { fileService.getFile(567L) } returns null

            val response = target.getFile(567L)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
        }

        @Test
        fun `when corrupted file then it returns 500`() = runTest {
            val noKey = File(id = 234L, choir = Choir.create())
            every { fileService.getFile(234L) } returns noKey

            val response = target.getFile(234L)

            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
            response.body shouldBe "File key not found"
        }
    }
}