package nl.stevenbontenbal.chorister.model.entities

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.util.toSlug
import org.springframework.data.rest.core.annotation.RestResource
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
class Song(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
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
    var addedAt: LocalDateTime = LocalDateTime.now(),
    @ManyToOne(fetch = FetchType.EAGER)
    var addedBy: User?,
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
) : ChoirOwnedEntity {
    companion object
}