package ee.kpikka.service

import ee.kpikka.model.FormResponse
import ee.kpikka.repository.FormResponseRepository
import io.ktor.http.*

object FormService {
    suspend fun getExistingOrNewFormResponse(responseId: Int?): FormResponse {
        return responseId?.let { FormResponseRepository.getResponse(it) } ?: FormResponse(
            name = "",
            agreedToTerms = 0
        )
    }

    suspend fun postFormResponse(formContent: Parameters, responseId: Int?): Int {
        val name = formContent["name"] ?: ""
        val sectors = formContent.getAll("sectors")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
        val terms = formContent["terms"]?.toInt() ?: 0

        val formResponseId = FormResponseRepository.saveResponse(
            FormResponse(
                id = responseId,
                name = name,
                agreedToTerms = terms,
                sectorIds = sectors
            )
        )

        return formResponseId
    }
}
