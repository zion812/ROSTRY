package com.rio.rostry.marketplace.form

import com.rio.rostry.marketplace.model.AgeGroup
import com.rio.rostry.marketplace.model.ProductCategory

/**
 * Validates dynamic listing form inputs based on age group and category.
 * Includes validation for farm data freshness: vaccination within 30 days, health log within 7 days, growth record within 14 days for traceable adoptions.
 * Rationale: Ensures products meet health and traceability standards before listing to prevent invalid or outdated listings.
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
        val longitude: Double?,
        val hasRecentVaccination: Boolean? = null,
        val hasRecentHealthLog: Boolean? = null,
        val hasRecentGrowthRecord: Boolean? = null
    )

    fun validate(input: Input): Result {
        val errors = mutableListOf<String>()

        if (input.title.isNullOrBlank()) errors += "Title is required"
        // Base photo requirement varies by traceability
        if (input.isTraceable) {
            if (input.photosCount < 3) errors += "At least 3 photos required for traceable listings"
        } else {
            if (input.photosCount < 1) errors += "At least 1 photo is required"
        }
        // GPS requirement: mandatory only for traceable adoption; otherwise recommend but do not block
        if (input.latitude == null || input.longitude == null) {
            if (input.category is ProductCategory.AdoptionTraceable) {
                errors += "GPS location required for traceable adoption"
            }
        }

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
                // Birth date should be within last 30 days
                input.birthDateMillis?.let { b ->
                    val thirtyDays = 30L * 24 * 60 * 60 * 1000
                    if (now - b > thirtyDays) errors += "Birth date must be within the last 30 days for chicks"
                }
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

        // Traceability additional constraints (family-tree proxy via lineageDoc, photos, vaccination, weight & height)
        if (input.isTraceable) {
            if (input.lineageDoc.isNullOrBlank() && input.parentInfo.isNullOrBlank()) errors += "Lineage information (parents or document) required for traceable listing"
            if (input.vaccinationRecords.isNullOrBlank()) errors += "Vaccination records required for traceable listing"
            if (input.weightGrams == null || input.sizeCm == null) errors += "Both weight and height required for traceable listing"
        }

        // Non-traceable requirements: basic details already partially covered above, ensure min photo
        if (!input.isTraceable && input.photosCount < 1) {
            errors += "At least 1 photo required for non-traceable listing"
        }

        // Category-specific: Meat requires weight, age (birthDate), health records
        if (input.category is ProductCategory.Meat) {
            if (input.weightGrams == null) errors += "Weight required for meat category"
            if (input.birthDateMillis == null) errors += "Age (birth date) required for meat category"
            if (input.healthRecords.isNullOrBlank()) errors += "Health records required for meat category"
        }

        // Farm data freshness validation
        when (input.category) {
            is ProductCategory.AdoptionTraceable -> {
                if (input.hasRecentVaccination == false) errors += "Recent vaccination record (within 30 days) required for traceable adoption"
                if (input.hasRecentHealthLog == false) errors += "Recent health log (within 7 days) required for traceable adoption"
                if (input.hasRecentGrowthRecord == false) errors += "Recent growth monitoring (within 2 weeks) required for traceable adoption"
            }
            is ProductCategory.Meat -> {
                if (input.hasRecentHealthLog == false) errors += "Recent health log required for meat category"
            }
            else -> Unit
        }

        // Price sanity
        val p = input.price ?: input.startPrice
        if (p == null || p <= 0.0) errors += "Valid price/start price required"

        // Media size constraints left to MediaManager; counts checked here
        return Result(valid = errors.isEmpty(), errors = errors)
    }
}
