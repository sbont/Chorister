package nl.stevenbontenbal.chorister.domain.events.events

import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.events.IEventEntryRepository
import org.springframework.data.rest.core.annotation.HandleAfterDelete
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(EventEntry::class)
class EventEntryEventHandler(
    private val eventEntryRepository: IEventEntryRepository
) {
    @HandleBeforeCreate
    fun handleEventEntryCreate(eventEntry: EventEntry) {
        val event = eventEntry.event
        val no = event!!.entries.size + 1
        eventEntry.sequence = no
    }

    @HandleAfterDelete
    fun handleEventEntryDelete(oldEventEntry: EventEntry) {
        oldEventEntry.event!!.id?.let {
            val laterEntries = eventEntryRepository.findByEventIdAndSequenceGreaterThan(it, oldEventEntry.sequence)
            laterEntries.forEach { current -> current.sequence -- }
            eventEntryRepository.saveAll(laterEntries)
        }
    }
}