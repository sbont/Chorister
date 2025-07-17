package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelUserGrantsRequest(
    val projectId: String,
    val roleKeys: List<String>
)