package ee.kpikka.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondText(text = "Validation error: ${cause.message}", status = HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception while processing request", cause)
            call.respondText(
                text = "An unexpected error occurred. Please try again later.",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
}
