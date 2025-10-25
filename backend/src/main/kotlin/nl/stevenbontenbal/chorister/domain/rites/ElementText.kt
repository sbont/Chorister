package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ElementText(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ELEMENT_ID")
    var element: OrderOfServiceElement?,
    var shortDescription: String?,
    var text: String,
    var sequence: Int,
    var skipToStep: Int?,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LITURGICAL_BOOK_TRANSLATION_ID")
    var translation: LiturgicalBookTranslation?,
    var canBeSung: Boolean,
)
{
    companion object
}