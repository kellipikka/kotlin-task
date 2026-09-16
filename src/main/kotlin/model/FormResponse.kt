package ee.kpikka.model

import kotlinx.serialization.Serializable

@Serializable
data class FormResponse(
    val id: Int? = null,
    val name: String,
    val agreedToTerms: Int,
    val sectorIds: List<Int> = emptyList(),
)