package nl.stevenbontenbal.chorister.model

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import javax.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir? = null,
    var name: String,
    var type: CategoryType
): ChoirOwnedEntity {
    companion object
}