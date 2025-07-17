package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.domain.events.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface EventRepository: CrudRepository<Event, Long>