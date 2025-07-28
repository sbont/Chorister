package nl.stevenbontenbal.chorister.domain.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "chorister_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHOIR_ID")
    @JsonIgnoreProperties("users")
    var choir: Choir? = null,
    var email: String?,
    var username: String,
    @Column(name = "display_name")
    var firstName: String?,
    var lastName: String?,
    var zitadelId: String? = null
    )
{
    companion object
}