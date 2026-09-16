package ee.kpikka.db.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object FormResponseTable : IntIdTable("form_responses") {
    var name = varchar("name", 100)
    val agreedToTerms = integer("agreed_to_terms")
}

object FormResponseSectorTable : Table("form_response_sector") {
    val formResponseId = reference("form_response_id", FormResponseTable.id)
    val sectorId = reference("sector_id", SectorsTable.id)
}