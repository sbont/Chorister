package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.application.events.EventService
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@RepositoryRestController
@ExposesResourceFor(Event::class)
class EventController(
    private val eventService: EventService
) {
    @PutMapping("/events/{id}/list")
    fun putEntryCollection(@PathVariable id: Long?, @RequestBody eventEntries: CollectionModel<EventEntry>): ResponseEntity<Any> {
        if (id == null)
            return ResponseEntity.badRequest().build()

        eventService.updateEventEntries(id, eventEntries.content.toList())
        return ResponseEntity.ok().build()
    }
}