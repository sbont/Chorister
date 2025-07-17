package nl.stevenbontenbal.chorister.application.models

import nl.stevenbontenbal.chorister.application.RegistrationRequest

data class NewChoirRegistrationRequest(
    override var displayName: String,
    override var email: String,
    override var password: String,
    var choirName: String,
    var choirType: String = "Choir") : RegistrationRequest
