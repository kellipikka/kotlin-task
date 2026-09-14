package ee.kpikka

import ee.kpikka.model.Session
import ee.kpikka.repository.SessionRepository
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(ThymeleafContent("index", emptyMap()))
        }

        route("/sessions") {
            post {
                val formContent = call.receiveParameters()

                val name = formContent["name"]
                val sectors = formContent["sectors"]
                val terms = formContent["terms"]

                val agreedToTerms = if (terms == "on") 1 else 0
                SessionRepository.saveResponse(Session(name = name!!, agreedToTerms = agreedToTerms))
                call.respondRedirect("/")
            }
        }

        staticResources("/static", "static")
    }
}
