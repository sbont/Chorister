package nl.stevenbontenbal.chorister.model

import java.util.*
import javax.persistence.*

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
    var user: User?)