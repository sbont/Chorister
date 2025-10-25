package nl.stevenbontenbal.chorister.domain.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import nl.stevenbontenbal.chorister.domain.rites.LiturgicalBookTranslation
import nl.stevenbontenbal.chorister.domain.rites.Rite

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
    var manager: User?,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RITE_ID")
    var rite: Rite? = null,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LITURGICAL_BOOK_TRANSLATION_ID")
    var liturgicalBookTranslation: LiturgicalBookTranslation? = null,
)
{
    companion object
}