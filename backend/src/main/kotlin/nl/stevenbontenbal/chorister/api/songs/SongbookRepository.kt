package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Songbook
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@Primary
@RepositoryRestResource
interface SongbookRepository : CrudRepository<Songbook, Long> {
    fun findByTitle(title: String): Songbook?
}