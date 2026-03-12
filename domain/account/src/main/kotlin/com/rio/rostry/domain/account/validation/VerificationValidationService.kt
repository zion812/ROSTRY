package com.rio.rostry.domain.account.validation

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String, val field: String? = null) : ValidationResult()
    data class Warning(val message: String) : ValidationResult()
}

data class ExifValidationResult(
    val hasGPS: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val timestampMillis: Long?,
    val isValid: Boolean,
    val errorMessage: String? = null,
)

interface VerificationValidationService {
    suspend fun validateImageFile(uriString: String): ValidationResult
    suspend fun validateDocumentFile(uriString: String): ValidationResult
    suspend fun validateFarmPhotoLocation(
        imageUri: String,
        claimedLat: Double?,
        claimedLng: Double?
    ): ExifValidationResult
    
    fun validateFarmerSubmission(
        lat: Double?,
        lng: Double?,
        uploadedImages: List<String>,
        uploadedDocuments: List<String>,
        uploadedImageTypes: Map<String, String>,
        uploadedDocTypes: Map<String, String>,
        requiredImages: List<String>,
        requiredDocuments: List<String>
    ): ValidationResult
    
    fun validateEnthusiastSubmission(
        uploadedImages: List<String>,
        uploadedDocuments: List<String>,
        uploadedImageTypes: Map<String, String>,
        uploadedDocTypes: Map<String, String>,
        requiredImages: List<String>,
        requiredDocuments: List<String>
    ): ValidationResult
    
    suspend fun checkDuplicateSubmission(userId: String?): Boolean
}
