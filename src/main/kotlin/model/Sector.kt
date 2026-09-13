package ee.kpikka.model

import kotlinx.serialization.Serializable

@Serializable
data class Sector(
    val id: Int,
    val name: String,
    val parentId: Int? = null,
)
