package nl.stevenbontenbal.chorister.model

import org.springframework.data.rest.core.annotation.RestResource
import javax.persistence.*

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
//    @RestResource(exported = false)
    @JoinColumn(name = "CHOIR_ID")
    var choir: Choir? = null,
    var email: String?,
    var username: String,
    var displayName: String?)