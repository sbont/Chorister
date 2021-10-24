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
        return hasPrivilege(auth, targetDomainObject, permission.toString().uppercase(Locale.getDefault()))
    }

    override fun hasPermission(
        auth: Authentication?, targetId: Serializable?, targetType: String?, permission: Any
    ): Boolean {
        return if (auth == null || targetType == null || permission !is String) {
            false
        } else hasPrivilege(
            auth, targetType.uppercase(Locale.getDefault()),
            permission.toString().uppercase(Locale.getDefault())
        )
    }

    private fun hasPrivilege(auth: Authentication, targetDomainObject: Any?, permission: String): Boolean {
        val currentUser = userService.getCurrentUser()
        if (targetDomainObject is ChoirOwnedEntity) {
            return if (targetDomainObject.choir != null) {
                if (currentUser.choir != null) {
                    targetDomainObject.choir!!.id == currentUser.choir!!.id
                } else {
                    true
                }
            } else {
                true
            }
        }
        return false
    }
}