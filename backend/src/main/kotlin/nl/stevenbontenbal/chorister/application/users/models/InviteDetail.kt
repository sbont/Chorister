package nl.stevenbontenbal.chorister.application.users.models

import nl.stevenbontenbal.chorister.domain.users.Choir

data class InviteDetail(
    var email: String? = null,
    var choir: Choir,
    var token: String
)
