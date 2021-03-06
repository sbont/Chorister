package nl.stevenbontenbal.chorister.controller

import nl.stevenbontenbal.chorister.configuration.ChoristerProperties
import nl.stevenbontenbal.chorister.model.Song
import nl.stevenbontenbal.chorister.repository.SongRepository
import nl.stevenbontenbal.chorister.util.format
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException

@Controller
class HtmlController(private val repository: SongRepository, private val properties: ChoristerProperties) {

    /*@GetMapping("/repertoire")
    fun repertoire(model: Model): String {
        model["title"] = properties.title
        model["songs"] = repository.findAllByOrderByAddedAtDesc().map { it.render() }
        return "repertoire"
    }*/
/*
    @GetMapping("/")
    fun index(): String {
        return "index"
    }*/

    @GetMapping("/song/{id}/{slug}")
    fun song(@PathVariable id: Long, @PathVariable slug: String, model: Model): String {
        var song = repository
            .findByIdOrNull(id)
            ?.render()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This song does not exist")
        model["title"] = song.title
        model["song"] = song
        return "song"
    }

    fun Song.render() = RenderedSong(
        id,
        title,
        composer,
        recordingUrl,
        scoreUrl,
        addedAt.format(),
        slug
    )

    data class RenderedSong(
        val id: Long?,
        val title: String,
        val composer: String?,
        val recordingUrl: String?,
        val scoreUrl: String?,
        val addedAt: String,
        val slug: String)


}