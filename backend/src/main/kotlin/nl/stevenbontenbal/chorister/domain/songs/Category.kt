package nl.stevenbontenbal.chorister.domain.songs

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.ChoirOwnedEntity

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    var name: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_TYPE_ID")
    var categoryType: CategoryType
): ChoirOwnedEntity {
    companion object
}