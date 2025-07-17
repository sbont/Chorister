package nl.stevenbontenbal.chorister.domain.events

import org.springframework.data.repository.CrudRepository

interface IEventEntryRepository : CrudRepository<EventEntry, Long> {
    fun findByEventIdAndSequenceGreaterThan(id: Long, number: Int): Iterable<EventEntry>
}