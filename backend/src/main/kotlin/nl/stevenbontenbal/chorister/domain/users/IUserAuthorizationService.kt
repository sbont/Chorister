package nl.stevenbontenbal.chorister.domain.users

import nl.stevenbontenbal.chorister.application.RegistrationRequest
import nl.stevenbontenbal.chorister.authorization.UserRole
import nl.stevenbontenbal.chorister.authorization.ZitadelUserId
import org.springframework.security.oauth2.jwt.Jwt

interface IUserAuthorizationService {
    fun postUser(registrationRequest: RegistrationRequest): Result<String?>

    fun setEmailAddress(userId: String, email: String): Result<String>

    fun getExternalUserId(): String

    fun getTenantId(): Long?

    fun createRolesForTenant(tenantId: Long)

    fun addRoleToUser(userId: String, tenantId: Long, accessLevel: AccessLevel)

    fun getRolesFromJwt(jwt: Jwt): Set<UserRole>

    fun retrieveUserRoles(tenantId: Long): Map<ZitadelUserId, List<AccessLevel>>

    fun replaceUserRoles(userId: String, tenantId: Long, accessLevel: AccessLevel)

    fun getRoles(): Set<UserRole>
}