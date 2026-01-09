package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelUserEmailGetResponse(
    var details: ZitadelResourceDetails,
    var email: EmailInfo?
)

data class EmailInfo(
    var email: String,
    var isEmailVerified: Boolean
)