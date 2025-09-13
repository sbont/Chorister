package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class OrderOfServiceElement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var sequence: Int,
    var name: String,
    var isHeader: Boolean,
    var canBeSung: Boolean,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ORDER_OF_SERVICE_ID")
    var orderOfService: OrderOfService? = null
) {
    companion object
}