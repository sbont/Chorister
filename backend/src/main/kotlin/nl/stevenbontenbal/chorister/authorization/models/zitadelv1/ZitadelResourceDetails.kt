package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

import java.util.*

data class ZitadelResourceDetails(
    var sequence: Int?,
    var creationDate: Date?,
    var changeDate: Date?,
    var resourceOwner: String?
)