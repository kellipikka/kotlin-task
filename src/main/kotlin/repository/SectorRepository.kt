package ee.kpikka.repository

import ee.kpikka.db.table.SectorDAO.Companion.all
import ee.kpikka.db.table.daoToModel
import ee.kpikka.db.withTransaction
import ee.kpikka.model.Sector

object SectorRepository {
    suspend fun getSectors(): List<Sector> = withTransaction {
        all()
            .map(::daoToModel)
            .hierarchicallySorted()
    }
}

private fun List<Sector>.hierarchicallySorted(): List<Sector> {
    val childrenByParent = groupBy(Sector::parentId)

    fun descendantsOf(parentId: Int?): List<Sector> = childrenByParent[parentId]
        .orEmpty()
        .sortedBy { it.name.lowercase() }
        .flatMap { sector -> listOf(sector) + descendantsOf(sector.id) }

    return descendantsOf(null)
}
