package ee.kpikka.repository

import ee.kpikka.db.SessionsTable
import ee.kpikka.db.withTransaction
import ee.kpikka.model.Session
import org.jetbrains.exposed.v1.jdbc.insertAndGetId

object SessionRepository {
    suspend fun saveResponse(session: Session) = withTransaction {
        SessionsTable.insertAndGetId {
            it[name] = session.name
            it[agreedToTerms] = session.agreedToTerms
        }
    }
}