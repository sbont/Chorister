package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*

@Entity
class EventEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne
    @MapsId("eventId")
    var event: Event,
    @ManyToOne
    @MapsId("songId")
    var song: Song?,
    var label: String? = null,
    var sequence: Int = event.entries.size + 1
) {
    companion object
}