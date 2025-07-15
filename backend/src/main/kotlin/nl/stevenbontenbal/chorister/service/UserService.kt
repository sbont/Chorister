package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.exceptions.AuthException
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.model.entities.Choir
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
        val userId = getZitadelUserId()
        return userRepository.findByZitadelId(userId) ?: throw AuthException("User with Zitadel ID $userId not found.")
    }

    fun getCurrentChoirId(): Long? {
        val token = getAuthToken()
        return token?.let { zitadelUserService.getChoir(token) }
    }

    private fun getZitadelUserId(): ZitadelUserId {
        val jwt = getAuthToken()
        val userId = jwt?.subject
        return userId ?: throw AuthException("Zitadel user ID unknown.")
    }

    private fun getAuthToken(): Jwt? {
        return SecurityContextHolder.getContext().authentication?.principal as Jwt?
    }

    fun addUserToChoir(user: User, choir: Choir) {
        user.choir = choir
        userRepository.save(user)
        val zitadelId = user.zitadelId ?: throw AuthException("Zitadel user ID unknown.")
        zitadelUserService.setChoir(zitadelId, choir.id!!)
    }

    fun setUserEmail(user: User) {
        val userId = getZitadelUserId()
        val newEmail = user.email
        if (newEmail != null) {
            zitadelUserService.setEmailAddress(userId, newEmail)
        }
    }

    fun hasAccess(targetDomainObject: Any): Boolean {
        val currentUser = getCurrentUser()
        return when (targetDomainObject) {
            is ChoirOwnedEntity -> currentUser.choir != null && currentUser.choir!!.id == targetDomainObject.choir?.id
            is Choir -> currentUser.choir != null && currentUser.choir!!.id == targetDomainObject.id
            is User -> currentUser == targetDomainObject || currentUser.choir != null && currentUser.choir!!.id == targetDomainObject.choir?.id
            else -> false
        }
    }
}
