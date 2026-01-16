package nl.stevenbontenbal.chorister.domain.events

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.songs.Song
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
class EventEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    var event: Event?,
    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "SONG_ID")
    var song: Song?,
    var songTitle: String? = null,
    var label: String? = null,
    var sequence: Int = event?.entries?.size?.plus(1) ?: 0
) {
    @PrePersist
    @PreUpdate
    fun copySongDetails() {
        if (song != null)
            songTitle = song?.title
    }

    companion object
}