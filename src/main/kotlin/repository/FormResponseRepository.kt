package ee.kpikka.repository

import ee.kpikka.db.FormResponseSectorTable
import ee.kpikka.db.FormResponseSectorTable.formResponseId
import ee.kpikka.db.FormResponseTable
import ee.kpikka.db.withTransaction
import ee.kpikka.model.FormResponse
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.*

object FormResponseRepository {
    suspend fun saveResponse(formResponse: FormResponse): Int = withTransaction {
        val formResponseId = formResponse.id
            ?.let { id ->
                updateResponse(id, formResponse)
                id
            }
            ?: createResponse(formResponse)

        saveSectors(formResponseId, formResponse.sectorIds)
        formResponseId
    }

    private fun createResponse(formResponse: FormResponse): Int {
        return FormResponseTable.insertAndGetId {
            it[name] = formResponse.name
            it[agreedToTerms] = formResponse.agreedToTerms
        }.value
    }

    private fun updateResponse(id: Int, formResponse: FormResponse) {
        val updatedRows = FormResponseTable.update({ FormResponseTable.id eq id }) {
            it[name] = formResponse.name
            it[agreedToTerms] = formResponse.agreedToTerms
        }

        check(updatedRows == 1) { "Form response $id does not exist" }
    }

    private fun saveSectors(formResponseId: Int, sectorIds: List<String>) {
        val uniqueSectorIds = sectorIds.map(String::toInt).distinct()

        FormResponseSectorTable.deleteWhere {
            FormResponseSectorTable.formResponseId eq formResponseId
        }

        FormResponseSectorTable.batchInsert(uniqueSectorIds) { sectorId ->
            this[FormResponseSectorTable.formResponseId] = formResponseId
            this[FormResponseSectorTable.sectorId] = sectorId
        }
    }

    suspend fun getResponse(id: Int): FormResponse? = withTransaction {
        val sectorIds = Coalesce(
            FormResponseSectorTable.sectorId.castTo(VarCharColumnType()).groupConcat(separator = ","),
            stringLiteral("")
        )

        FormResponseTable.join(
            FormResponseSectorTable,
            JoinType.LEFT,
            additionalConstraint = { FormResponseTable.id eq formResponseId }
        ).select(
            FormResponseTable.name,
            FormResponseTable.agreedToTerms,
            sectorIds
        ).groupBy(FormResponseTable.id, FormResponseTable.name, FormResponseTable.agreedToTerms)
            .where { FormResponseTable.id eq id }
            .singleOrNull()
            ?.let { row ->
                FormResponse(
                    id = id,
                    name = row[FormResponseTable.name],
                    agreedToTerms = row[FormResponseTable.agreedToTerms],
                    sectorIds = row[sectorIds]
                        .split(",")
                        .filter(String::isNotEmpty)
                        .map(String::toInt)
                )
            }
    }
}
