package ee.kpikka.routes

import ee.kpikka.repository.SectorRepository
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.sectorRoutes() {
    route("/sectors") {
        get {
            val sectors = SectorRepository.getSectors()
            call.respond(sectors)
        }
    }
}