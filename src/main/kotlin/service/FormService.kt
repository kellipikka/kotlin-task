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
        val submittedSectorIds = formRequest.sectorIds.distinct()
        val sectors = SectorService.filterSelectableSectorIds(submittedSectorIds)

        validateFormRequest(formRequest, submittedSectorIds, sectors)
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

    private fun validateFormRequest(
        formRequest: FormRequest,
        submittedSectorIds: List<Int>,
        validSectors: List<Int>,
    ) {
        val name = formRequest.name.trim()

        require(name.isNotBlank()) { "Name cannot be empty." }
        require(name.length <= 100) { "Name must not exceed 100 characters." }

        require(formRequest.agreedToTerms == 1) { "You must agree to the terms." }

        require(validSectors.isNotEmpty()) { "Selected sectors are invalid." }

        require(validSectors.size == submittedSectorIds.size) {
            "One or more selected sectors are invalid or cannot be chosen."
        }
    }
}
