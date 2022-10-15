package nl.stevenbontenbal.chorister.model.dto

data class NewChoirRegistrationRequest(
    override var displayName: String,
    override var email: String,
    override var password: String,
    var choirName: String,
    var choirType: String = "Choir") : RegistrationRequest
