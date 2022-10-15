package nl.stevenbontenbal.chorister.event

import nl.stevenbontenbal.chorister.model.Song
import nl.stevenbontenbal.chorister.repository.SongbookRepository
import nl.stevenbontenbal.chorister.service.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Song::class)
class SongEventHandler(
    private val userService: UserService,
    private val songbookRepository: SongbookRepository) {

    @HandleBeforeCreate
    fun handleSongCreate(song: Song) {
        song.linkChoir(userService)
        loadSongbook(song)
    }

    @HandleBeforeSave
    fun handleSongSave(song: Song) {
        loadSongbook(song)
    }

    fun loadSongbook(song: Song) {
        val songbookTitle = song.songbook?.title
        if(songbookTitle != null) {
            val existingSongbook = songbookRepository.findByTitle(songbookTitle)
            if(existingSongbook != null) {
                song.songbook = existingSongbook
            } else {
                songbookRepository.save(song.songbook ?: return)
            }
        } else {
            song.songbook = null
        }
    }
}