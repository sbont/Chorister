package nl.stevenbontenbal.chorister.model.dto

import nl.stevenbontenbal.chorister.model.entities.Choir

data class InviteDetail(
    var email: String? = null,
    var choir: Choir,
    var token: String
)
