package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface SetlistEntryRepository: CrudRepository<SetlistEntry, Long> {
    fun findBySetlistId(setlistId: Long): Iterable<SetlistEntry>
}