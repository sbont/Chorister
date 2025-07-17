package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.exceptions.AuthException
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.repository.UserRepository
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
@Lazy
@DependsOn("accessPermissionEvaluator")
class UserService(private val userRepository: UserRepository, private val zitadelService: ZitadelService) {
    fun getCurrentUser(): User {
        val userId = zitadelService.getExternalUserId()
        return userRepository.findByZitadelId(userId) ?: throw AuthException("User with external ID $userId not found.")
    }

    fun getCurrentChoirId(): Long? {
        return zitadelService.getTenant()
    }

    fun addUserToChoir(user: User, choir: Choir) {
        user.choir = choir
        userRepository.save(user)
        val zitadelId = user.zitadelId ?: throw AuthException("Zitadel user ID unknown.")
        zitadelService.addRoleToUser(zitadelId, choir.id!!)
    }

    fun setUserEmail(user: User) {
        val userId = zitadelService.getExternalUserId()
        val newEmail = user.email
        if (newEmail != null) {
            zitadelService.setEmailAddress(userId, newEmail)
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
