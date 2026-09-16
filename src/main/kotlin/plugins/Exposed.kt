package ee.kpikka.plugins

import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.sqlite.SQLiteDataSource

fun Application.configureDatabase() {
    val jdbcUrl = environment.config.property("database.jdbcUrl").getString()

    val dataSource = SQLiteDataSource().apply {
        url = jdbcUrl
        setEnforceForeignKeys(true)
    }

    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .validateMigrationNaming(true)
        .load()
        .migrate()

    Database.connect(dataSource)
}
