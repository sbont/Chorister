package nl.stevenbontenbal.chorister.application

import nl.stevenbontenbal.chorister.application.models.AcceptInviteRequest
import nl.stevenbontenbal.chorister.application.models.InviteDetail
import nl.stevenbontenbal.chorister.application.models.NewChoirRegistrationRequest
import nl.stevenbontenbal.chorister.domain.users.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

open class RegistrationService(
    private val userRepository: IUserRepository,
    private val choirRepository: IChoirRepository,
    private val inviteRepository: IInviteRepository,
    private val authorizationService: IUserAuthorizationService,
    private val categorisationService: CategorisationService,
    private val currentChoirRepository: ICurrentChoirRepository,
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
                userService.addUserToChoir(user, choir)
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
        userService.addUserToChoir(user, choir)
        initializeChoirData(choir)
    }

    private fun addInviteeToChoir(user: User, invite: Invite) {
        val choir = invite.choir!!
        userService.addUserToChoir(user, choir)
        currentChoirRepository.setCurrentChoir(choir)
        invite.user = user
        invite.acceptedDate = LocalDateTime.now()
        inviteRepository.save(invite)
    }

    private fun registerUser(registrationRequest: RegistrationRequest): User {
        val userId = authorizationService.postUser(registrationRequest)
        val user = createUser(registrationRequest, userId)
        userRepository.save(user)
        return user
    }

    private fun registerChoir(registrationRequest: NewChoirRegistrationRequest, user: User): Choir {
        val choir = createChoir(registrationRequest, user)
        currentChoirRepository.insertAsCurrentChoir(choir)
        return choir
    }

    private fun initializeChoirData(choir: Choir) {
        categorisationService.createDefaultCategories(choir)
    }

    private fun createUser(registrationRequest: RegistrationRequest, userId: Result<String?>): User =
        User(
            username = registrationRequest.email,
            email = registrationRequest.email,
            displayName = registrationRequest.displayName,
            zitadelId = userId.getOrNull()
        )

    private fun createChoir(registrationRequest: NewChoirRegistrationRequest, user: User): Choir =
        Choir(
            name = registrationRequest.choirName,
            type = registrationRequest.choirType,
            manager = user
        )
}