package nl.stevenbontenbal.chorister.model.entities

import nl.stevenbontenbal.chorister.interfaces.ChoirOwnedEntity
import java.time.LocalDateTime
import java.util.*
import jakarta.persistence.*

@Entity
class Invite(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var email: String?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    override var choir: Choir?,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITED_BY_ID")
    var invitedBy: User?,
    var token: String = UUID.randomUUID().toString(),
    var createdDate: LocalDateTime = LocalDateTime.now(),
    var acceptedDate: LocalDateTime? = null,
    var expired: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    var user: User? = null
) : ChoirOwnedEntity {
    fun isAccepted(): Boolean = acceptedDate != null && user != null

    companion object
}