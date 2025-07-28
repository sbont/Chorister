package nl.stevenbontenbal.chorister.api.events

import nl.stevenbontenbal.chorister.domain.events.Event
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface EventRepository: CrudRepository<Event, Long>