package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelProjectRolesBulkPostRequest(
    val roles: List<Role>
)

data class Role(
    val key:  String,
    val displayName: String,
    val group: String,
)
