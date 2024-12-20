package nl.stevenbontenbal.chorister.model.entities

import org.springframework.data.rest.core.config.Projection

@Projection(name = "defaultProjection", types = [EventEntry::class])
interface EventEntryProjection {
    var id: Long?
    var event: Event?
    var song: Song?
    var label: String?
    var sequence: Int
}