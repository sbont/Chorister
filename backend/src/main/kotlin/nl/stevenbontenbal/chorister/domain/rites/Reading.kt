package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist

/**
 * A single reading of the Mass for a liturgical day, identified by its
 * canonical [LiturgicalDayReference]: the text (entered in markdown) with
 * its biblical reference and translation. [part] is expected to be a
 * reading-kind [MassPart] (READING_1..8, PSALM_1..8, GOSPEL_ACCLAMATION,
 * GOSPEL).
 */
@Entity
class Reading(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var dayReference: String,
    @Enumerated(EnumType.STRING)
    var part: MassPart,
    var reference: String? = null,
    var translation: String? = null,
    @Column(length = 32000)
    var text: String? = null,
) {
    @PrePersist
    fun validatePart() {
        require(part.contentKind == ContentKind.READING) {
            "A reading's part must be a reading part, but was $part"
        }
    }

    companion object
}
