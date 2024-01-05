package nl.stevenbontenbal.chorister.model.dto

import java.util.*

data class ZitadelResourceDetails(
    var sequence: Int?,
    var creationDate: Date?,
    var changeDate: Date?,
    var resourceOwner: String?
)