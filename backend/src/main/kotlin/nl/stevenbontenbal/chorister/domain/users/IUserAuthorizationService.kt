package nl.stevenbontenbal.chorister.domain.users

import nl.stevenbontenbal.chorister.application.RegistrationRequest

interface IUserAuthorizationService {
    fun postUser(registrationRequest: RegistrationRequest): Result<String?>

    fun setEmailAddress(userId: String, email: String): Result<String>

    fun getExternalUserId(): String

    fun getTenantId(): Long?

    fun createRolesForTenant(tenantId: Long)

    fun addRoleToUser(userId: String, tenantId: Long)
}