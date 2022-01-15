package nl.stevenbontenbal.chorister.model

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import java.util.*
import javax.persistence.*

@Entity
class Setlist (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var date: Date? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir? = null,
    @ManyToMany
    @JoinTable(
        name = "SETLIST_SONG",
        joinColumns = [JoinColumn(name = "SETLIST_ID")],
        inverseJoinColumns = [JoinColumn(name = "SONG_ID")]
    )
    var songs: MutableList<Song>?,
    @OneToMany(mappedBy = "setlist")
    var setlistSongs: MutableSet<SetlistSong>?
): ChoirOwnedEntity