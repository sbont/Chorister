package nl.stevenbontenbal.chorister.model.dto

data class ZitadelUserGrantsRequest(
    val projectId: String,
    val roleKeys: List<String>
)