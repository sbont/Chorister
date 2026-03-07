package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.api.events.models.EventProjection
import nl.stevenbontenbal.chorister.domain.events.Event
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*

@Primary
@RepositoryRestResource(excerptProjection = EventProjection::class)
interface EventRepository: CrudRepository<Event, Long> {
    @EntityGraph(attributePaths = ["entries", "choir", "entries.event", "entries.song", "entries.song.choir", "entries.song.categories", "entries.song.eventEntries"])
    fun findById(id: Long?): Optional<Event?>

    @PreAuthorize("hasRole('EDITOR')")
    override fun <T : Event> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: Event)
}