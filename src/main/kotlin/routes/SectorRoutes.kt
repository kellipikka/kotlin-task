package ee.kpikka.routes

import ee.kpikka.service.SectorService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.sectorRoutes() {
    route("/sectors") {
        get {
            val sectors = SectorService.getSectors()
            call.respond(sectors)
        }
    }
}