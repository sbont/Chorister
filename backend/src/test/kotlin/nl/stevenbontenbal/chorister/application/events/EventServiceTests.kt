package nl.stevenbontenbal.chorister.application.events

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.application.songs.create
import nl.stevenbontenbal.chorister.application.users.create
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.events.IEventEntryRepository
import nl.stevenbontenbal.chorister.domain.events.IEventRepository
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class EventServiceTests {
    private val eventRepository: IEventRepository = mockk()
    private val eventEntryRepository: IEventEntryRepository = mockk()
    private val target = EventService(eventRepository, eventEntryRepository)
    private val choir = Choir.Companion.create()
    private val event = Event.Companion.create(choir, 123L)
    private val song1 = Song.Companion.create(choir)
    private val song2 = Song.Companion.create(choir)

    @Test
    fun `when updateEventEntries with valid data then update entries correctly`() {
        val eventId = 123L
        val oldEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        event.entries.addAll(oldEntries)
        val newEntries = arrayListOf(EventEntry(2L, event, song1), EventEntry(3L, event, song2))

        every { eventRepository.findByIdOrNull(eventId) } returns event
        every { eventEntryRepository.saveAll(newEntries) } returns newEntries
        every { eventEntryRepository.deleteAllById(listOf(1L)) } returns Unit

        val result = target.updateEventEntries(eventId, newEntries)

        result shouldBe event
        verify { eventRepository.findByIdOrNull(eventId) }
        verify { eventEntryRepository.saveAll(newEntries) }
        verify { eventEntryRepository.deleteAllById(listOf(1L)) }
        event.entries.size shouldBe 2
        event.entries.none { it.id == 1L } shouldBe true
    }

    @Test
    fun `when updateEventEntries with non-existing event then throw exception`() {
        val nonExistingId = 456L
        val newEntries = arrayListOf(EventEntry(1L, event, song1))

        every { eventRepository.findByIdOrNull(nonExistingId) } returns null

        val exception = shouldThrow<IllegalArgumentException> {
            target.updateEventEntries(nonExistingId, newEntries)
        }

        exception.message shouldBe "Event not found with id: $nonExistingId"
    }

    @Test
    fun `when updateEventEntries with no entries to remove then skip deletion`() {
        val eventId = 123L
        val existingEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        event.entries.addAll(existingEntries)
        val newEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))

        every { eventRepository.findByIdOrNull(eventId) } returns event
        every { eventEntryRepository.saveAll(newEntries) } returns newEntries

        val result = target.updateEventEntries(eventId, newEntries)

        result shouldBe event
        verify { eventRepository.findByIdOrNull(eventId) }
        verify { eventEntryRepository.saveAll(newEntries) }
        verify(exactly = 0) { eventEntryRepository.deleteAllById(any()) }
    }
}