package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Chords
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource(collectionResourceRel = "chords", path = "chords")
interface ChordsRepository : CrudRepository<Chords, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Chords>
}