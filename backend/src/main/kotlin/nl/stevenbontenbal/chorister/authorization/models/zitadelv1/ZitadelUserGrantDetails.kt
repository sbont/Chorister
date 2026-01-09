package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelUserGrantDetails(
    var sequence: String,
    var creationDate: String,
    var changeDate: String,
    var resourceOwner: String
)