package nl.stevenbontenbal.chorister.model.dto

data class AcceptInviteRequest(
    override var displayName: String,
    override var email: String,
    override var password: String,
    var token: String,
) : RegistrationRequest
