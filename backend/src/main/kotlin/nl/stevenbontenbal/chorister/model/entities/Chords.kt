package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*

@Entity
class Chords(
    override var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG_ID")
    override var song: Song,
    override var description: String?,
    override var key: Key?,
    var chords: String?,
) : Notation(id) {
    companion object
}