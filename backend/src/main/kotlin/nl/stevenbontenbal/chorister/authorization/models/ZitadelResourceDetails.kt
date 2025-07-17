package nl.stevenbontenbal.chorister.authorization.models

import java.util.*

data class ZitadelResourceDetails(
    var sequence: Int?,
    var creationDate: Date?,
    var changeDate: Date?,
    var resourceOwner: String?
)