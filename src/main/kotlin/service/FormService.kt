package ee.kpikka.service

import ee.kpikka.model.FormResponse
import ee.kpikka.model.request.FormRequest
import ee.kpikka.repository.FormResponseRepository

object FormService {
    suspend fun getExistingOrNewFormResponse(responseId: Int?): FormResponse {
        return responseId?.let { FormResponseRepository.getResponse(it) } ?: FormResponse(
            name = "",
            agreedToTerms = 0,
            sectorIds = emptyList(),
        )
    }

    suspend fun postFormResponse(formRequest: FormRequest, responseId: Int?): Int {
        val sectors = SectorService.filterSelectableSectorIds(formRequest.sectorIds)

        val formResponseId = FormResponseRepository.saveResponse(
            FormResponse(
                id = responseId,
                name = formRequest.name.trim(),
                agreedToTerms = formRequest.agreedToTerms,
                sectorIds = sectors
            )
        )

        return formResponseId
    }
}
