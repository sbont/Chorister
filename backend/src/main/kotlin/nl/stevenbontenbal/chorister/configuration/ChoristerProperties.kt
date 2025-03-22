package nl.stevenbontenbal.chorister.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("chorister")
data class ChoristerProperties(
    var title: String,
    val version: String,
    val baseUrl: String,
    val defaultCategories: DefaultCategories
) {
    data class DefaultCategories(
        val liturgicalMoment: List<String>,
        val season: List<String>
    )

    companion object
}