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

        fun roleKeyQuery(substring: String): ZitadelGrantsSearchRequest {
            return ZitadelGrantsSearchRequest(
                listOf(
                    GrantsQuery(
                        roleKeyQuery = RoleKeyQuery(
                            roleKey = substring,
                            method = "TEXT_QUERY_METHOD_CONTAINS_IGNORE_CASE"
                        )
                    )
                )
            )
        }
    }
}

data class GrantsQuery(
    var userIdQuery: UserIdQuery? = null,
    var roleKeyQuery: RoleKeyQuery? = null
)

data class UserIdQuery(
    var userId: String
)

data class RoleKeyQuery(
    var roleKey: String,
    var method: String = "TEXT_QUERY_METHOD_EQUALS"
)