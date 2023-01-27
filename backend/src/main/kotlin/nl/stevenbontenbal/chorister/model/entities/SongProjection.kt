package nl.stevenbontenbal.chorister.model.entities

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
    var scores: MutableList<Score>?
    var addedAt: LocalDateTime
    var slug: String
    var text: String?
}