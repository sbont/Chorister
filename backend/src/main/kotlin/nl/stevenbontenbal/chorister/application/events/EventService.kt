package nl.stevenbontenbal.chorister.application.events

import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.events.IEventEntryRepository
import nl.stevenbontenbal.chorister.domain.events.IEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EventService(
    private val eventRepository: IEventRepository,
    private val eventEntryRepository: IEventEntryRepository
) {

    @Transactional
    fun updateEventEntries(eventId: Long, newEntries: List<EventEntry>): Event {
        val event = eventRepository.findById(eventId).orElseThrow { IllegalArgumentException("Event not found with id: $eventId") }
        val currentEntries = event.entries.toList()
        val processedNewEntries = newEntries.map { it.event = event; it }

        val newEntryIds = processedNewEntries.mapNotNull { it.id }.toSet()
        val entriesToRemove = currentEntries.filter { it.id != null && !newEntryIds.contains(it.id) }

        event.entries.removeAll(entriesToRemove)
        eventRepository.save(event)

        eventEntryRepository.saveAll(processedNewEntries)

        val idsToDelete = entriesToRemove.mapNotNull { it.id }
        if (idsToDelete.isNotEmpty()) {
            eventEntryRepository.deleteAllById(idsToDelete)
        }

        return event
    }
}