package nl.stevenbontenbal.chorister.event

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.configuration.ChoristerConfiguration
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.model.entities.Choir
import nl.stevenbontenbal.chorister.model.entities.Event
import nl.stevenbontenbal.chorister.model.entities.EventEntry
import nl.stevenbontenbal.chorister.model.entities.Song
import nl.stevenbontenbal.chorister.repository.EventEntryRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [ChoristerConfiguration::class])
class EventEntryEventHandlerTests {
    @Test
    fun `when event empty then first entry is 1`() {
        // Arrange
        val choir = Choir.create()
        val event = Event.create(choir, 1)
        val song1 = Song.create(choir)
        val entry = EventEntry.create(event, song1)
        val repository: EventEntryRepository = mockk(relaxed = true)
        val target = EventEntryEventHandler(repository)

        // Act
        target.handleEventEntryCreate(entry)

        // Assert
        Assertions.assertThat(entry.sequence).isEqualTo(1)
    }

    @Test
    fun `when event contains 2 then next entry is 3`() {
        // Arrange
        val choir = Choir.create()
        val event = Event.create(choir, 1)
        val song1 = Song.create(choir)
        val song2 = Song.create(choir)
        val song3 = Song.create(choir)
        val entries = EventEntry.create(event, listOf(song1, song2, song3))
        event.entries.addAll(entries.take(2))
        val repository: EventEntryRepository = mockk(relaxed = true)
        val target = EventEntryEventHandler(repository)

        // Act
        target.handleEventEntryDelete(entries[2])

        // Assert
        Assertions.assertThat(entries[2].sequence).isEqualTo(3)
    }

    @Test
    fun `when entry deleted then following entries are reordered`() {
        // Arrange
        val choir = Choir.create()
        val event = Event.create(choir, 1)
        val song1 = Song.create(choir)
        val song2 = Song.create(choir)
        val song3 = Song.create(choir)
        val entries = EventEntry.create(event, listOf(song1, song2, song3))
        val repository: EventEntryRepository = mockk(relaxed = true)
        every { repository.findByEventIdAndSequenceGreaterThan(event.id!!, 1) }.returns(listOf(entries[1], entries[2]))
        val target = EventEntryEventHandler(repository)

        // Act
        target.handleEventEntryDelete(entries[0])

        // Assert
        verify(exactly = 1) { repository.saveAll(listOf(entries[1], entries[2])) }
        Assertions.assertThat(entries[1].sequence).isEqualTo(1)
    }
}