package nl.stevenbontenbal.chorister.api.users

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.users.User
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserControllerTests {
    private val userService: UserService = mockk()
    private val mockCurrentUser = User.create()
    private val target = UserController(userService)

    @BeforeEach
    fun init() {
        clearAllMocks()
        every { userService.currentUser } returns mockCurrentUser
    }

    @Test
    fun `when getCurrentUser then return current user`() {
        val actual = target.getCurrentUser()

        actual shouldBe mockCurrentUser
    }
}