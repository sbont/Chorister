package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.exceptions.InvalidIdentifierException
import nl.stevenbontenbal.chorister.model.dto.EventEntriesPutRequest
import nl.stevenbontenbal.chorister.model.entities.Event
import nl.stevenbontenbal.chorister.model.entities.EventEntry
import nl.stevenbontenbal.chorister.repository.EventEntryRepository
import nl.stevenbontenbal.chorister.repository.EventRepository
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@RepositoryRestController
@ExposesResourceFor(Event::class)
class EventController(
    private val eventEntryRepository: EventEntryRepository,
    private val eventRepository: EventRepository
) {
    @PutMapping("/events/{id}/entries")
    fun putEntryCollection(@PathVariable id: Long?, @RequestBody eventEntries: EntityModel<EventEntriesPutRequest>): ResponseEntity<Any> {
        if (id == null)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        val event = eventRepository.findById(id).orElseThrow { InvalidIdentifierException("Event not found") }
        val oldEntries = event.entries
        val newEntries =  eventEntries.content?.entries ?: ArrayList()
        val toRemove = oldEntries.filterNot { newEntries.map { it.id }.contains(it.id) }
        eventEntryRepository.saveAll(newEntries.toList())
        eventEntryRepository.deleteAll(toRemove)
        return ResponseEntity.ok().build()
    }
}