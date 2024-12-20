package nl.stevenbontenbal.chorister.model.entities

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import java.time.LocalDate
import jakarta.persistence.*
import org.springframework.data.rest.core.annotation.RestResource

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