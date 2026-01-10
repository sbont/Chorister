package nl.stevenbontenbal.chorister.api.users.models

import nl.stevenbontenbal.chorister.domain.users.AccessLevel

data class UpdateUserRolesRequest(
    val roles: List<AccessLevel>
)
