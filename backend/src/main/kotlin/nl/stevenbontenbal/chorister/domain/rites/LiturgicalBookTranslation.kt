package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class LiturgicalBookTranslation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var code: String,
    var title: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RITE_ID")
    var rite: Rite?,
) {
    companion object
}