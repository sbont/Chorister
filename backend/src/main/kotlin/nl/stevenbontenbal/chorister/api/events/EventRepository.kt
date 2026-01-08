package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.domain.events.Event
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface EventRepository: CrudRepository<Event, Long> {
    @PreAuthorize("hasRole('EDITOR')")
    override fun <T : Event> save(entity: T): T

    @PreAuthorize("hasRole('MANAGER')")
    override fun delete(entity: Event)
}