package nl.stevenbontenbal.chorister.application.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("chorister")
data class ChoristerProperties(
    var title: String,
    val version: String,
    val baseUrl: String,
    val defaultCategories: DefaultCategories,
    val datasource: DataSource,
) {
    data class DefaultCategories(
        val liturgicalMoment: List<String>,
        val season: List<String>
    )

    data class DataSource(
        val system: DataSourceProperties,
        val user: DataSourceProperties,
    )

    companion object
}