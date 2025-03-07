package nl.stevenbontenbal.chorister.authorization

import io.mockk.every
import io.mockk.mockk
import nl.stevenbontenbal.chorister.service.UserService
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(result).isFalse
    }

    @Test
    fun `when targetDomainObject = null then hasPermission returns false`() {
        val auth: Authentication = mockk()
        val result = target.hasPermission(auth, null, "READ")
        assertThat(result).isFalse
    }

    @Test
    fun `when incorrect permission then hasPermission returns false`() {
        val auth: Authentication = mockk()
        val result = target.hasPermission(auth, Any(), 123)
        assertThat(result).isFalse
    }

    @Test
    fun `when valid and has permission then hasPermission returns true`() {
        val auth: Authentication = mockk()
        val targetDomainObject = Any()
        every { userService.hasAccess(targetDomainObject) } returns true

        val result = target.hasPermission(auth, targetDomainObject, "READ")
        assertThat(result).isTrue
    }

    @Test
    fun `when valid and has no permission then hasPermission returns false`() {
        val auth: Authentication = mockk()
        val targetDomainObject = Any()
        every { userService.hasAccess(targetDomainObject) } returns false

        val result = target.hasPermission(auth, targetDomainObject, "READ")
        assertThat(result).isFalse
    }
}