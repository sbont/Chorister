package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.api.users.models.UserViewDto
import nl.stevenbontenbal.chorister.api.users.views.UserViews
import nl.stevenbontenbal.chorister.authorization.UserRole
import nl.stevenbontenbal.chorister.authorization.TenantUser
import nl.stevenbontenbal.chorister.domain.users.AccessLevel
import nl.stevenbontenbal.chorister.domain.users.User
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping("/api/user")
    fun getCurrentUser(): User {
        return userService.currentUser
    }

    @GetMapping("/api/users")
    fun getUsers(): MappingJacksonValue {
        val currentTenantId = userService.getCurrentChoirId()
        val currentUserRoles = userService.getUserRoles()
        
        return if (currentTenantId != null) {
            val allUsers = userService.getUsersByChoirId(currentTenantId)
            val userRolesMap = userService.retrieveRolesByExternalUserId(currentTenantId)
            
            // Convert users to DTOs with roles
            val userDtos = allUsers.map { user ->
                UserViewDto(
                    id = user.id,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    roles = user.zitadelId?.let { zitadelId ->
                        userRolesMap[zitadelId]?.mapNotNull { role ->
                            (role as? TenantUser)?.takeIf { it.tenantId == currentTenantId }?.accessLevel
                        }?.toList()
                    }
                )
            }
            
            // Apply JsonView based on user role
            val viewClass = determineViewClass(currentUserRoles)
            MappingJacksonValue(userDtos).apply {
                serializationView = viewClass
            }
        } else {
            MappingJacksonValue(emptyList<UserViewDto>()).apply {
                serializationView = UserViews.Basic::class.java
            }
        }
    }

    /**
     * Determines which JsonView to use based on the current user's roles
     */
    private fun determineViewClass(roles: Set<UserRole>): Class<*> {
        return if (hasManagerRole(roles)) {
            UserViews.Manager::class.java
        } else {
            UserViews.Basic::class.java
        }
    }

    private fun hasManagerRole(roles: Set<UserRole>): Boolean {
        return roles.any { role ->
            role is TenantUser && role.accessLevel == AccessLevel.MANAGER
        }
    }
}