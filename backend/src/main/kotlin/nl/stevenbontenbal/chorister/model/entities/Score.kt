package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*

@Entity
class Score(
    override var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG_ID")
    override var song: Song,
    override var description: String?,
    override var key: Key?,
    var fileUrl: String?,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    var file: File?
) : Notation(id) {
    companion object
}