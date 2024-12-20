package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.EventEntry
import nl.stevenbontenbal.chorister.model.entities.EventEntryProjection
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(excerptProjection = EventEntryProjection::class)
interface EventEntryRepository: CrudRepository<EventEntry, Long> {
    fun findByEventId(eventId: Long): Iterable<EventEntry>

    @RestResource(exported = false)
    fun findByEventIdAndSequenceGreaterThan(id: Long, number: Int): Iterable<EventEntry>
}