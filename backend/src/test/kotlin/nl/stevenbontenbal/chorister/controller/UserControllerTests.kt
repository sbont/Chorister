package nl.stevenbontenbal.chorister.controller

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserControllerTests {
    private val userService: UserService = mockk()
    private val mockCurrentUser = User.create()
    private val target = UserController(userService)

    @BeforeEach
    fun init() {
        clearAllMocks()
        every { userService.getCurrentUser() } returns mockCurrentUser
    }

    @Test
    fun `when getCurrentUser then return current user`() {
        val actual = target.getCurrentUser()

        actual shouldBe mockCurrentUser
    }
}