package nl.stevenbontenbal.chorister.interfaces

import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest

interface UserAuthorizationService {
    fun postUser(registrationRequest: RegistrationRequest): Result<String?>
}