package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface EventRepository: CrudRepository<Event, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Event>
}