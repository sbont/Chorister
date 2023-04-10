package nl.stevenbontenbal.chorister.interfaces

import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostResponse
import org.springframework.http.ResponseEntity

interface UserAuthorizationService {
    fun postUser(registrationRequest: RegistrationRequest) : ResponseEntity<UserPostResponse>?
}