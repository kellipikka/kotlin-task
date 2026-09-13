package ee.kpikka

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.sqlite.SQLiteDataSource

fun Application.configureDatabase() {
    val jdbcUrl = environment.config.property("database.jdbcUrl").getString()

    val dataSource = SQLiteDataSource().apply {
        url = jdbcUrl
        setEnforceForeignKeys(true)
    }

    Database.connect(dataSource)
}
