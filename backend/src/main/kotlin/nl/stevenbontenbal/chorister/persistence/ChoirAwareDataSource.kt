package nl.stevenbontenbal.chorister.persistence

import nl.stevenbontenbal.chorister.application.users.ChoirContext
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DelegatingDataSource
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class ChoirAwareDataSource(targetDataSource: DataSource) : DelegatingDataSource(targetDataSource) {
    private val logger = LoggerFactory.getLogger(ChoirAwareDataSource::class.java)

    @Throws(SQLException::class)
    override fun getConnection(): Connection {
        val connection = targetDataSource!!.connection
        setChoirId(connection)
        return connection
    }

    @Throws(SQLException::class)
    override fun getConnection(username: String, password: String): Connection {
        val connection = targetDataSource!!.getConnection(username, password)
        setChoirId(connection)
        return connection
    }

    @Throws(SQLException::class)
    fun setChoirId(connection: Connection) {
        connection.createStatement().use { sql ->
            val choirId = ChoirContext.getCurrentChoirId()
            if (choirId != null) {
                logger.debug("Setting tenant ID = $choirId")
                sql.execute("SET app.current_choir_id TO '$choirId'")
            } else {
                logger.debug("Unsetting tenant ID")
                sql.execute("RESET app.current_choir_id")
            }
        }
    }
}