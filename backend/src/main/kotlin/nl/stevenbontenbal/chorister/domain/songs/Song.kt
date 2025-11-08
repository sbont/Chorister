package nl.stevenbontenbal.chorister.domain.songs

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.EntityBase
import nl.stevenbontenbal.chorister.domain.events.EventEntry
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.ChoirOwnedEntity
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

@Entity
class Song(
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    var title: String,
    var composer: String?,
    var recordingUrl: String?,
    var scoreUrl: String?,
    @ManyToOne(fetch = FetchType.EAGER)
    @RestResource(exported = false)
    var songbook: Songbook?,
    var songbookNumber: Int?,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "song")
    var scores: MutableList<Score>? = mutableListOf(),
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "song")
    var chords: MutableList<Chords>? = mutableListOf(),
    var slug: String = title.toSlug(),
    @ManyToMany(cascade = [CascadeType.REMOVE])
    @JoinTable(
        name = "SONG_CATEGORY",
        joinColumns = [JoinColumn(name = "SONG_ID")],
        inverseJoinColumns = [JoinColumn(name = "CATEGORY_ID")]
    )
    var categories: MutableSet<Category>? = mutableSetOf(),
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "song", cascade=[CascadeType.DETACH])
    var eventEntries: MutableList<EventEntry>? = mutableListOf(),
    @Column(length = 32000)
    var text: String?,
) : EntityBase(), ChoirOwnedEntity {
    companion object {
        fun String.toSlug() = lowercase(Locale.getDefault())
            .replace("\n", " ")
            .replace("[^a-z\\d\\s]".toRegex(), " ")
            .split(" ")
            .joinToString("-")
            .replace("-+".toRegex(), "-")
    }
}