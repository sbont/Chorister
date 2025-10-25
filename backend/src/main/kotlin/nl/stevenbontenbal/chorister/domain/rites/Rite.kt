package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.*

@Entity
class Rite(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rite")
    var ordersOfService: MutableList<OrderOfService>? = mutableListOf(),
) {
}