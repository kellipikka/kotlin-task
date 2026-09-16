package ee.kpikka.service

import ee.kpikka.model.Sector
import ee.kpikka.model.SectorOptions
import ee.kpikka.model.toOptions
import ee.kpikka.repository.SectorRepository

object SectorService {
    suspend fun getSectors(): List<Sector> {
        return SectorRepository.getSectors()
    }

    suspend fun getSectorOptions(): List<SectorOptions> {
        return getSectors().toOptions()
    }
}