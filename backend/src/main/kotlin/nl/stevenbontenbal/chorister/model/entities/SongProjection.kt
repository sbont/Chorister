package nl.stevenbontenbal.chorister.model.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.rest.core.config.Projection
import java.time.LocalDateTime

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
    var text: String?
    var categories: MutableList<Category>?

    @JsonIgnore
    fun getSetlistEntries(): MutableList<SetlistEntry>?

    fun getLastSetlist(): Setlist? = getSetlistEntries()?.map { entry -> entry.setlist }?.sortedByDescending { it.date }?.firstOrNull()

}