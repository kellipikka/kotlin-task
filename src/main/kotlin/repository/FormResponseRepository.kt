package ee.kpikka.repository

import ee.kpikka.db.FormResponseTable
import ee.kpikka.db.withTransaction
import ee.kpikka.model.FormResponse
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.update

object FormResponseRepository {
    suspend fun saveResponse(formResponse: FormResponse): Int = formResponse.id
        ?.let { id ->
            updateResponse(id, formResponse)
            id
        }
        ?: createResponse(formResponse)

    private suspend fun createResponse(formResponse: FormResponse): Int = withTransaction {
        FormResponseTable.insertAndGetId {
            it[name] = formResponse.name
            it[agreedToTerms] = formResponse.agreedToTerms
        }.value
    }

    private suspend fun updateResponse(id: Int, formResponse: FormResponse) = withTransaction {
        val updatedRows = FormResponseTable.update({ FormResponseTable.id eq id }) {
            it[name] = formResponse.name
            it[agreedToTerms] = formResponse.agreedToTerms
        }

        check(updatedRows == 1) { "Form response $id does not exist" }
    }
}
