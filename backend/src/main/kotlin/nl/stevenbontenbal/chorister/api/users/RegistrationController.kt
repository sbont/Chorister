package nl.stevenbontenbal.chorister.api.users

import nl.stevenbontenbal.chorister.application.users.RegistrationService
import nl.stevenbontenbal.chorister.application.config.ChoristerProperties
import nl.stevenbontenbal.chorister.application.users.models.AcceptInviteRequest
import nl.stevenbontenbal.chorister.application.users.models.InviteDetail
import nl.stevenbontenbal.chorister.application.users.models.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.domain.users.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class RegistrationController(
    private val registrationService: RegistrationService,
    private val properties: ChoristerProperties
) {

    @PostMapping("/api/registration")
    fun register(@RequestBody registrationRequest: NewChoirRegistrationRequest): ResponseEntity<User> {
        println("User request: $registrationRequest")
        val user = registrationService.register(registrationRequest)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/api/invite/accept")
    fun register(@RequestBody registrationRequest: AcceptInviteRequest): ResponseEntity<User> {
        println("User request: $registrationRequest")
        val user = registrationService.register(registrationRequest)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/api/invite")
    fun getInviteByToken(@RequestParam(required = true) token: String): ResponseEntity<InviteDetail> {
        val invite = registrationService.getInviteDetail(token)
        return ResponseEntity.ok(invite)
    }

    @GetMapping("/api/choir/invitelink")
    fun getInviteUrl(): ResponseEntity<String> {
        val token = registrationService.generateChoirInviteToken()
        val url = properties.baseUrl + "/signup?invite=" + token
        return ResponseEntity.ok(url)
    }

    @DeleteMapping("/api/choir/invitelink")
    fun deleteInviteToken() {
        registrationService.nullifyChoirInviteToken()
    }

}