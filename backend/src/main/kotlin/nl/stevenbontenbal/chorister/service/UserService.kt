package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.model.User
import nl.stevenbontenbal.chorister.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.lang.Exception

class UserService(private val userRepository: UserRepository) {

    fun getCurrentUser(): User {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        val username = jwt.getClaimAsString("preferred_username")
        return userRepository.findByUsername(username) ?: throw Exception("Username $username not found.")
    }
}