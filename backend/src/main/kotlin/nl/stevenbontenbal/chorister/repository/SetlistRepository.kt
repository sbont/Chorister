package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Setlist
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource
interface SetlistRepository: CrudRepository<Setlist, Long> {

    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Setlist>
}