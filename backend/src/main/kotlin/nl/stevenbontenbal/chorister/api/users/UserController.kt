package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.api.users.models.UpdateUserRolesRequest
import nl.stevenbontenbal.chorister.api.users.models.UserViewDto
import nl.stevenbontenbal.chorister.api.users.views.UserViews
import nl.stevenbontenbal.chorister.authorization.UserRole
import nl.stevenbontenbal.chorister.authorization.TenantUser
import nl.stevenbontenbal.chorister.domain.users.AccessLevel
import nl.stevenbontenbal.chorister.domain.users.IUserRepository
import nl.stevenbontenbal.chorister.domain.users.User
import nl.stevenbontenbal.chorister.domain.users.UserService
import nl.stevenbontenbal.chorister.shared.toOptional
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import kotlin.jvm.optionals.getOrElse

@BasePathAwareController
@ExposesResourceFor(User::class)
class UserController(
    private val userService: UserService,
    private val userRepository: IUserRepository
) {
    @GetMapping("/users/me")
    fun getCurrentUser(): ResponseEntity<User> {
        return ResponseEntity.ok(userService.retrieveCurrentUser())
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

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/users/{id}/roles")
    fun updateUserRole(@PathVariable id: Long?, @RequestBody request: UpdateUserRolesRequest): ResponseEntity<Any> {
        if (id == null)
            return ResponseEntity.badRequest().build()

        val user = userRepository.findById(id).getOrElse {
            return ResponseEntity.notFound().build()
        }

        if (user.id == userService.currentUser.id)
            return ResponseEntity.badRequest().body("You can not update your own role")

        val accessLevel = request.roles.singleOrNull().toOptional().getOrElse {
            return ResponseEntity.badRequest().body("`roles` should exactly contain one role")
        }

        userService.updateUserRole(user, accessLevel)
        return ResponseEntity.ok(Result.success(accessLevel))
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