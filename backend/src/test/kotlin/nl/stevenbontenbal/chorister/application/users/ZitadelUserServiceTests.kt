package nl.stevenbontenbal.chorister.application.users

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.application.config.ZitadelProperties
import nl.stevenbontenbal.chorister.application.users.models.AcceptInviteRequest
import nl.stevenbontenbal.chorister.authorization.ZitadelService
import nl.stevenbontenbal.chorister.authorization.ZitadelV2Client
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.EmailInfo
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelResourceDetails
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUserEmailGetResponse
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUserEmailPutRequest
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUserPostResponse
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUsernamePutRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ZitadelV1Client

class ZitadelUserServiceTests {
    private val zitadelProperties: ZitadelProperties = mockk()
    private val zitadelV1Client: ZitadelV1Client = mockk(relaxed = true)
    private val zitadelV2Client: ZitadelV2Client = mockk(relaxed = true)
    private val target = ZitadelService(zitadelProperties, zitadelV1Client, zitadelV2Client)

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Test
    fun `when postUser then return user id`() {
        val request = AcceptInviteRequest(
            firstName = "John",
            lastName = "Doe",
            email = "test@example.com",
            password = "123456",
            token = "qwerty"
        )
        every { zitadelV1Client.postUser(request) } returns ZitadelUserPostResponse(userId = "789")

        val actual = target.postUser(request)

        actual.isSuccess shouldBe true
        actual.getOrNull() shouldBe "789"
    }

    @Test
    fun `when setEmailAddress with different email then update email and username`() {
        every { zitadelV1Client.getCurrentEmail("789") } returns currentEmail("oldEmail@example.com")

        val actual = target.setEmailAddress("789", "newEmail@example.com")

        actual.isSuccess shouldBe true
        actual.getOrNull() shouldBe "newEmail@example.com"
        verify { zitadelV1Client.changeEmail("789", ZitadelUserEmailPutRequest("newEmail@example.com", false)) }
        verify { zitadelV1Client.changeUserName("789", ZitadelUsernamePutRequest("newEmail@example.com")) }
    }

    @Test
    fun `when setEmailAddress with same email then no subsequent calls`() {
        every { zitadelV1Client.getCurrentEmail("789") } returns currentEmail("oldEmail@example.com")

        val actual = target.setEmailAddress("789", "oldEmail@example.com")

        actual.isSuccess shouldBe true
        actual.getOrNull() shouldBe "oldEmail@example.com"
        verify(exactly = 0) { zitadelV1Client.changeEmail(any(), any()) }
        verify(exactly = 0) { zitadelV1Client.changeUserName(any(), any()) }
    }

    private fun currentEmail(email: String): ZitadelUserEmailGetResponse =
        ZitadelUserEmailGetResponse(
            details = ZitadelResourceDetails(sequence = null, creationDate = null, changeDate = null, resourceOwner = null),
            email = EmailInfo(email = email, isEmailVerified = true)
        )
}
