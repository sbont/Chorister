package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelAddUserGrantsRequest(
    val projectId: String,
    val roleKeys: List<String>
)

data class ZitadelPutUserGrantsRequest(
    val roleKeys: List<String>
)