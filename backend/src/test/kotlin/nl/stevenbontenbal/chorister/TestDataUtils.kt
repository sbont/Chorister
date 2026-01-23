package nl.stevenbontenbal.chorister

import nl.stevenbontenbal.chorister.application.config.ChoristerProperties
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager


fun ChoristerProperties.Companion.create(): ChoristerProperties {
    return ChoristerProperties("Chorister", "v1", "https://chorister.co", ChoristerProperties.DataSource(DataSourceProperties(), DataSourceProperties()))
}

fun <T> T.persist(entityManager: TestEntityManager): T {
    entityManager.persist(this)
    return this
}
// TODO: https://betterprogramming.pub/test-data-creation-using-the-power-of-kotlin-dsl-9526a1fad05b