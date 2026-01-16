package nl.stevenbontenbal.chorister.api.events.models

import nl.stevenbontenbal.chorister.api.songs.models.SongProjection
import nl.stevenbontenbal.chorister.domain.events.Event
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import org.springframework.data.rest.core.config.Projection

@Projection(name = "defaultProjection", types = [EventEntry::class])
interface EventEntryProjection {
    var id: Long?
    var event: Event?
    var song: SongProjection?
    var songTitle: String?
    var label: String?
    var sequence: Int
}