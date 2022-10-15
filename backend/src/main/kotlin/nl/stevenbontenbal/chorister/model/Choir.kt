package nl.stevenbontenbal.chorister.model

import javax.persistence.*

@Entity
class Choir(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var type: String,
    @OneToMany(mappedBy = "choir")
    var users: MutableList<User>? = mutableListOf(),
    @OneToOne(cascade = [CascadeType.ALL])
    var manager: User?)
{
    companion object
}