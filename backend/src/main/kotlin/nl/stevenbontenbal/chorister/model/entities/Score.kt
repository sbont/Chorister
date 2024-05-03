package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity

@Entity
class Score(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG_ID")
    var song: Song,
    var description: String?,
    var fileUrl: String,
    var key: Key?,
) : ChoirOwnedEntity {
    companion object
}