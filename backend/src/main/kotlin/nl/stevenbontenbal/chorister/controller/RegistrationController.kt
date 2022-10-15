package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.model.dto.AcceptInviteRequest
import nl.stevenbontenbal.chorister.model.dto.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest
import nl.stevenbontenbal.chorister.repository.InviteRepository
import nl.stevenbontenbal.chorister.repository.UserRepository
import nl.stevenbontenbal.chorister.service.KeycloakUserService
import nl.stevenbontenbal.chorister.service.RegistrationService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RegistrationController(
    private val registrationService: RegistrationService) {

    @PostMapping("/api/registration")
    fun register(@RequestBody registrationRequest: NewChoirRegistrationRequest) {
        println("User request: $registrationRequest")
        registrationService.register(registrationRequest)
    }

    @PostMapping("/api/invite/accept")
    fun register(@RequestBody registrationRequest: AcceptInviteRequest) {
        println("User request: $registrationRequest")
        registrationService.register(registrationRequest)
    }

}