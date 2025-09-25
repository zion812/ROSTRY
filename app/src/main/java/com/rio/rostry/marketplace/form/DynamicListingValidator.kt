package com.rio.rostry.marketplace.form

import com.rio.rostry.marketplace.model.AgeGroup
import com.rio.rostry.marketplace.model.ProductCategory

/**
 * Validates dynamic listing form inputs based on age group and category.
 */
object DynamicListingValidator {
    data class Result(val valid: Boolean, val errors: List<String>)

    data class Input(
        val category: ProductCategory,
        val ageGroup: AgeGroup,
        val isTraceable: Boolean,
        val title: String?,
        val birthDateMillis: Long?,
        val birthPlace: String?,
        val vaccinationRecords: String?,
        val healthRecordDateMillis: Long? = null,
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
        val longitude: Double?
    )

    fun validate(input: Input): Result {
        val errors = mutableListOf<String>()

        if (input.title.isNullOrBlank()) errors += "Title is required"
        if (input.photosCount < 2) errors += "At least 2 photos required"
        if (input.latitude == null || input.longitude == null) errors += "GPS location required"

        // Date validations
        val now = System.currentTimeMillis()
        input.birthDateMillis?.let {
            if (it > now) errors += "Birth date cannot be in the future"
        }
        input.healthRecordDateMillis?.let { d ->
            if (d > now) errors += "Health record date cannot be in the future"
            input.birthDateMillis?.let { b ->
                if (d < b) errors += "Health record date must be after birth date"
            }
        }

        // Category + traceability
        when (input.category) {
            is ProductCategory.Meat -> Unit
            is ProductCategory.AdoptionNonTraceable -> Unit
            is ProductCategory.AdoptionTraceable -> {
                if (!input.isTraceable) errors += "Traceability flag must be ON for traceable category"
                if (input.lineageDoc.isNullOrBlank()) errors += "Lineage documentation required for traceable adoption"
            }
        }

        // Age group specific requirements
        when (input.ageGroup) {
            AgeGroup.CHICK_0_5_WEEKS -> {
                if (input.birthDateMillis == null) errors += "Birth date required"
                if (input.birthPlace.isNullOrBlank()) errors += "Birth place required"
                if (input.vaccinationRecords.isNullOrBlank()) errors += "Vaccination records mandatory"
                if (input.isTraceable && input.parentInfo.isNullOrBlank()) errors += "Parent bird information required for traceable chicks"
            }
            AgeGroup.YOUNG_5_20_WEEKS -> {
                // All chick requirements + growth
                if (input.birthDateMillis == null) errors += "Birth date required"
                if (input.birthPlace.isNullOrBlank()) errors += "Birth place required"
                if (input.vaccinationRecords.isNullOrBlank()) errors += "Vaccination records mandatory"
                if (input.weightGrams == null) errors += "Weight (growth monitoring) required"
                if (input.healthRecords.isNullOrBlank()) errors += "Health records required"
                // Gender identification if determinable is optional; skip hard enforcement
            }
            AgeGroup.ADULT_20_52_WEEKS -> {
                // All previous + final gender confirmation, size, color, special
                if (input.gender.isNullOrBlank() || input.gender.equals("unknown", true)) errors += "Final gender confirmation required"
                if (input.sizeCm == null && input.weightGrams == null) errors += "Size or weight measurement required"
                if (input.colorPattern.isNullOrBlank()) errors += "Color pattern documentation required"
                if (input.specialCharacteristics.isNullOrBlank()) errors += "Special characteristics required"
            }
            AgeGroup.BREEDER_12_MONTHS_PLUS -> {
                if (input.breedingHistory.isNullOrBlank()) errors += "Complete breeding history required"
                if (input.provenPairsDoc.isNullOrBlank()) errors += "Proven breeding pairs documentation required"
                if (input.geneticTraits.isNullOrBlank()) errors += "Genetic trait records required"
                if (input.awards.isNullOrBlank()) errors += "Championship or award records required"
                if (input.lineageDoc.isNullOrBlank()) errors += "Detailed lineage documentation required"
            }
        }

        // Price sanity
        val p = input.price ?: input.startPrice
        if (p == null || p <= 0.0) errors += "Valid price/start price required"

        // Media size constraints left to MediaManager; counts checked here
        return Result(valid = errors.isEmpty(), errors = errors)
    }
}
