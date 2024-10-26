package nl.stevenbontenbal.chorister.model.entities

import jakarta.persistence.*
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity

@Entity
class File(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var s3Key: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
): ChoirOwnedEntity
