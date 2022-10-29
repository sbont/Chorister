package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import nl.stevenbontenbal.chorister.model.entities.SetlistEntryId
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface SetlistEntryRepository: CrudRepository<SetlistEntry, SetlistEntryId> {
    fun findBySetlistId(setlistId: Long): Iterable<SetlistEntry>
}