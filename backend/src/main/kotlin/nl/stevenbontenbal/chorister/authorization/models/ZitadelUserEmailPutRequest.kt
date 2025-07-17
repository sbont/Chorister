package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelUserEmailPutRequest(
    var email: String,
    var isEmailVerified: Boolean
)
