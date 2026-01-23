package nl.stevenbontenbal.chorister.application.users

import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.Invite
import nl.stevenbontenbal.chorister.domain.users.User
import java.util.*

fun User.Companion.create(): User {
    return User(
        firstName = "Me",
        lastName = "Myself",
        username = "me@chorister.io",
        email = "me@chorister.io",
    )
}

fun Choir.Companion.create(manager: User? = null): Choir {
    return Choir(
        name = "MyChoir",
        type = "Band",
        manager = manager
    )
}

fun Invite.Companion.create(choir: Choir): Invite {
    return Invite(
        email = "new@user.com",
        choir = choir,
        invitedBy = User.create(),
        token = UUID.randomUUID().toString()
    )
}
