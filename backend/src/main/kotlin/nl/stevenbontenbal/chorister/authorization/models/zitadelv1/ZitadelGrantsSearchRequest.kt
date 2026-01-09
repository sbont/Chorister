package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

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

        fun roleKeyQuery(roleKey: String): ZitadelGrantsSearchRequest {
            return ZitadelGrantsSearchRequest(
                listOf(
                    GrantsQuery(
                        roleKeyQuery = RoleKeyQuery(
                            roleKey
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