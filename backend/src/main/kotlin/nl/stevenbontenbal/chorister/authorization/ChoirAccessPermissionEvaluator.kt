package nl.stevenbontenbal.chorister.authorization

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import java.io.Serializable
import java.util.*


class ChoirAccessPermissionEvaluator(
    private val userService: UserService
) : PermissionEvaluator {

    override fun hasPermission(
        auth: Authentication?, targetDomainObject: Any?, permission: Any
    ): Boolean {
        if (auth == null || targetDomainObject == null || permission !is String) {
            return false
        }
        val targetType = targetDomainObject.javaClass.simpleName.uppercase(Locale.getDefault())
        return hasPrivilege(targetDomainObject, permission.toString().uppercase(Locale.getDefault()))
    }

    override fun hasPermission(
        auth: Authentication?, targetId: Serializable?, targetType: String?, permission: Any
    ): Boolean {
        return auth != null && targetType != null && permission is String &&
             hasPrivilege(
            targetType, permission.toString().uppercase(Locale.getDefault()))
    }

    private fun hasPrivilege(targetDomainObject: Any?, permission: String): Boolean {
        val currentUser = userService.getCurrentUser()
        return targetDomainObject is ChoirOwnedEntity
                && (currentUser.choir != null && currentUser.choir == targetDomainObject.choir)
    }
}