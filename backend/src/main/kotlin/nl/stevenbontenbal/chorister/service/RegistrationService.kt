package nl.stevenbontenbal.chorister.service

import nl.stevenbontenbal.chorister.exceptions.UsernameAlreadyExistingException
import nl.stevenbontenbal.chorister.model.Choir
import nl.stevenbontenbal.chorister.model.User
import nl.stevenbontenbal.chorister.model.dto.RegistrationRequest
import nl.stevenbontenbal.chorister.model.dto.UserPostRequest
import nl.stevenbontenbal.chorister.repository.ChoirRepository
import nl.stevenbontenbal.chorister.repository.UserRepository

class RegistrationService(
    private val userRepository: UserRepository,
    private val choirRepository: ChoirRepository,
    private val keycloakUserService: KeycloakUserService,
    private val categorisationService: CategorisationService
) {

    fun register(registrationRequest: RegistrationRequest) {
        if (existsUserWithEmail(registrationRequest.email)) throw UsernameAlreadyExistingException("Username ${registrationRequest.email} already in use.")
        var user = registerUser(registrationRequest)
        val choir = registerChoir(registrationRequest, user)
        initalizeChoirData(choir)
    }

    private fun existsUserWithEmail(email: String): Boolean {
        val user = userRepository.findByEmail(email)
        return user != null
    }

    private fun registerUser(registrationRequest: RegistrationRequest): User {
        val userPostRequest = createUserPostRequest(registrationRequest)
        keycloakUserService.postUser(userPostRequest)
        val user = createUser(registrationRequest)
        userRepository.save(user)
        return user
    }

    private fun registerChoir(registrationRequest: RegistrationRequest, user: User): Choir {
        val choir = createChoir(registrationRequest, user)
        choirRepository.save(choir)
        user.choir = choir
        userRepository.save(user)
        return choir
    }

    private fun initalizeChoirData(choir: Choir) {
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

    private fun createChoir(registrationRequest: RegistrationRequest, user: User): Choir =
        Choir(
            name = registrationRequest.choirName,
            type = registrationRequest.choirType,
            manager = user
        )
}