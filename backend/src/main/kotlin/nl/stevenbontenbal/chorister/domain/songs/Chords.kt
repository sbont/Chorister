package nl.stevenbontenbal.chorister.domain.songs

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.users.Choir

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
    @Column(length = 30000)
    var chords: String?,
) : Notation(id) {
    companion object
}