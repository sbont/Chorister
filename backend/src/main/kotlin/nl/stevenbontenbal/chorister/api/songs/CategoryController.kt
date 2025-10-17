package nl.stevenbontenbal.chorister.api.songs

import nl.stevenbontenbal.chorister.api.songs.models.AssignCategoryToSongsRequest
import nl.stevenbontenbal.chorister.domain.songs.Category
import nl.stevenbontenbal.chorister.domain.songs.Song
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.hateoas.server.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@RepositoryRestController
@ExposesResourceFor(Category::class)
class CategoryController(
    private val songRepository: SongRepository,
    private val formattingConversionService: DefaultFormattingConversionService,
) {
    @PostMapping("/categories/{id}/songs")
    fun assignCategoryToSongs(@PathVariable("id") category: Category?, @RequestBody request: AssignCategoryToSongsRequest): ResponseEntity<Any> {
        if (category == null)
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        val songs = request.songs.mapNotNull { formattingConversionService.convert(it, Song::class.java) }
        songs.forEach { it.categories?.add(category) }

        if (songs.isEmpty())
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)

        songRepository.saveAll(songs)
        return ResponseEntity.ok().build()
    }
}