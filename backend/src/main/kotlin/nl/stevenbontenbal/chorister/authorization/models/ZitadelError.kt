package nl.stevenbontenbal.chorister.authorization.models

data class ZitadelError(
    var code: Int,
    var message: String,
    var details: Array<ZitadelErrorDetail>?
)

data class ZitadelErrorDetail(
    var `@type`: String,
    var id: String,
    var message: String
)
