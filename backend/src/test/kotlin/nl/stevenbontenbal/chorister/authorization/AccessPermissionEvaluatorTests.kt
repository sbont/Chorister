package nl.stevenbontenbal.chorister.authorization

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import nl.stevenbontenbal.chorister.service.UserService
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.security.core.Authentication
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
class AccessPermissionEvaluatorTests {
    private val userService: UserService = mockk()
    private val target = AccessPermissionEvaluator(userService)

    @Test
    fun `when auth = null then hasPermission returns false`() {
        val result = target.hasPermission(null, Any(), "READ")
        result shouldBe false
    }

    @Test
    fun `when targetDomainObject = null then hasPermission returns false`() {
        val auth: Authentication = mockk()
        val result = target.hasPermission(auth, null, "READ")
        result shouldBe false
    }

    @Test
    fun `when incorrect permission then hasPermission returns false`() {
        val auth: Authentication = mockk()
        val result = target.hasPermission(auth, Any(), 123)
        result shouldBe false
    }

    @Test
    fun `when valid and has permission then hasPermission returns true`() {
        val auth: Authentication = mockk()
        val targetDomainObject = Any()
        every { userService.hasAccess(targetDomainObject) } returns true

        val result = target.hasPermission(auth, targetDomainObject, "READ")
        result shouldBe true
    }

    @Test
    fun `when valid and has no permission then hasPermission returns false`() {
        val auth: Authentication = mockk()
        val targetDomainObject = Any()
        every { userService.hasAccess(targetDomainObject) } returns false

        val result = target.hasPermission(auth, targetDomainObject, "READ")
        result shouldBe true
    }
}