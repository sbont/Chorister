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
class UserService(private val userRepository: UserRepository, private val zitadelUserService: ZitadelUserService) {
    fun getCurrentUser(): User {
        val userId = getUserId()
        return userRepository.findByZitadelId(userId) ?: throw AuthException("User with Zitadel ID $userId not found.")
    }

    private fun getUserId(): String {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        return jwt.subject
    }

    fun setUserEmail(user: User) {
        val userId = getUserId()
        val newEmail = user.email
        if (newEmail != null) {
            zitadelUserService.setEmailAddress(userId, newEmail)
        }
    }
}