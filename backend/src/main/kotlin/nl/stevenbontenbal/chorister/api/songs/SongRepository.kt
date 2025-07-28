package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.api.songs.models.SongProjection
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource

@Primary
@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    @RestResource(path = "bycategory", rel = "bycategory")
    fun findAllByCategories_id(@Param("id") categoryId: Long): Iterable<Song>

    @Query("select s from Song s join Event l where l.id = :id")
    fun byevent(@Param("id") eventId: Long): Iterable<Song>
}