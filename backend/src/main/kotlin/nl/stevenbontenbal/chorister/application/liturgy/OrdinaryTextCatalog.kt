package nl.stevenbontenbal.chorister.application.liturgy

import nl.stevenbontenbal.chorister.domain.rites.MassPart

data class OrdinaryText(
    val text: String,
    val languageTag: String,
)

/**
 * Catalog of the (translatable) ordinary of the Mass, keyed by part and
 * language tag. Vernacular translations are subject to copyright, so only
 * the public-domain Latin texts are shipped; additional translations can be
 * registered via [register] (e.g. from a choir-owned database or licensed source).
 */
object OrdinaryTextCatalog {
    private const val LATIN = "la"

    private val texts: MutableMap<MassPart, MutableMap<String, String>> = mutableMapOf(
        MassPart.KYRIE to mutableMapOf(
            LATIN to "Kyrie eleison.\nChriste eleison.\nKyrie eleison.",
        ),
        MassPart.GLORIA to mutableMapOf(
            LATIN to (
                "Gloria in excelsis Deo\n" +
                "et in terra pax hominibus bonae voluntatis.\n" +
                "Laudamus te,\n" +
                "benedicimus te,\n" +
                "adoramus te,\n" +
                "glorificamus te,\n" +
                "gratias agimus tibi propter magnam gloriam tuam,\n" +
                "Domine Deus, Rex caelestis,\n" +
                "Deus Pater omnipotens.\n" +
                "Domine Fili unigenite, Iesu Christe,\n" +
                "Domine Deus, Agnus Dei, Filius Patris,\n" +
                "qui tollis peccata mundi, miserere nobis;\n" +
                "qui tollis peccata mundi, suscipe deprecationem nostram.\n" +
                "Qui sedes ad dexteram Patris, miserere nobis.\n" +
                "Quoniam tu solus Sanctus,\n" +
                "tu solus Dominus,\n" +
                "tu solus Altissimus,\n" +
                "Iesu Christe,\n" +
                "cum Sancto Spiritu\n" +
                "in gloria Dei Patris.\n" +
                "Amen."
            ),
        ),
        MassPart.CREED to mutableMapOf(
            LATIN to (
                "Credo in unum Deum,\n" +
                "Patrem omnipotentem,\n" +
                "factorem caeli et terrae,\n" +
                "visibilium omnium et invisibilium.\n" +
                "Et in unum Dominum Iesum Christum,\n" +
                "Filium Dei unigenitum,\n" +
                "et ex Patre natum ante omnia saecula.\n" +
                "Deum de Deo, Lumen de Lumine, Deum verum de Deo vero,\n" +
                "genitum non factum, consubstantialem Patri;\n" +
                "per quem omnia facta sunt.\n" +
                "Qui propter nos homines et propter nostram salutem descendit de caelis.\n" +
                "Et incarnatus est de Spiritu Sancto ex Maria Virgine, et homo factus est.\n" +
                "Crucifixus etiam pro nobis sub Pontio Pilato,\n" +
                "passus et sepultus est,\n" +
                "et resurrexit tertia die, secundum Scripturas,\n" +
                "et ascendit in caelum,\n" +
                "sedet ad dexteram Patris.\n" +
                "Et iterum venturus est cum gloria,\n" +
                "iudicare vivos et mortuos,\n" +
                "cuius regni non erit finis.\n" +
                "Et in Spiritum Sanctum, Dominum et vivificantem,\n" +
                "qui ex Patre Filioque procedit.\n" +
                "Qui cum Patre et Filio simul adoratur et conglorificatur,\n" +
                "qui locutus est per prophetas.\n" +
                "Et unam sanctam catholicam et apostolicam Ecclesiam.\n" +
                "Confiteor unum baptisma in remissionem peccatorum.\n" +
                "Et exspecto resurrectionem mortuorum,\n" +
                "et vitam venturi saeculi. Amen."
            ),
        ),
        MassPart.SANCTUS to mutableMapOf(
            LATIN to (
                "Sanctus, Sanctus, Sanctus\n" +
                "Dominus Deus Sabaoth.\n" +
                "Pleni sunt caeli et terra gloria tua.\n" +
                "Hosanna in excelsis.\n" +
                "Benedictus qui venit in nomine Domini.\n" +
                "Hosanna in excelsis."
            ),
        ),
        MassPart.MEMORIAL_ACCLAMATION to mutableMapOf(
            LATIN to (
                "Mortem tuam Domine annuntiamus,\n" +
                "et tuam resurrectionem confitemur,\n" +
                "donec venias."
            ),
        ),
        MassPart.PATER_NOSTER to mutableMapOf(
            LATIN to (
                "Pater noster, qui es in caelis,\n" +
                "sanctificetur nomen tuum.\n" +
                "Adveniat regnum tuum.\n" +
                "Fiat voluntas tua,\n" +
                "sicut in caelo et in terra.\n" +
                "Panem nostrum quotidianum da nobis hodie,\n" +
                "et dimitte nobis debita nostra,\n" +
                "sicut et nos dimittimus debitoribus nostris.\n" +
                "Et ne nos inducas in tentationem,\n" +
                "sed libera nos a malo."
            ),
        ),
        MassPart.AGNUS_DEI to mutableMapOf(
            LATIN to (
                "Agnus Dei, qui tollis peccata mundi,\n" +
                "miserere nobis.\n" +
                "Agnus Dei, qui tollis peccata mundi,\n" +
                "miserere nobis.\n" +
                "Agnus Dei, qui tollis peccata mundi,\n" +
                "dona nobis pacem."
            ),
        ),
    )

    fun resolve(part: MassPart, languageTag: String): OrdinaryText? {
        val language = languageTag.lowercase()
        val primary = language.substringBefore('-')
        val translations = texts[part] ?: return null
        translations[language]?.let { return OrdinaryText(it, language) }
        if (primary != language) {
            translations[primary]?.let { return OrdinaryText(it, primary) }
        }
        translations[LATIN]?.let { return OrdinaryText(it, LATIN) }
        return null
    }

    fun register(part: MassPart, languageTag: String, text: String) {
        texts.getOrPut(part) { mutableMapOf() }[languageTag.lowercase()] = text
    }
}
