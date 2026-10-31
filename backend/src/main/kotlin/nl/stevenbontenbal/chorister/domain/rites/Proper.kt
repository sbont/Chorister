package nl.stevenbontenbal.chorister.domain.rites

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

/**
 * The types of proper texts that vary with the liturgical day.
 */
enum class ProperType {
    ENTRANCE_ANTIPHON,
    COLLECT,
    PRAYER_OVER_OFFERINGS,
    COMMUNION_ANTIPHON,
    PRAYER_AFTER_COMMUNION,
}

/**
 * A single proper text of the Mass for a liturgical day, identified by its
 * canonical [LiturgicalDayReference], entered in markdown.
 */
@Entity
class Proper(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var dayReference: String,
    @Enumerated(EnumType.STRING)
    var type: ProperType,
    var translation: String? = null,
    @Column(length = 32000)
    var text: String? = null,
) {
    companion object
}
