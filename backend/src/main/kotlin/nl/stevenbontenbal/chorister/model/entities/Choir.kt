package nl.stevenbontenbal.chorister.model.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
class Choir(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var type: String,
    var inviteToken: String? = null,
    @OneToMany(mappedBy = "choir")
    @JsonIgnoreProperties("choir")
    var users: MutableList<User>? = mutableListOf(),
    @OneToOne(cascade = [CascadeType.ALL])
    @JsonIgnoreProperties("choir")
    var manager: User?)
{
    companion object
}