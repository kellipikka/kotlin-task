package ee.kpikka.repository

import ee.kpikka.db.FormResponseSectorTable
import ee.kpikka.db.withTransaction
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.batchInsert

object FormResponseSectorRepository {
    suspend fun saveSectors(formResponseId: EntityID<Int>, sectorIds: List<String>) = withTransaction {
        FormResponseSectorTable.batchInsert(sectorIds) { sectorId ->
            this[FormResponseSectorTable.formResponseId] = formResponseId
            this[FormResponseSectorTable.sectorId] = sectorId.toInt()
        }
    }
}