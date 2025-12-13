package com.rio.rostry.marketplace.form

import com.rio.rostry.marketplace.model.AgeGroup
import com.rio.rostry.marketplace.model.ProductCategory

object DynamicListingValidator {

    data class Input(
        val category: ProductCategory,
        val ageGroup: AgeGroup,
        val isTraceable: Boolean,
        val title: String,
        val birthDateMillis: Long?,
        val birthPlace: String?,
        val vaccinationRecords: String?,
        val healthRecordDateMillis: Long?,
        val parentInfo: String?,
        val weightGrams: Double?,
        val healthRecords: String?,
        val gender: String?,
        val sizeCm: Double?,
        val colorPattern: String?,
        val specialCharacteristics: String?,
        val breedingHistory: String?,
        val provenPairsDoc: String?,
        val geneticTraits: String?,
        val awards: String?,
        val lineageDoc: String?,
        val photosCount: Int,
        val videosCount: Int,
        val audioCount: Int,
        val documentsCount: Int,
        val price: Double?,
        val startPrice: Double?,
        val latitude: Double?,
        val longitude: Double?,
        val hasRecentVaccination: Boolean?,
        val hasRecentHealthLog: Boolean?,
        val hasRecentGrowthRecord: Boolean?
    )

    data class ValidationResult(
        val valid: Boolean,
        val errors: List<String>
    )

    fun validate(input: Input): ValidationResult {
        val errors = mutableListOf<String>()

        if (input.title.isBlank()) {
            errors.add("Title is required")
        }

        if (input.price == null && input.startPrice == null) {
            errors.add("Price is required")
        }

        if (input.isTraceable) {
            if (input.parentInfo.isNullOrBlank() && input.lineageDoc.isNullOrBlank()) {
                errors.add("Parent info or lineage document is required for traceable products")
            }
        }

        if (input.ageGroup == AgeGroup.CHICK_0_5_WEEKS) {
             if (input.vaccinationRecords.isNullOrBlank()) {
                 errors.add("Vaccination records are required for chicks")
             }
        }

        // Add more validation logic as needed based on requirements
        
        return ValidationResult(errors.isEmpty(), errors)
    }
}
