package nl.stevenbontenbal.chorister.domain.users

import nl.stevenbontenbal.chorister.authorization.UserRole
import nl.stevenbontenbal.chorister.domain.InvalidIdentifierException
import org.springframework.stereotype.Component

@Component
class UserService(private val userRepository: IUserRepository, private val authService: IUserAuthorizationService) {
    val currentUser: User by lazy {
        val userId = authService.getExternalUserId()
        userRepository.findByZitadelId(userId) ?: throw InvalidIdentifierException("User with external ID $userId not found.")
    }

    fun getCurrentChoirId(): Long? {
        return authService.getTenantId()
    }

    fun getUserRoles() = authService.getRoles()

    fun addUserToChoir(user: User, choir: Choir, accessLevel: AccessLevel = AccessLevel.VIEWER) {
        user.choir = choir
        userRepository.save(user)
        val zitadelId = user.zitadelId ?: throw InvalidIdentifierException("Zitadel user ID unknown.")
        authService.addRoleToUser(zitadelId, choir.id!!, accessLevel)
    }

    fun setUserEmail(user: User) {
        val userId = authService.getExternalUserId()
        val newEmail = user.email
        if (newEmail != null) {
            authService.setEmailAddress(userId, newEmail)
        }
    }

    fun hasAccess(targetDomainObject: Any): Boolean {
        return when (targetDomainObject) {
            is ChoirOwnedEntity -> currentUser.choir != null && currentUser.choir!!.id == targetDomainObject.choir?.id
            is Choir -> currentUser.choir != null && currentUser.choir!!.id == targetDomainObject.id
            is User -> currentUser == targetDomainObject || currentUser.choir != null && currentUser.choir!!.id == targetDomainObject.choir?.id
            else -> false
        }
    }

    fun getUsersByChoirId(choirId: Long): List<User> {
        return userRepository.findByChoirId(choirId)
    }

    fun retrieveRolesByExternalUserId(tenantId: Long): Map<String, Set<UserRole>> {
        return authService.retrieveUserRoles(tenantId)
    }
}