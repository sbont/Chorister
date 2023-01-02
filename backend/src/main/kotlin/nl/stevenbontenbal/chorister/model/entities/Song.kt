package nl.stevenbontenbal.chorister.model.entities

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import nl.stevenbontenbal.chorister.util.toSlug
import org.springframework.data.rest.core.annotation.RestResource
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Song(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
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
    var slug: String = title.toSlug(),
    var addedAt: LocalDateTime = LocalDateTime.now(),
    @ManyToOne(fetch = FetchType.EAGER)
    var addedBy: User?,
    @ManyToMany
    @JoinTable(
        name = "SONG_CATEGORY",
        joinColumns = [JoinColumn(name = "SONG_ID")],
        inverseJoinColumns = [JoinColumn(name = "CATEGORY_ID")]
    )
    var categories: MutableList<Category>? = mutableListOf(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "song", cascade=[CascadeType.ALL])
    var setlistEntries: MutableList<SetlistEntry>? = mutableListOf(),
    @Column(length = 32000)
    var text: String?,
) : ChoirOwnedEntity {
    companion object
}