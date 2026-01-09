package nl.stevenbontenbal.chorister.authorization.models.zitadelv2

data class ListAuthorizationsRequest(
    val filters: List<Filter>
)

abstract class Filter

data class RoleKeyFilter(
    val roleKey: RoleKey
)

data class RoleKey(
    val key: String,
    val method: String = "TEXT_FILTER_METHOD_EQUALS"
)


