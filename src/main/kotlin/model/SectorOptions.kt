package ee.kpikka.model

data class SectorOptions(
    val id: Int,
    val name: String,
    val depth: Int,
    val isSelectable: Boolean
) {
    val label: String
        get() = "\u00A0".repeat(depth * 4) + name
}

fun List<Sector>.toOptions(): List<SectorOptions> {
    val sectorsById = associateBy { it.id }
    val parentIds = mapNotNull(Sector::parentId).toSet()

    fun depthOf(sector: Sector): Int =
        generateSequence(sector.parentId) { parentId ->
            sectorsById[parentId]?.parentId
        }.count()

    return map { sector ->
        SectorOptions(
            id = sector.id,
            name = sector.name,
            depth = depthOf(sector),
            isSelectable = sector.id !in parentIds
        )
    }
}
