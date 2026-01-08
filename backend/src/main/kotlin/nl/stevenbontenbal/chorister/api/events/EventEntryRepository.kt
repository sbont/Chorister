package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.api.events.models.EventEntryProjection
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource(excerptProjection = EventEntryProjection::class)
interface EventEntryRepository: CrudRepository<EventEntry, Long> {
    fun findByEventId(eventId: Long): Iterable<EventEntry>

    @PreAuthorize("hasRole('EDITOR')")
    override fun <T : EventEntry> save(entity: T): T

    @PreAuthorize("hasRole('EDITOR')")
    override fun delete(entity: EventEntry)
}