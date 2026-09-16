package ee.kpikka.service

import ee.kpikka.model.FormResponse
import ee.kpikka.repository.FormResponseRepository
import io.ktor.http.*

object FormService {
    suspend fun getFormResponse(responseId: Int): FormResponse {
        val response = FormResponseRepository.getResponse(responseId)
        return checkNotNull(response) { "Response not found" }
    }

    suspend fun getExistingOrNewFormResponse(responseId: Int?): FormResponse {
        return responseId?.let { getFormResponse(it) } ?: FormResponse(
            name = "",
            agreedToTerms = 0
        )
    }

    suspend fun postFormResponse(formContent: Parameters, responseId: Int?): Int {
        val name = formContent["name"] ?: ""
        val sectors = formContent.getAll("sectors") ?: emptyList()
        val terms = formContent["terms"]?.toInt() ?: 0

        val formResponseId = FormResponseRepository.saveResponse(
            FormResponse(
                id = responseId,
                name = name,
                agreedToTerms = terms,
            )
        )
        FormResponseRepository.saveSectors(formResponseId, sectors)

        return formResponseId
    }
}