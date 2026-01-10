package nl.stevenbontenbal.chorister.api.users.models

import com.fasterxml.jackson.annotation.JsonView
import nl.stevenbontenbal.chorister.api.users.views.UserViews
import nl.stevenbontenbal.chorister.domain.users.AccessLevel

/**
 * Unified User DTO with JsonView annotations for role-based filtering
 */
data class UserViewDto(
    @field:JsonView(UserViews.Manager::class)
    val id: Long?,
    
    @field:JsonView(UserViews.Basic::class)
    val firstName: String?,
    
    @field:JsonView(UserViews.Basic::class)
    val lastName: String?,
    
    @field:JsonView(UserViews.Manager::class)
    val email: String?,
    
    @field:JsonView(UserViews.Basic::class)
    val roles: List<AccessLevel>?
)