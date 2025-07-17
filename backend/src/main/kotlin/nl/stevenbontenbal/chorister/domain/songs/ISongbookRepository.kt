package nl.stevenbontenbal.chorister.domain.songs

import org.springframework.data.repository.CrudRepository

interface ISongbookRepository : CrudRepository<Songbook, Long> {
    fun findByTitle(title: String): Songbook?
}
