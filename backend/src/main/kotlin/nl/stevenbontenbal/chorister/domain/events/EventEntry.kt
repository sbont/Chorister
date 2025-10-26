package nl.stevenbontenbal.chorister.domain.events

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.rites.ElementText
import nl.stevenbontenbal.chorister.domain.rites.OrderOfServiceElement
import nl.stevenbontenbal.chorister.domain.songs.Song

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
    var song: Song? = null,
    var label: String? = null,
    var sequence: Int = event?.entries?.size?.plus(1) ?: 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_OF_SERVICE_ELEMENT_ID")
    var orderOfServiceElement: OrderOfServiceElement? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ELEMENT_TEXT_ID")
    var elementText: ElementText? = null,
) {
    companion object
}