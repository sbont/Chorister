package nl.stevenbontenbal.chorister.domain.users

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import nl.stevenbontenbal.chorister.domain.InvalidIdentifierException
import nl.stevenbontenbal.chorister.shared.ChoristerError
import nl.stevenbontenbal.chorister.shared.Failure
import nl.stevenbontenbal.chorister.shared.Validation
import nl.stevenbontenbal.chorister.shared.optionalToEither
import org.springframework.stereotype.Component

@Component
class UserService(private val userRepository: IUserRepository, private val authService: IUserAuthorizationService) {
    val userExternalId: String by lazy {
        authService.getExternalUserId()
    }

    val currentUser: User by lazy { retrieveCurrentUser() }

    fun retrieveCurrentUser(): User = userRepository.findByZitadelId(userExternalId)
        ?: throw InvalidIdentifierException("User with external ID $userExternalId not found.")

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

    fun retrieveRolesByExternalUserId(tenantId: Long): Map<String, List<AccessLevel>> {
        return authService.retrieveUserRoles(tenantId)
    }

    fun updateUserRole(userId: Long, accessLevel: AccessLevel): Either<ChoristerError, Unit> {
        val user = userRepository.findById(userId).optionalToEither { Validation.NotFound }
        return either {
            val user_ = user.bind()
            if (user_.id == currentUser.id)
                Failure.InvalidOperation("You can not update your own role").left().bind()

            authService.replaceUserRoles(user_, accessLevel)
        }
    }
}