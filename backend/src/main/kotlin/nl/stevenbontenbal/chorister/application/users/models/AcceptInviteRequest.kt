package nl.stevenbontenbal.chorister.application.users.models

import nl.stevenbontenbal.chorister.application.users.RegistrationRequest

data class AcceptInviteRequest(
    override var firstName: String,
    override var lastName: String?,
    override var email: String,
    override var password: String,
    var token: String,
) : RegistrationRequest
