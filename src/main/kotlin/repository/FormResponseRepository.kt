package ee.kpikka.repository

import ee.kpikka.db.FormResponseTable
import ee.kpikka.db.withTransaction
import ee.kpikka.model.FormResponse
import org.jetbrains.exposed.v1.jdbc.insertAndGetId

object FormResponseRepository {
    suspend fun saveResponse(formResponse: FormResponse) = withTransaction {
        FormResponseTable.insertAndGetId {
            it[name] = formResponse.name
            it[agreedToTerms] = formResponse.agreedToTerms
        }.value
    }
}