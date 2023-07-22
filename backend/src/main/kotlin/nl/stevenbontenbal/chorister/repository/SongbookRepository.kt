package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Songbook
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource
interface SongbookRepository : CrudRepository<Songbook, Long> {
    @RestResource
    fun findByTitle(title: String): Songbook?
}