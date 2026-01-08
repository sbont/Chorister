package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.api.songs.models.SongProjection
import nl.stevenbontenbal.chorister.domain.songs.Song
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.security.access.prepost.PreAuthorize

@Primary
@RepositoryRestResource(excerptProjection = SongProjection::class)
interface SongRepository : CrudRepository<Song, Long> {
    @EntityGraph(attributePaths = ["categories", "eventEntries"])
    override fun findAll(): Iterable<Song>

    @PreAuthorize("hasRole('EDITOR')")
    override fun <S : Song> save(entity: S): S

    @PreAuthorize("hasRole('EDITOR')")
    override fun delete(entity: Song)

    @RestResource(path = "bycategory", rel = "bycategory")
    @EntityGraph(attributePaths = ["categories", "eventEntries"])
    fun findAllByCategories_id(@Param("id") categoryId: Long): Iterable<Song>

    @Query("select s from Song s join Event l where l.id = :id")
    @EntityGraph(attributePaths = ["categories", "eventEntries"])
    fun byevent(@Param("id") eventId: Long): Iterable<Song>
}