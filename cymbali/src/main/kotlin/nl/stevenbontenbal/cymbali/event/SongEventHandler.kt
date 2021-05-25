package nl.stevenbontenbal.cymbali.event

import nl.stevenbontenbal.cymbali.SongbookRepository
import nl.stevenbontenbal.cymbali.model.Song
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Song::class)
class SongEventHandler(private val songbookRepository: SongbookRepository) {

    @HandleBeforeCreate
    fun handleSongCreate(s: Song) {
        loadSongbook(s)
    }

    @HandleBeforeSave
    fun handleSongSave(s: Song) {
        loadSongbook(s)
    }

    fun loadSongbook(s: Song) {
        val songbookTitle = s.songbook?.title
        if(songbookTitle != null) {
            val existingSongbook = songbookRepository.findByTitle(songbookTitle)
            if(existingSongbook != null) {
                s.songbook = existingSongbook
            } else {
                songbookRepository.save(s.songbook ?: return)
            }
        } else {
            s.songbook = null
        }
    }
}