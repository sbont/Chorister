package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelUserEmailPutRequest(
    var email: String,
    var isEmailVerified: Boolean
)
