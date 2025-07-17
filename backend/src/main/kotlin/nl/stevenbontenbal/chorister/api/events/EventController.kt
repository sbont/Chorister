package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.events.IEventEntryRepository
import nl.stevenbontenbal.chorister.domain.events.IEventRepository
import nl.stevenbontenbal.chorister.domain.InvalidIdentifierException
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@RepositoryRestController
@ExposesResourceFor(Event::class)
class EventController(
    private val eventEntryRepository: IEventEntryRepository,
    private val eventRepository: IEventRepository
) {
    @PutMapping("/events/{id}/list")
    fun putEntryCollection(@PathVariable id: Long?, @RequestBody eventEntries: CollectionModel<EventEntry>): ResponseEntity<Any> {
        if (id == null)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        val event = eventRepository.findById(id).orElseThrow { InvalidIdentifierException("Event not found") }
        val oldEntries = event.entries
        val newEntries = eventEntries.content.toList().map { it.event = event; it }
        val toRemove = oldEntries.filterNot { newEntries.map { it.id }.contains(it.id) }
        eventEntryRepository.saveAll(newEntries.toList())
        eventEntryRepository.deleteAll(toRemove)
        return ResponseEntity.ok().build()
    }
}