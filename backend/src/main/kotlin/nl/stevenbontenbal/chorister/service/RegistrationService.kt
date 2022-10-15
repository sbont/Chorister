package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.exceptions.RegistrationException
import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.model.Invite
import nl.stevenbontenbal.chorister.model.User
import nl.stevenbontenbal.chorister.model.dto.AcceptInviteRequest
import nl.stevenbontenbal.chorister.model.dto.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostRequest
import nl.stevenbontenbal.chorister.repository.ChoirRepository
import nl.stevenbontenbal.chorister.repository.InviteRepository
import nl.stevenbontenbal.chorister.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

class RegistrationService(
    private val userRepository: UserRepository,
    private val choirRepository: ChoirRepository,
    private val inviteRepository: InviteRepository,
    private val keycloakUserService: KeycloakUserService,
    private val categorisationService: CategorisationService
) {

    fun register(registrationRequest: RegistrationRequest): User {
        validate(registrationRequest)
        val user = registerUser(registrationRequest)
        when (registrationRequest) {
            is NewChoirRegistrationRequest -> {
                registerNewChoir(user, registrationRequest)
            }
            is AcceptInviteRequest -> {
                val invite = getInvite(registrationRequest.token)
                addInviteeToChoir(user, invite)
            }
        }
        return user
        // TODO: catch exception & rollback
    }

    private fun validate(registrationRequest: RegistrationRequest) {
        validateEmail(registrationRequest.email)
    }

    private fun getInvite(token: String): Invite {
        val invite = inviteRepository.findByToken(token) ?: throw RegistrationException("No invite found with token $token")
        validate(invite)
        return invite
    }

    private fun validate(invite: Invite) {
        if (invite.expired)
            throw RegistrationException("Invite expired")
        if (invite.acceptedDate != null)
            throw RegistrationException("Invite already accepted")
    }

    private fun validateEmail(email: String) {
        if (existsUserWithEmail(email))
            throw UsernameAlreadyExistingException("Username $email already in use.")
    }

    private fun existsUserWithEmail(email: String): Boolean {
        val user = userRepository.findByEmail(email)
        return user != null
    }

    private fun registerNewChoir(user: User, registrationRequest: NewChoirRegistrationRequest) {
        val choir = registerChoir(registrationRequest, user)
        initializeChoirData(choir)
        addUserToChoir(user, choir)
    }

    private fun addInviteeToChoir(user: User, invite: Invite) {
        val choir = invite.choir
        addUserToChoir(user, choir)
        invite.user = user
        invite.acceptedDate = LocalDateTime.now()
        inviteRepository.save(invite)
    }

    private fun registerUser(registrationRequest: RegistrationRequest): User {
        val userPostRequest = createUserPostRequest(registrationRequest)
        keycloakUserService.postUser(userPostRequest)
        val user = createUser(registrationRequest)
        userRepository.save(user)
        return user
    }

    private fun registerChoir(registrationRequest: NewChoirRegistrationRequest, user: User): Choir {
        val choir = createChoir(registrationRequest, user)
        choirRepository.save(choir)
        return choir
    }

    private fun addUserToChoir(user: User, choir: Choir) {
        user.choir = choir
        userRepository.save(user)
    }

    private fun initializeChoirData(choir: Choir) {
        categorisationService.createDefaultCategories(choir)
    }

    private fun createUserPostRequest(registrationRequest: RegistrationRequest): UserPostRequest =
        UserPostRequest(
            username = registrationRequest.email,
            email = registrationRequest.email,
            credentials = mutableListOf(UserPostRequest.Credential(
                type = "password",
                value = registrationRequest.password
            )),
            firstName = registrationRequest.displayName,
            lastName = null
        )

    private fun createUser(registrationRequest: RegistrationRequest): User =
        User(
            username = registrationRequest.email,
            email = registrationRequest.email,
            displayName = registrationRequest.displayName
    )

    private fun createChoir(registrationRequest: NewChoirRegistrationRequest, user: User): Choir =
        Choir(
            name = registrationRequest.choirName,
            type = registrationRequest.choirType,
            manager = user
        )
}