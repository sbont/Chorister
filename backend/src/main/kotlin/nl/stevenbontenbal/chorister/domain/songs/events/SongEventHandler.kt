package nl.stevenbontenbal.chorister.domain.songs.events

import nl.stevenbontenbal.chorister.domain.songs.ISongbookRepository
import nl.stevenbontenbal.chorister.domain.songs.Song
import nl.stevenbontenbal.chorister.domain.users.UserService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Song::class)
class SongEventHandler(
    private val userService: UserService,
    private val songbookRepository: ISongbookRepository
) {
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