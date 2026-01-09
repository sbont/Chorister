package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelAddUserGrantsRequest(
    val projectId: String,
    val roleKeys: List<String>
)

data class ZitadelPutUserGrantsRequest(
    val roleKeys: List<String>
)