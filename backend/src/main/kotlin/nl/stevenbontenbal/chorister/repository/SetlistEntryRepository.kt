package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.SetlistEntry
import nl.stevenbontenbal.chorister.model.entities.SetlistEntryId
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface SetlistEntryRepository : CrudRepository<SetlistEntry, SetlistEntryId> {
    @RestResource
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<SetlistEntry>

    @RestResource
    @PostFilter("hasPermission(filterObject, 'read')")
    fun findBySetlistId(setlistId: Long): Iterable<SetlistEntry>
}