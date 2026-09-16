package ee.kpikka.model.request

import io.ktor.http.*

data class FormRequest(
    val name: String,
    val agreedToTerms: Int,
    val sectorIds: List<Int> = emptyList(),
) {

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
