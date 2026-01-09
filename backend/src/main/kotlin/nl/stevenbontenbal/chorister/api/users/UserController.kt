package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.api.users.models.UserViewDto
import nl.stevenbontenbal.chorister.api.users.views.UserViews
import nl.stevenbontenbal.chorister.authorization.UserRole
import nl.stevenbontenbal.chorister.authorization.TenantUser
import nl.stevenbontenbal.chorister.domain.users.AccessLevel
import nl.stevenbontenbal.chorister.domain.users.User
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.GetMapping

@BasePathAwareController
@ExposesResourceFor(User::class)
class UserController(
    private val userService: UserService
) {
    @GetMapping("/users/me")
    fun getCurrentUser(): ResponseEntity<User> {
        return ResponseEntity.ok(userService.currentUser)
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<MappingJacksonValue> {
        val currentTenantId = userService.getCurrentChoirId()
        val currentUserRoles = userService.getUserRoles()

        if (currentTenantId == null)
            return ResponseEntity.ok(MappingJacksonValue(emptyList<UserViewDto>()).apply { serializationView = UserViews.Basic::class.java })

        val allUsers = userService.getUsersByChoirId(currentTenantId)
        val accessLevelsMap = userService.retrieveRolesByExternalUserId(currentTenantId)

        // Convert users to DTOs with roles
        val userDtos = allUsers.map { user ->
            UserViewDto(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName,
                email = user.email,
                roles = user.zitadelId?.let { accessLevelsMap[it] }
            )
        }

        // Apply JsonView based on user role
        val viewClass = determineViewClass(currentUserRoles)
        val result = MappingJacksonValue(userDtos).apply {
            serializationView = viewClass
        }
        return ResponseEntity.ok(result)
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