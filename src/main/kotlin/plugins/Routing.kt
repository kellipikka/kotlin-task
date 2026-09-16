package ee.kpikka.plugins

import ee.kpikka.routes.formRoutes
import ee.kpikka.routes.sectorRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        formRoutes()
        sectorRoutes()

        staticResources("/static", "static")
    }
}
