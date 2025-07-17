package nl.stevenbontenbal.chorister.authorization

enum class AccessLevel {
    MANAGER,
    VIEWER,
}

interface UserRole {
    companion object {
        fun parse(value: String): UserRole {
            val parts = value.split('.')
            return when (parts.first()) {
                "admin" -> Admin
                "tenant" -> parseTenantUser(parts.drop(1))
                else -> throw AuthException("Role cannot be determined for user")
            }
        }

        private fun parseTenantUser(parts: List<String>): TenantUser {
            if (parts.size != 2)
                throw AuthException("Role cannot be determined for user")

            val tenantId = parts.first().toLong()
            val accessLevel = AccessLevel.valueOf(parts.last().uppercase())
            return TenantUser(tenantId, accessLevel)
        }
    }
}

object Admin : UserRole

data class TenantUser(
    val tenantId: Long,
    val accessLevel: AccessLevel
) : UserRole
