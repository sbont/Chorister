package nl.stevenbontenbal.chorister.domain.events

import org.springframework.data.repository.CrudRepository

interface IEventRepository: CrudRepository<Event, Long>