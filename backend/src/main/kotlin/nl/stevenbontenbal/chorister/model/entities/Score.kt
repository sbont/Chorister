package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*

@Entity
class Score(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG_ID")
    var song: Song,
    var description: String?,
    var fileUrl: String)