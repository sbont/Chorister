package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import nl.stevenbontenbal.chorister.repository.SetlistEntryRepository
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(SetlistEntry::class)
class SetlistEntryEventHandler(
    private val setlistEntryRepository: SetlistEntryRepository
) {

    @HandleBeforeCreate
    fun handleSetlistEntryCreate(setlistEntry: SetlistEntry) {
        val setlist = setlistEntry.setlist
        val no = setlist.entries.size + 1
        setlistEntry.number = no
    }
}