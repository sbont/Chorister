package nl.stevenbontenbal.chorister.application.models

import nl.stevenbontenbal.chorister.application.RegistrationRequest

data class AcceptInviteRequest(
    override var displayName: String,
    override var email: String,
    override var password: String,
    var token: String,
) : RegistrationRequest
