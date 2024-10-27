package nl.stevenbontenbal.chorister.authorization

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.repository.UserRepository
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

@Component("accessPermissionEvaluator")
class AccessPermissionEvaluator(@Lazy userService: UserService) : PermissionEvaluator {

    private lateinit var userService: UserService

    init {
        this.userService = userService
    }

    override fun hasPermission(
        auth: Authentication?, targetDomainObject: Any?, permission: Any
    ): Boolean {
        if (auth == null || targetDomainObject == null || permission !is String) {
            return false
        }
        return hasPrivilege(targetDomainObject, permission.toString().uppercase(Locale.getDefault()))
    }

    override fun hasPermission(
        auth: Authentication?, targetId: Serializable?, targetType: String?, permission: Any
    ): Boolean {
        return auth != null && targetType != null && permission is String &&
             hasPrivilege(
            targetType, permission.toString().uppercase(Locale.getDefault()))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun hasPrivilege(targetDomainObject: Any?, permission: String): Boolean =
        targetDomainObject?.let { userService.hasAccess(it) } ?: false

}
