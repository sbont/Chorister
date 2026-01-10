package nl.stevenbontenbal.chorister.authorization

import ZitadelV1Client
import nl.stevenbontenbal.chorister.application.RegistrationRequest
import nl.stevenbontenbal.chorister.application.config.ZitadelProperties
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUserEmailPutRequest
import nl.stevenbontenbal.chorister.authorization.models.zitadelv1.ZitadelUsernamePutRequest
import nl.stevenbontenbal.chorister.domain.users.AccessLevel
import nl.stevenbontenbal.chorister.domain.users.IUserAuthorizationService
import nl.stevenbontenbal.chorister.domain.users.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component


private const val RolesClaim = "urn:zitadel:iam:org:project:roles"

@Component
class ZitadelService(
    zitadelConfiguration: ZitadelProperties,
) : IUserAuthorizationService {
    private val zitadelV1Client = ZitadelV1Client(zitadelConfiguration)
    private val zitadelV2Client = ZitadelV2Client(zitadelConfiguration)

    override fun postUser(registrationRequest: RegistrationRequest): Result<String?> {
        val result = zitadelV1Client.postUser(registrationRequest)
        return Result.success(result?.userId)
    }

    override fun setEmailAddress(userId: ZitadelUserId, email: String): Result<String> {
        val result = zitadelV1Client.getCurrentEmail(userId)
        if (result?.email?.email == email)
            return Result.success(email)

        val emailRequest = ZitadelUserEmailPutRequest(email, false)
        zitadelV1Client.changeEmail(userId, emailRequest)

        val userNameRequest = ZitadelUsernamePutRequest(email)
        zitadelV1Client.changeUserName(userId, userNameRequest)

        return Result.success(email)
    }

    override fun getExternalUserId(): ZitadelUserId {
        val jwt = getAuthToken()
        val userId = jwt?.subject
        return userId ?: throw AuthException("Zitadel user ID unknown.")
    }

    override fun getTenantId(): Long? {
        val roles = getRoles()
        return roles.filterIsInstance<TenantUser>().firstOrNull()?.tenantId
    }

    private fun getAuthToken(): Jwt? = SecurityContextHolder.getContext().authentication?.principal as Jwt?

    override fun getRoles(): Set<UserRole> {
        val jwt = getAuthToken()
        return if (jwt == null) setOf() else getRolesFromJwt(jwt)
    }

    override fun getRolesFromJwt(jwt: Jwt): Set<UserRole> {
        val rolesClaim = jwt.getClaim<Map<String, String>>(RolesClaim)
        val roles = rolesClaim?.keys?.map { UserRole.parse(it) }
        return roles?.toSet() ?: setOf()
    }

    override fun createRolesForTenant(tenantId: Long) {
        zitadelV1Client.createRolesForTenant(tenantId)
    }

    private fun roleKey(tenantId: Long, accessLevel: AccessLevel): String {
        return "tenant.$tenantId.${accessLevel.name.lowercase()}"
    }

    override fun addRoleToUser(userId: ZitadelUserId, tenantId: Long, accessLevel: AccessLevel) {
        zitadelV1Client.addRoleToUser(userId, tenantId, accessLevel)
    }

    override fun retrieveUserRoles(tenantId: Long): Map<ZitadelUserId, List<AccessLevel>> {
        val pairs = AccessLevel.entries.flatMap { accessLevel ->
            val zitadelRoleName = "${UserRole.TENANT_ROLE_PREFIX}.$tenantId.${accessLevel.name.lowercase()}"
            val grants = zitadelV1Client.findUserGrants(zitadelRoleName)
            grants.map { it.userId to accessLevel }
        }
        return pairs.groupBy({ it.first }, { it.second})
    }

    override fun replaceUserRoles(user: User, accessLevel: AccessLevel) {
        val userId = user.zitadelId ?: throw AuthException("User has no Zitadel ID")
        val tenantId = user.choir?.id ?: throw AuthException("User choir ID unknown")
        val grantId = zitadelV1Client.findUserGrantId(userId)
        val roleKeys = listOf(roleKey(tenantId, accessLevel))
        zitadelV1Client.putGrantRoles(userId, grantId, roleKeys)
    }
}

typealias ZitadelUserId = String
