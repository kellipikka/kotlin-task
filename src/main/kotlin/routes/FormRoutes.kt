package ee.kpikka.routes

import ee.kpikka.model.FormResponse
import ee.kpikka.plugins.FormSession
import ee.kpikka.repository.FormResponseRepository
import ee.kpikka.repository.FormResponseSectorRepository
import ee.kpikka.service.SectorService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*

fun Route.formRoutes() {
    get("/") {
        val formResponseId = call.sessions.get<FormSession>()?.formResponseId
        val response = formResponseId?.let { FormResponseRepository.getResponse(it) } ?: FormResponse(
            name = "",
            agreedToTerms = 0
        )
        val sectors = SectorService.getSectorOptions()
        call.respond(
            ThymeleafContent(
                "index",
                mapOf(
                    "response" to response,
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
}