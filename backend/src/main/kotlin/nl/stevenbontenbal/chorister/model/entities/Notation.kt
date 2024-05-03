package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import org.hibernate.annotations.DiscriminatorOptions

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
abstract class Notation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long?
) : ChoirOwnedEntity {
    abstract var song: Song
    abstract var description: String?
    abstract var key: Key?

    companion object
}