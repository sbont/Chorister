package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelProjectRolesBulkPostRequest(
    val roles: List<Role>
)

data class Role(
    val key:  String,
    val displayName: String,
    val group: String,
)
