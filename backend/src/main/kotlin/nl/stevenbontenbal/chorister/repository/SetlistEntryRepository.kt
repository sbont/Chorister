package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import nl.stevenbontenbal.chorister.model.entities.SetlistEntryId
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource
interface SetlistEntryRepository: CrudRepository<SetlistEntry, SetlistEntryId> {
    fun findBySetlistId(setlistId: Long): Iterable<SetlistEntry>

    @RestResource(exported = false)
    fun findBySetlistIdAndNumberGreaterThan(id: Long, number: Int): Iterable<SetlistEntry>
}