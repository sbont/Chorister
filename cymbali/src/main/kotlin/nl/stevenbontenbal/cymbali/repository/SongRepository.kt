package nl.stevenbontenbal.cymbali.repository

import nl.stevenbontenbal.cymbali.model.Song
import nl.stevenbontenbal.cymbali.model.SongProjection
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    fun findByTitle(title: String): Song?
    fun findBySlug(slug: String): Song?
    fun findAllByOrderByAddedAtDesc(): Iterable<Song>
}