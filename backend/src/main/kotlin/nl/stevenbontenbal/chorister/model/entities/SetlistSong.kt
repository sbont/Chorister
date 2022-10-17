package nl.stevenbontenbal.chorister.model.entities

import javax.persistence.*

@Entity
@Table(name = "SETLIST_SONG")
class SetlistSong(
    @EmbeddedId
    var id: SetlistSongId = SetlistSongId(),
    @ManyToOne
    @MapsId("setlistId")
    var setlist: Setlist,
    @ManyToOne
    @MapsId("songId")
    var song: Song,
    var number: Int
)