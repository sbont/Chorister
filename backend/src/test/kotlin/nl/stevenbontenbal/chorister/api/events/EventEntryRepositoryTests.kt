package nl.stevenbontenbal.chorister.api.events

import io.kotest.matchers.collections.shouldHaveSize
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.persist
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class EventEntryRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val eventEntryRepository: EventEntryRepository,
) {
    @Test
    fun `When findByEventId then return all event's entries`() {
        // Arrange
        val choir = Choir.create().persist(entityManager)
        val event = Event.create(choir, entityManager)
        val song1 = Song.create(choir).persist(entityManager)
        val song2 = Song.create(choir).persist(entityManager)
        Song.create(choir).persist(entityManager)
        EventEntry.create(event, song1).persist(entityManager)
        EventEntry.create(event, song2).persist(entityManager)
        // Act
        val events = eventEntryRepository.findByEventId(eventId = event.id!!)
        // Assert
        events shouldHaveSize 2
    }

}