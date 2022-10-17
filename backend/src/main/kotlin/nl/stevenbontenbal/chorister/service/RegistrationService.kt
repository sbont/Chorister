package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.exceptions.InvalidInputException
import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.Invite
import nl.stevenbontenbal.chorister.model.entities.User
import nl.stevenbontenbal.chorister.model.dto.*
import nl.stevenbontenbal.chorister.repository.ChoirRepository
import nl.stevenbontenbal.chorister.repository.InviteRepository
import nl.stevenbontenbal.chorister.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

open class RegistrationService(
    private val userRepository: UserRepository,
    private val choirRepository: ChoirRepository,
    private val inviteRepository: InviteRepository,
    private val keycloakUserService: KeycloakUserService,
    private val categorisationService: CategorisationService,
    private val userService: UserService
) {

    @Transactional
    open fun register(registrationRequest: RegistrationRequest): User {
        validate(registrationRequest)
        val user = registerUser(registrationRequest)
        when (registrationRequest) {
            is NewChoirRegistrationRequest -> registerNewChoir(user, registrationRequest)
            is AcceptInviteRequest -> acceptInvite(user, registrationRequest)
        }
        return user
    }

    open fun generateChoirInviteToken(): String? {
        val choir = userService.getCurrentUser().choir
        if (choir != null) {
            val token = UUID.randomUUID().toString()
            choir.inviteToken = token
            choirRepository.save(choir)
        }
        return choir?.inviteToken
    }

    open fun nullifyChoirInviteToken() {
        val choir = userService.getCurrentUser().choir
        if (choir != null) {
            choir.inviteToken = null
            choirRepository.save(choir)
        }
    }

    open fun getInviteDetail(token: String): InviteDetail {
        if (token.isBlank())
            throw InvalidInputException("Invite not valid")

        val choir = choirRepository.findByInviteToken(token)
        if (choir != null)
            return InviteDetail(choir = choir, token = token)

        val invite = getInvite(token)
        if (invite.choir == null)
            throw InvalidInputException("Choir not found")

        return InviteDetail(invite.email, invite.choir!!, token)
    }

    private fun acceptInvite(user: User, acceptInviteRequest: AcceptInviteRequest) {
        val invite = inviteRepository.findByToken(acceptInviteRequest.token)
        if (invite != null)
            addInviteeToChoir(user, invite)
        else {
            val choir = choirRepository.findByInviteToken(acceptInviteRequest.token)
            if (choir != null)
                addUserToChoir(user, choir)
            else
                throw InvalidInputException("Invalid invite")
        }
    }

    private fun validate(registrationRequest: RegistrationRequest) {
        validateEmail(registrationRequest.email)
    }

    private fun getInvite(token: String): Invite {
        val invite = inviteRepository.findByToken(token) ?: throw InvalidInputException("No invite found with token $token")
        validate(invite)
        return invite
    }

    private fun validate(invite: Invite) {
        if (invite.expired)
            throw InvalidInputException("Invite expired")
        if (invite.acceptedDate != null)
            throw InvalidInputException("Invite already accepted")
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
        val choir = invite.choir!!
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