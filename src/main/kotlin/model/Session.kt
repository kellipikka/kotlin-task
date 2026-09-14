package ee.kpikka.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: Int? = null,
    val name: String,
    val agreedToTerms: Int,
)