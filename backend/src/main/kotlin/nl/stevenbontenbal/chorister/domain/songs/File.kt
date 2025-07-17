package nl.stevenbontenbal.chorister.domain.songs

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.users.Choir
import nl.stevenbontenbal.chorister.domain.users.ChoirOwnedEntity

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
