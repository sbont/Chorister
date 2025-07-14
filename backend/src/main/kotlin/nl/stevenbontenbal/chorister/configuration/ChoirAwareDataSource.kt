package nl.stevenbontenbal.chorister.configuration

import nl.stevenbontenbal.chorister.service.ChoirContext
import org.springframework.jdbc.datasource.DelegatingDataSource
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource


class ChoirAwareDataSource(targetDataSource: DataSource) : DelegatingDataSource(targetDataSource) {
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
    private fun setChoirId(connection: Connection) {
        connection.createStatement().use { sql ->
            val choirId = ChoirContext.getCurrentChoirId()
            if (choirId != null)
                sql.execute("SET app.current_choir_id TO '$choirId'")
        }
    }
}
