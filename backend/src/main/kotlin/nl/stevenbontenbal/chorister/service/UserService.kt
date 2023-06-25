package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.exceptions.AuthException
import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.repository.UserRepository
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
@Lazy
@DependsOn("accessPermissionEvaluator")
class UserService(private val userRepository: UserRepository) {

    fun getCurrentUser(): User {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        val userId = jwt.subject
        return userRepository.findByZitadelId(userId) ?: throw AuthException("User with Zitadel ID $userId not found.")
    }
}