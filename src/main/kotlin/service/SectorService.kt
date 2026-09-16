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

    suspend fun filterSelectableSectorIds(sectorIds: List<Int>): List<Int> {
        val selectableIds = getSectorOptions()
            .filter(SectorOptions::isSelectable)
            .map(SectorOptions::id)
            .toSet()

        return sectorIds.filter { it in selectableIds }
    }
}