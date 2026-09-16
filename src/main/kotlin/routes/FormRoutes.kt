package ee.kpikka.routes

import ee.kpikka.model.request.FormRequest
import ee.kpikka.plugins.FormSession
import ee.kpikka.service.FormService
import ee.kpikka.service.SectorService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.thymeleaf.*

fun Route.formRoutes() {
    get("/") {
        val formResponseId = call.sessions.get<FormSession>()?.formResponseId

        val response = FormService.getExistingOrNewFormResponse(formResponseId)
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
            val formRequest = FormRequest.from(formContent)

            val formSession = call.sessions.get<FormSession>()

            val formResponseId = FormService.postFormResponse(formRequest, formSession?.formResponseId)

            if (formSession == null) {
                call.sessions.set(FormSession(formResponseId))
            }

            call.respondRedirect("/")
        }
    }
}