package ee.kpikka.db

import ee.kpikka.model.Sector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.inTopLevelSuspendTransaction

object SectorsTable : IntIdTable("sectors") {
    val name = varchar("name", 100)
    val parentId = integer("parent_id").nullable()
}

class SectorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SectorDAO>(SectorsTable)

    var name by SectorsTable.name
    var parentId by SectorsTable.parentId
}

suspend fun <T> withTransaction(block: suspend JdbcTransaction.() -> T): T = withContext(Dispatchers.IO) {
    inTopLevelSuspendTransaction { block() }
}

fun daoToModel(dao: SectorDAO) = Sector(
    dao.id.value,
    dao.name,
    dao.parentId,
)
