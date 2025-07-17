package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.application.config.ChoristerProperties
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.songs.CategoryType
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.Invite
import nl.stevenbontenbal.chorister.domain.users.User
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

fun Event.Companion.create(choir: Choir, id: Long? = null): Event {
    return Event(
        id = id,
        choir = choir,
        name = "Christmas Morning",
        date = LocalDate.of(2022, 12, 25),
    )
}

fun Event.Companion.create(choir: Choir, entityManager: TestEntityManager): Event {
    return Event.create(choir).persist(entityManager)
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

fun EventEntry.Companion.create(event: Event, song: Song, sequence: Int = 1): EventEntry {
    return EventEntry(
        event = event,
        song = song,
        sequence = sequence
    )
}

fun EventEntry.Companion.create(event: Event, songs: List<Song>): List<EventEntry> {
    return songs.mapIndexed { index, song -> EventEntry.create(event, song, index + 1) }
}

fun ChoristerProperties.Companion.create(): ChoristerProperties {
    return ChoristerProperties("Chorister", "v1", "https://chorister.co", ChoristerProperties.DefaultCategories(listOf(), listOf()))
}

fun <T> T.persist(entityManager: TestEntityManager): T {
    entityManager.persist(this)
    return this
}
// TODO: https://betterprogramming.pub/test-data-creation-using-the-power-of-kotlin-dsl-9526a1fad05b