package nl.stevenbontenbal.chorister.domain.rites

data class LiturgyItem(
    val part: MassPart,
    val sequence: Int = part.order,
    val text: String? = null,
    val reference: String? = null,
    val translation: String? = null,
)
