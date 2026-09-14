package ee.kpikka.repository

import ee.kpikka.db.SessionSectorTable
import ee.kpikka.db.withTransaction
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.batchInsert

object SessionSectorRepository {
    suspend fun saveSectors(sessionId: EntityID<Int>, sectorIds: List<String>) = withTransaction {
        SessionSectorTable.batchInsert(sectorIds) { sectorId ->
            this[SessionSectorTable.sessionId] = sessionId
            this[SessionSectorTable.sectorId] = sectorId.toInt()
        }
    }
}