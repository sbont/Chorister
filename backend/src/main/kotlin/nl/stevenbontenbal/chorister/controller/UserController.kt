package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.service.UserService
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