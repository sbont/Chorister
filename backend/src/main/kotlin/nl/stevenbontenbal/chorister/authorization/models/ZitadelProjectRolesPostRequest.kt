package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelProjectRolesPostRequest(
    val roleKey:  String,
    val displayName: String,
    val group: String,
)