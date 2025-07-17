package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.domain.users.User
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping("/api/user")
    fun getCurrentUser(): User {
        return userService.getCurrentUser()
    }
}