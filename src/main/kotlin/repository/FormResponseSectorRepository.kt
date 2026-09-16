package ee.kpikka.repository

import ee.kpikka.db.FormResponseSectorTable
import ee.kpikka.db.withTransaction
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere

object FormResponseSectorRepository {
    suspend fun saveSectors(formResponseId: Int, sectorIds: List<String>) = withTransaction {
        val uniqueSectorIds = sectorIds.map(String::toInt).distinct()

        FormResponseSectorTable.deleteWhere {
            FormResponseSectorTable.formResponseId eq formResponseId
        }

        FormResponseSectorTable.batchInsert(uniqueSectorIds) { sectorId ->
            this[FormResponseSectorTable.formResponseId] = formResponseId
            this[FormResponseSectorTable.sectorId] = sectorId
        }
    }
}