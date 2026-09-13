package ee.kpikka

import ee.kpikka.repository.SectorRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun Application.configureSerialization() {
    val repository = dependencies.resolve<SectorRepository>()

    install(ContentNegotiation) {
        json()
    }
    routing {
        route("/sectors") {
            get {
                val tasks = repository.getSectors()
                call.respond(tasks)
            }
        }
    }
}