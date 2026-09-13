package ee.kpikka

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.server.http.content.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(ThymeleafContent("index", emptyMap()))
        }

        staticResources("/static", "static")
    }
}
