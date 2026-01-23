package nl.stevenbontenbal.chorister.application.events

import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.persist
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate
import kotlin.collections.List

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
