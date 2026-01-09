package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelGrantsSearchRequest(
    var queries: List<GrantsQuery>
) {
    companion object {
        fun userIdQuery(userId: String): ZitadelGrantsSearchRequest {
            return ZitadelGrantsSearchRequest(
                listOf(
                    GrantsQuery(
                        UserIdQuery(userId)
                    )
                )
            )
        }
    }
}

data class GrantsQuery(
    var userIdQuery: UserIdQuery
)

data class UserIdQuery(
    var userId: String
)