package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.exceptions.InvalidIdentifierException
import nl.stevenbontenbal.chorister.model.dto.EventEntriesPutRequest
import nl.stevenbontenbal.chorister.model.entities.Event
import nl.stevenbontenbal.chorister.repository.EventEntryRepository
import nl.stevenbontenbal.chorister.repository.EventRepository
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@BasePathAwareController
@ExposesResourceFor(Event::class)
class EventController(
    private val eventEntryRepository: EventEntryRepository,
    private val eventRepository: EventRepository
) {
    @PutMapping("/events/{id}/reorder")
    fun putEntryCollection(@PathVariable id: Long?, @RequestBody eventEntries: EventEntriesPutRequest): ResponseEntity<Any> {
        if (id == null || eventEntries.entries == null)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        val event = eventRepository.findById(id).orElseThrow { InvalidIdentifierException("Event not found") }
        val oldEntries = event.entries
        val newEntries =  eventEntries.entries!!
        newEntries.forEach { it.event = event }
        val toRemove = oldEntries.filterNot { newEntries.map { it.id }.contains(it.id) }
        eventEntryRepository.saveAll(newEntries.toList())
        eventEntryRepository.deleteAll(toRemove)
        return ResponseEntity.ok().build()
    }
}