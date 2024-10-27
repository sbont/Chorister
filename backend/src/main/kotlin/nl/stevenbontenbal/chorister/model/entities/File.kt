package nl.stevenbontenbal.chorister.model.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity

@Entity
class File(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @JsonIgnore
    var s3Key: String? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
): ChoirOwnedEntity
