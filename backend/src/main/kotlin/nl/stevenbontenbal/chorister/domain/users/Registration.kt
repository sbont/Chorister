package nl.stevenbontenbal.chorister.domain.users

import java.util.*
import jakarta.persistence.*

@Entity
class Registration(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var registrationUuid: UUID = UUID.randomUUID(),
    var email: String,
    var username: String,
    var displayName: String,
    var choirName: String?,
    var choirType: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    var choir: Choir?,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    var user: User?)