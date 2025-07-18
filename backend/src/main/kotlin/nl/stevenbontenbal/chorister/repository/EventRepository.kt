package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface EventRepository: CrudRepository<Event, Long>