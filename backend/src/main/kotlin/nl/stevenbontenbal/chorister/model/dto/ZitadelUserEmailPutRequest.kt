package nl.stevenbontenbal.chorister.model.dto

data class ZitadelUserEmailPutRequest(
    var email: String,
    var isEmailVerified: Boolean
)
