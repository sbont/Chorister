package nl.stevenbontenbal.chorister.api.events

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.api.events.models.EventEntriesPutRequest
import nl.stevenbontenbal.chorister.create
import nl.stevenbontenbal.chorister.domain.InvalidIdentifierException
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.events.IEventEntryRepository
import nl.stevenbontenbal.chorister.domain.events.IEventRepository
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir
import org.junit.jupiter.api.Test
import org.springframework.hateoas.EntityModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.Optional

class EventControllerTests {
    private val eventRepository: IEventRepository = mockk()
    private val eventEntryRepository: IEventEntryRepository = mockk()
    private val target = EventController(eventEntryRepository, eventRepository)
    private val choir = Choir.Companion.create()
    private val event = Event.Companion.create(choir, 123L)
    private val song1 = Song.Companion.create(choir)
    private val song2 = Song.Companion.create(choir)

    @Test
    fun `when putEntryCollection with valid id then update entries`() {
        val eventId = 123L
        val oldEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        event.entries.addAll(oldEntries)
        val newEntries = arrayListOf(EventEntry(2L, event, song1), EventEntry(3L, event, song2))
        val request = EventEntriesPutRequest(newEntries)
        val entityModel = EntityModel.of(request)
        every { eventRepository.findById(eventId) } returns Optional.of(event)
        every { eventEntryRepository.saveAll(newEntries) } returns newEntries
        every { eventEntryRepository.deleteAll(any()) } returns Unit

        val response: ResponseEntity<Any> = target.putEntryCollection(eventId, entityModel)

        response.statusCode shouldBe HttpStatus.OK
        verify { eventEntryRepository.saveAll(newEntries) }
        verify { eventEntryRepository.deleteAll(match { it.single().id == 1L }) }
    }

    @Test
    fun `when putEntryCollection with invalid id then return not found`() {
        val nonExistingId = 456L
        val newEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        val request = EventEntriesPutRequest(newEntries)
        val entityModel = EntityModel.of(request)

        every { eventRepository.findById(nonExistingId) } returns Optional.empty()

        val exception = shouldThrow<InvalidIdentifierException> {
            target.putEntryCollection(nonExistingId, entityModel)
        }

        exception.message shouldBe "Event not found"
    }

    @Test
    fun `when putEntryCollection with null id then return bad request`() {
        val newEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        val request = EventEntriesPutRequest(newEntries)
        val entityModel = EntityModel.of(request)

        val response: ResponseEntity<Any> = target.putEntryCollection(null, entityModel)

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
    }
}