package nl.stevenbontenbal.chorister.domain.events

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.ChoirOwnedEntity
import java.time.LocalDate

@Entity
class Event (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var date: LocalDate? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "event", cascade=[CascadeType.REMOVE])
    var entries: MutableList<EventEntry> = mutableListOf(),
): ChoirOwnedEntity {
    companion object
}