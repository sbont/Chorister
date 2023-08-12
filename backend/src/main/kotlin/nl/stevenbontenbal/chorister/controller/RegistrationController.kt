package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.configuration.ChoristerProperties
import nl.stevenbontenbal.chorister.model.dto.AcceptInviteRequest
import nl.stevenbontenbal.chorister.model.dto.InviteDetail
import nl.stevenbontenbal.chorister.model.dto.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.service.RegistrationService
import org.springframework.web.bind.annotation.*

@RestController
class RegistrationController(
    private val registrationService: RegistrationService,
    private val properties: ChoristerProperties
) {

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

    @GetMapping("/api/invite")
    fun getInviteByToken(@RequestParam(required = true) token: String): InviteDetail {
        return registrationService.getInviteDetail(token)
    }

    @GetMapping("/api/choir/invitelink")
    fun getInviteUrl(): String {
        val token = registrationService.generateChoirInviteToken()
        return properties.baseUrl + "/signup?invite=" + token
    }

    @DeleteMapping("/api/choir/invitelink")
    fun deleteInviteToken() {
        registrationService.nullifyChoirInviteToken()
    }

}