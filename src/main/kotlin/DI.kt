package ee.kpikka

import ee.kpikka.repository.SectorRepository
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*


fun Application.configureDependencyInjection() {
    dependencies {
        provide<SectorRepository> {
            SectorRepository()
        }
    }
}