package nl.stevenbontenbal.chorister.model.dto

data class ZitadelUserEmailGetResponse(
    var details: ZitadelResourceDetails,
    var email: EmailInfo?
)

data class EmailInfo(
    var email: String,
    var isEmailVerified: Boolean
)