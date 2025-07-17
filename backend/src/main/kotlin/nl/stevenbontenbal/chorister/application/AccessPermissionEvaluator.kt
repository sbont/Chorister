package nl.stevenbontenbal.chorister.application

import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.context.annotation.Lazy
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.Locale

@Component("accessPermissionEvaluator")
class AccessPermissionEvaluator(@Lazy private var userService: UserService) : PermissionEvaluator {

    override fun hasPermission(auth: Authentication?, targetDomainObject: Any?, permission: Any): Boolean {
        if (auth == null || targetDomainObject == null || permission !is String)
            return false

        return hasPrivilege(targetDomainObject, permission.toString().uppercase(Locale.getDefault()))
    }

    override fun hasPermission(
        auth: Authentication?,
        targetId: Serializable?,
        targetType: String?,
        permission: Any
    ): Boolean {
        if (auth == null || targetType == null || permission !is String)
            return false

        // This uses authorities, which we don't use in our setup
        val authority = "${targetType}_${targetId}_${permission}"
        return auth.authorities.any { it.authority == authority }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun hasPrivilege(targetDomainObject: Any?, permission: String): Boolean =
        targetDomainObject?.let { userService.hasAccess(it) } ?: false
}