package nl.stevenbontenbal.chorister.model

import javax.persistence.*

@Entity
class Choir(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @OneToMany(mappedBy = "choir")
    var users: MutableList<User>?,
    @OneToOne
    var manager: User?)