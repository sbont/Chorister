package nl.stevenbontenbal.cymbali

import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.data.rest.core.config.Projection
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.persistence.*


@Entity
class Song(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: String,
    var composer: String?,
    var recordingUrl: String?,
    var scoreUrl: String?,
    @ManyToOne(fetch = FetchType.EAGER)
    @RestResource(exported = false)
    var songbook: Songbook?,
    var songbookNumber: Int?,
    var addedAt: LocalDateTime = LocalDateTime.now(),
    var slug: String = title.toSlug(),
    @ManyToOne(fetch = FetchType.EAGER)
    var addedBy: User?)

@Projection(name = "defaultProjection", types = [Song::class])
interface SongProjection {
    var id: Long?
    var title: String
    var composer: String?
    var recordingUrl: String?
    var scoreUrl: String?
    var songbook: Songbook?
    var songbookNumber: Int?
    var addedAt: LocalDateTime
    var slug: String
}

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

@Entity
class Songbook(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: String)

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var email: String?,
    var displayName: String?)
