package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.model.*
import java.time.LocalDate
import java.util.*

fun Song.Companion.create(choir: Choir): Song {
    return Song(
        title = "My Song",
        composer = "John Doe",
        recordingUrl = "http://www.youtube.com/1234",
        scoreUrl = "http://www.hymnary.com/5678",
        songbook = null,
        songbookNumber = 23,
        choir = choir,
        addedBy = null,
    )
}

fun Setlist.Companion.create(choir: Choir): Setlist {
    return Setlist(
        choir = choir,
        name = "Christmas Morning",
        date = LocalDate.of(2022, 12, 25),
    )
}

fun Category.Companion.create(choir: Choir, categoryType: CategoryType): Category {
    return Category(
        name = "Christmas",
        choir = choir,
        type = categoryType
    )
}

fun User.Companion.create(): User {
    return User(
        displayName = "Me",
        username = "me@corister.io",
        email = "me@corister.io"
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
// TODO: https://betterprogramming.pub/test-data-creation-using-the-power-of-kotlin-dsl-9526a1fad05b