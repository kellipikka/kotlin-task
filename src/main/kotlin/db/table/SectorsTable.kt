package ee.kpikka.db.table

import ee.kpikka.model.Sector
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object SectorsTable : IntIdTable("sectors") {
    val name = varchar("name", 100)
    val parentId = integer("parent_id").nullable()
}

class SectorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SectorDAO>(SectorsTable)

    var name by SectorsTable.name
    var parentId by SectorsTable.parentId
}

fun daoToModel(dao: SectorDAO) = Sector(
    dao.id.value,
    dao.name,
    dao.parentId,
)