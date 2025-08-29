package nl.stevenbontenbal.chorister.domain.songs

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.ChoirOwnedEntity

@Entity
class CategoryType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    var name: String,
): ChoirOwnedEntity {
    companion object
}