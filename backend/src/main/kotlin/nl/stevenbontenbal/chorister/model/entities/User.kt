package nl.stevenbontenbal.chorister.model.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
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
    var displayName: String?)
{
    companion object
}