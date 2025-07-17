package nl.stevenbontenbal.chorister.api.users

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.application.RegistrationService
import nl.stevenbontenbal.chorister.application.config.ChoristerProperties
import nl.stevenbontenbal.chorister.application.models.AcceptInviteRequest
import nl.stevenbontenbal.chorister.application.models.InviteDetail
import nl.stevenbontenbal.chorister.application.models.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.users.Choir
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RegistrationControllerTests {
    private val registrationService: RegistrationService = mockk(relaxed = true)
    private val target: RegistrationController =
        RegistrationController(registrationService, properties = ChoristerProperties.create())

    @BeforeEach
    fun init() {
        clearAllMocks()
    }

    @Nested
    inner class Register {
        @Test
        fun `when new choir it calls registrationService`() {
            val request = NewChoirRegistrationRequest("M. Choir", "email@choir.co", "!@#$%^&*()", "MyChoir")
            target.register(request)

            verify { registrationService.register(request) }
        }

        @Test
        fun `when accept invite it calls registrationService`() {
            val request = AcceptInviteRequest("M. Choir", "email@choir.co", "!@#$%^&*()", "5675756-86769-8565765")
            target.register(request)

            verify { registrationService.register(request) }
        }
    }

    @Nested
    inner class GetInviteByToken {
        @Test
        fun `it calls registrationService`() {
            val token = "5428185-524254-2542424"
            val inviteDetail = InviteDetail("email@choir.co", Choir.create(), token)
            every { registrationService.getInviteDetail(token) } returns inviteDetail

            val actual = target.getInviteByToken(token)

            actual shouldBe inviteDetail
        }
    }

    @Nested
    inner class GetInviteUrl {
        @Test
        fun `it returns an invite url`() {
            every { registrationService.generateChoirInviteToken() } returns "123456"
            val expected = "https://chorister.co/signup?invite=123456"
            val actual = target.getInviteUrl()

            actual shouldBe expected
        }
    }

    @Nested
    inner class DeleteInvite {
        @Test
        fun `it calls registrationService`() {
            target.deleteInviteToken()

            verify { registrationService.nullifyChoirInviteToken() }
        }
    }
}