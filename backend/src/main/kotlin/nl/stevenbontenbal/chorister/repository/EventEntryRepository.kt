package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.EventEntry
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource
interface EventEntryRepository: CrudRepository<EventEntry, Long> {
    fun findByEventId(eventId: Long): Iterable<EventEntry>

    @RestResource(exported = false)
    fun findByEventIdAndSequenceGreaterThan(id: Long, number: Int): Iterable<EventEntry>
}