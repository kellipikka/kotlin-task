package ee.kpikka

import ee.kpikka.model.FormResponse
import ee.kpikka.model.toOptions
import ee.kpikka.repository.FormResponseRepository
import ee.kpikka.repository.FormResponseSectorRepository
import ee.kpikka.repository.SectorRepository
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*

fun Application.configureRouting() {
    routing {
        get("/") {
            val sectors = SectorRepository.getSectors().toOptions()
            call.respond(
                ThymeleafContent(
                    "index",
                    mapOf(
                        "sectors" to sectors
                    )
                )
            )
        }

        route("/form") {
            post {
                val formContent = call.receiveParameters()

                val name = formContent["name"] ?: ""
                val sectors = formContent.getAll("sectors") ?: emptyList()
                val terms = formContent["terms"]?.toInt() ?: 0

                val formSession = call.sessions.get<FormSession>()
                val formResponseId = FormResponseRepository.saveResponse(
                    FormResponse(
                        id = formSession?.formResponseId,
                        name = name,
                        agreedToTerms = terms,
                    )
                )

                if (formSession == null) {
                    call.sessions.set(FormSession(formResponseId))
                }

                FormResponseSectorRepository.saveSectors(formResponseId, sectors)
                call.respondRedirect("/")
            }
        }

        route("/sectors") {
            get {
                val sectors = SectorRepository.getSectors()
                call.respond(sectors)
            }
        }

        staticResources("/static", "static")
    }
}
