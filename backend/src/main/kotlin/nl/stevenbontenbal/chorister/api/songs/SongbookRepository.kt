package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Songbook
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource
interface SongbookRepository : CrudRepository<Songbook, Long> {
    @PreAuthorize("hasRole('EDITOR')")
    override fun <T : Songbook> save(entity: T): T

    @PreAuthorize("hasRole('EDITOR')")
    override fun delete(entity: Songbook)

    fun findByTitle(title: String): Songbook?
}