package nl.stevenbontenbal.chorister.model.entities

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import jakarta.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    var name: String,
    var type: CategoryType
): ChoirOwnedEntity {
    companion object
}