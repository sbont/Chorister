package nl.stevenbontenbal.chorister.domain.songs

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.users.ChoirOwnedEntity
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