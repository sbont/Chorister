package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelGrantsSearchResponse(
    var details: SearchDetails,
    var result: List<ZitadelUserGrant>?
)

data class SearchDetails(
    var totalResult: String?,
    var viewTimestamp: String?
)
