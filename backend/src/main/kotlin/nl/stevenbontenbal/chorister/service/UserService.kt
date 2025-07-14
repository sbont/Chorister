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
import java.nio.charset.Charset
import java.util.*

@Component
@Lazy
@DependsOn("accessPermissionEvaluator")
class UserService(private val userRepository: UserRepository, private val zitadelUserService: ZitadelUserService) {
    fun getCurrentUser(): User {
        val userId = getZitadelUserId()
        return userRepository.findByZitadelId(userId) ?: throw AuthException("User with Zitadel ID $userId not found.")
    }

    fun getCurrentChoirId(): Long? {
        val value = getMetadata("org_id")
        return value?.toLong()
    }

    private fun getZitadelUserId(): ZitadelUserId {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        val userId = jwt.subject
        return userId ?: throw AuthException("Zitadel user ID unknown.")
    }

    private fun getMetadata(key: String): String? {
        val jwt = SecurityContextHolder.getContext().authentication.principal as Jwt
        val metadataClaim = jwt.getClaim<Map<String, String>>("urn:zitadel:iam:user:metadata")
        return metadataClaim?.get(key)?.let {
            Base64.getDecoder().decode(it).toString(Charset.defaultCharset())
        }
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
