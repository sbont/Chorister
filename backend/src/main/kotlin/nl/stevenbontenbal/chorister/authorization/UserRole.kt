package nl.stevenbontenbal.chorister.authorization

import nl.stevenbontenbal.chorister.domain.users.AccessLevel

interface UserRole {
    val name: String

    fun allInHierarchy(): List<UserRole>

    companion object {
        const val SPRING_ROLE_PREFIX = "ROLE_"
        const val ADMIN_ROLE_PREFIX = "admin"
        const val TENANT_ROLE_PREFIX = "tenant"

        fun parse(value: String): UserRole {
            val parts = value.split('.')
            return when (parts.first()) {
                ADMIN_ROLE_PREFIX -> Admin
                TENANT_ROLE_PREFIX -> parseTenantUser(parts.drop(1))
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

object Admin : UserRole {
    override val name: String = UserRole.SPRING_ROLE_PREFIX + "ADMIN"
    override fun allInHierarchy(): List<UserRole> = listOf(Admin)
}

data class TenantUser(
    val tenantId: Long,
    val accessLevel: AccessLevel
) : UserRole {
    override val name = UserRole.SPRING_ROLE_PREFIX + accessLevel.name.uppercase()

    override fun allInHierarchy(): List<UserRole> {
        val levels = AccessLevel.entries
        return levels.takeLast(levels.size - accessLevel.ordinal).map { TenantUser(tenantId, it) }
    }
}
