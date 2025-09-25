package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.marketplace.model.AgeGroup
import com.rio.rostry.marketplace.model.ProductCategory

/**
 * Validates product listings for marketplace rules.
 */
object ProductValidator {

    data class ValidationResult(
        val valid: Boolean,
        val reasons: List<String> = emptyList()
    )

    fun validate(product: ProductEntity, now: Long = System.currentTimeMillis()): ValidationResult {
        val reasons = mutableListOf<String>()

        // Basic required fields
        if (product.name.isBlank()) reasons += "Name is required"
        if (product.price <= 0.0) reasons += "Price must be positive"
        if (product.quantity < 0.0) reasons += "Quantity cannot be negative"

        // Location verification for farmers
        if (product.latitude == null || product.longitude == null) {
            reasons += "Accurate location (latitude/longitude) is required"
        }

        // Image quality/count
        if (product.imageUrls.size < 2) reasons += "At least 2 product images are required"

        // Category mapping and rules
        val category = ProductCategory.fromString(product.category)
        val ageGroup = AgeGroup.fromBirthDate(product.birthDate, now)

        // Age group validations
        when (ageGroup) {
            AgeGroup.CHICK_0_5_WEEKS -> {
                if (product.vaccinationRecordsJson.isNullOrBlank()) {
                    reasons += "Vaccination records are required for 0-5 weeks"
                }
            }
            AgeGroup.YOUNG_5_20_WEEKS -> {
                // Require growth monitoring: at least weight or height present
                if (product.weightGrams == null && product.heightCm == null) {
                    reasons += "Growth monitoring data (weight/height) required for 5-20 weeks"
                }
            }
            AgeGroup.ADULT_20_52_WEEKS -> {
                val gender = product.gender?.lowercase()
                if (gender.isNullOrBlank() || gender == "unknown") {
                    reasons += "Gender identification required for 20-52 weeks"
                }
            }
            AgeGroup.BREEDER_12_MONTHS_PLUS -> {
                if (product.breedingStatus.isNullOrBlank()) {
                    reasons += "Breeding history/status required for 12+ months"
                }
            }
            null -> Unit
        }

        // Category-specific requirements
        when (category) {
            is ProductCategory.Meat -> {
                // Meat should not require family tree
                if (!product.familyTreeId.isNullOrBlank()) {
                    // allow but warn? We'll not invalidate
                }
            }
            is ProductCategory.AdoptionTraceable -> {
                if (product.familyTreeId.isNullOrBlank()) {
                    reasons += "Family tree documentation is required for traceable adoption"
                }
            }
            is ProductCategory.AdoptionNonTraceable -> {
                // No lineage required
            }
            null -> {
                reasons += "Unknown or unsupported category"
            }
        }

        // Price reasonableness heuristic
        val price = product.price
        val suspicious = when (ageGroup) {
            AgeGroup.CHICK_0_5_WEEKS -> price !in 50.0..2000.0
            AgeGroup.YOUNG_5_20_WEEKS -> price !in 200.0..10000.0
            AgeGroup.ADULT_20_52_WEEKS -> price !in 500.0..20000.0
            AgeGroup.BREEDER_12_MONTHS_PLUS -> price !in 1000.0..50000.0
            null -> price <= 0.0
        }
        if (suspicious) reasons += "Price appears unreasonable for the selected age group"

        return ValidationResult(valid = reasons.isEmpty(), reasons = reasons)
    }
}
