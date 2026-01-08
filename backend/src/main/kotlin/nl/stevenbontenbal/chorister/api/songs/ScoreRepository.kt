package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Score
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface ScoreRepository : CrudRepository<Score, Long> {
    @PreAuthorize("hasRole('EDITOR')")
    override fun <T : Score> save(entity: T): T

    @PreAuthorize("hasRole('EDITOR')")
    override fun delete(entity: Score)
}