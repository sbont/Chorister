package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.*

@Entity
class OrderOfService(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RITE_ID")
    var rite: Rite?,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orderOfService")
    var elements: MutableList<OrderOfServiceElement>? = mutableListOf()
) {
    companion object
}