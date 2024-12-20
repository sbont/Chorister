package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*

@Entity
class EventEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    var event: Event?,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SONG_ID")
    var song: Song?,
    var label: String? = null,
    var sequence: Int = event?.entries?.size?.plus(1) ?: 0
) {
    companion object
}