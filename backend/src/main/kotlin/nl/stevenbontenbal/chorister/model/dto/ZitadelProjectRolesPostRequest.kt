package nl.stevenbontenbal.chorister.model.dto

data class ZitadelProjectRolesPostRequest(
    val roleKey:  String,
    val displayName: String,
    val group: String,
)