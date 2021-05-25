package nl.stevenbontenbal.cymbali.repository

import nl.stevenbontenbal.cymbali.model.Songbook
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface SongbookRepository : CrudRepository<Songbook, Long> {
    fun findByTitle(title: String): Songbook?
}