package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.model.entities.*
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate
import java.util.*
import kotlin.collections.List

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
        text = ""
    )
}

fun Setlist.Companion.create(choir: Choir, id: Long? = null): Setlist {
    return Setlist(
        id = id,
        choir = choir,
        name = "Christmas Morning",
        date = LocalDate.of(2022, 12, 25),
    )
}

fun Setlist.Companion.create(choir: Choir, entityManager: TestEntityManager): Setlist {
    return Setlist.create(choir).persist(entityManager)
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

fun SetlistEntry.Companion.create(setlist: Setlist, song: Song, number: Int = 1): SetlistEntry {
    return SetlistEntry(
        setlist = setlist,
        song = song,
        number = number
    )
}

fun SetlistEntry.Companion.create(setlist: Setlist, songs: List<Song>): List<SetlistEntry> {
    return songs.mapIndexed { index, song -> SetlistEntry.create(setlist, song, index + 1) }
}

fun <T> T.persist(entityManager: TestEntityManager): T {
    entityManager.persist(this)
    return this
}
// TODO: https://betterprogramming.pub/test-data-creation-using-the-power-of-kotlin-dsl-9526a1fad05b