package nl.stevenbontenbal.chorister.domain.users

import nl.stevenbontenbal.chorister.domain.InvalidIdentifierException
import org.springframework.stereotype.Component

@Component
class UserService(private val userRepository: IUserRepository, private val authService: IUserAuthorizationService) {
    fun getCurrentUser(): User {
        val userId = authService.getExternalUserId()
        return userRepository.findByZitadelId(userId) ?: throw InvalidIdentifierException("User with external ID $userId not found.")
    }

    fun getCurrentChoirId(): Long? {
        return authService.getTenantId()
    }

    fun addUserToChoir(user: User, choir: Choir) {
        user.choir = choir
        userRepository.save(user)
        val zitadelId = user.zitadelId ?: throw InvalidIdentifierException("Zitadel user ID unknown.")
        authService.addRoleToUser(zitadelId, choir.id!!)
    }

    fun setUserEmail(user: User) {
        val userId = authService.getExternalUserId()
        val newEmail = user.email
        if (newEmail != null) {
            authService.setEmailAddress(userId, newEmail)
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