package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Chords
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource(collectionResourceRel = "chords", path = "chords")
interface ChordsRepository : CrudRepository<Chords, Long> {
    @PreAuthorize("hasRole('VIEWER')")
    override fun <T : Chords> save(entity: T): T

    @PreAuthorize("hasRole('EDITOR')")
    override fun delete(entity: Chords)
}