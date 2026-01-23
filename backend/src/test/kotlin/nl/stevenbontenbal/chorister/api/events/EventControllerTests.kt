package nl.stevenbontenbal.chorister.api.events

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nl.stevenbontenbal.chorister.application.events.EventService
import nl.stevenbontenbal.chorister.application.events.create
import nl.stevenbontenbal.chorister.application.songs.create
import nl.stevenbontenbal.chorister.application.users.create
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.Choir
import org.junit.jupiter.api.Test
import org.springframework.hateoas.CollectionModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class EventControllerTests {
    private val eventService: EventService = mockk()
    private val target = EventController(eventService)
    private val choir = Choir.Companion.create()
    private val event = Event.Companion.create(choir, 123L)
    private val song1 = Song.Companion.create(choir)
    private val song2 = Song.Companion.create(choir)

    @Test
    fun `when putEntryCollection with valid id then update entries`() {
        val eventId = 123L
        val newEntries = arrayListOf(EventEntry(2L, event, song1), EventEntry(3L, event, song2))
        val requestBody = CollectionModel.of(newEntries)
        every { eventService.updateEventEntries(eventId, newEntries) } returns event

        val response: ResponseEntity<Any> = target.putEntryCollection(eventId, requestBody)

        response.statusCode shouldBe HttpStatus.OK
        verify { eventService.updateEventEntries(eventId, newEntries) }
    }

    @Test
    fun `when putEntryCollection with invalid id then return not found`() {
        val nonExistingId = 456L
        val newEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        val requestBody = CollectionModel.of(newEntries)

        every { eventService.updateEventEntries(nonExistingId, newEntries) } throws IllegalArgumentException("Event not found with id: $nonExistingId")

        val exception = shouldThrow<IllegalArgumentException> {
            target.putEntryCollection(nonExistingId, requestBody)
        }

        exception.message shouldBe "Event not found with id: $nonExistingId"
    }

    @Test
    fun `when putEntryCollection with null id then return bad request`() {
        val newEntries = arrayListOf(EventEntry(1L, event, song1), EventEntry(2L, event, song2))
        val requestBody = CollectionModel.of(newEntries)

        val response: ResponseEntity<Any> = target.putEntryCollection(null, requestBody)

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
    }
}