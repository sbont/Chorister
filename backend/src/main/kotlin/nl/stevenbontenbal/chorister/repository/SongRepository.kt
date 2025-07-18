package nl.stevenbontenbal.chorister.repository

import nl.stevenbontenbal.chorister.model.entities.Song
import nl.stevenbontenbal.chorister.model.entities.SongProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    @RestResource(path = "bycategory", rel = "bycategory")
    fun findAllByCategories_id(@Param("id") categoryId: Long): Iterable<Song>

    @Query("select s from Song s join Event l where l.id = :id")
    fun byevent(@Param("id") eventId: Long): Iterable<Song>
}