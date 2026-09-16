package ee.kpikka.model.request

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class FormRequest(
    val name: String,
    val agreedToTerms: Int,
    val sectorIds: List<Int> = emptyList(),
) {
    init {
        require(name.isNotBlank()) { "Name cannot be empty" }
        require(agreedToTerms == 1) { "You must agree to the terms" }
        require(sectorIds.isNotEmpty()) { "At least one sector must be selected" }
        require(sectorIds.size <= 100) { "No more than 100 sectors can be selected" }
    }

    companion object {
        fun from(parameters: Parameters): FormRequest {
            return FormRequest(
                name = parameters["name"] ?: "",
                agreedToTerms = parameters["terms"]?.toIntOrNull() ?: 0,
                sectorIds = parameters.getAll("sectors")
                    ?.map { sectorId ->
                        requireNotNull(sectorId.toIntOrNull()) { "Sector IDs must be integers" }
                    }
                    ?: emptyList()
            )
        }
    }
}
