package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.Song
import nl.stevenbontenbal.chorister.model.SongProjection
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.prepost.PostFilter

import org.springframework.security.access.prepost.PreAuthorize




@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    fun findByTitle(title: String): Song?
    fun findBySlug(slug: String): Song?
    fun findAllByOrderByAddedAtDesc(): Iterable<Song>

    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Song>
}