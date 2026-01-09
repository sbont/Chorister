package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelProjectRolesPostRequest(
    val roleKey:  String,
    val displayName: String,
    val group: String,
)