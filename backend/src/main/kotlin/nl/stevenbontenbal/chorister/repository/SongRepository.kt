package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Song
import nl.stevenbontenbal.chorister.model.entities.SongProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PostFilter

@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    @PostFilter("hasPermission(filterObject, 'read')")
    override fun findAll(): Iterable<Song>

    @PostFilter("hasPermission(filterObject, 'read')")
    @RestResource(path = "bycategory", rel = "bycategory")
    fun findAllByCategories_id(@Param("id") categoryId: Long): Iterable<Song>

    @PostFilter("hasPermission(filterObject, 'read')")
    @Query("select s from Song s join Setlist l where l.id = :id")
    fun bysetlist(@Param("id") setlistId: Long): Iterable<Song>
}