package com.rio.rostry.domain.trust

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.BirdPurpose
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates a documentation completeness score for a bird.
 *
 * Higher documentation → higher trust → justified higher price for adoption birds.
 * The score is displayed as a trust badge on marketplace listings.
 *
 * Scoring weights are tuned for ADOPTION-purpose birds where buyers need
 * full proof of lineage, health, and performance before paying premium prices.
 */
@Singleton
class DocumentationScoreCalculator @Inject constructor() {

    /**
     * Calculate the documentation score for a product/bird.
     *
     * @param product The bird entity
     * @param hasVaccinationRecords Whether vaccination records exist
     * @param hasFcrData Whether FCR tracking data exists
     * @param hasGrowthRecords Whether growth measurement records exist
     * @param pedigreeDepth How many generations of lineage are known (0-5+)
     * @param medicalRecordCount Number of medical event records
     * @param photoCount Number of photos attached
     */
    fun calculate(
        product: ProductEntity,
        hasVaccinationRecords: Boolean = false,
        hasFcrData: Boolean = false,
        hasGrowthRecords: Boolean = false,
        pedigreeDepth: Int = 0,
        medicalRecordCount: Int = 0,
        photoCount: Int = 0
    ): DocumentationScore {
        val breakdown = mutableMapOf<String, DocumentationItem>()

        // 1. Breed identified (10%)
        val breedKnown = !product.breed.isNullOrBlank()
        breakdown["breed"] = DocumentationItem("Breed Identified", breedKnown, 10)

        // 2. Birth date known (10%)
        val birthDateKnown = product.birthDate != null
        breakdown["birthDate"] = DocumentationItem("Birth Date Known", birthDateKnown, 10)

        // 3. Weight recorded (5%)
        val hasWeight = product.weightGrams != null && product.weightGrams > 0
        breakdown["weight"] = DocumentationItem("Weight Recorded", hasWeight, 5)

        // 4. Gender identified (5%)
        val genderKnown = !product.gender.isNullOrBlank() && product.gender != "unknown"
        breakdown["gender"] = DocumentationItem("Gender Identified", genderKnown, 5)

        // 5. Health status current (10%)
        val healthCurrent = !product.healthStatus.isNullOrBlank()
        breakdown["health"] = DocumentationItem("Health Status Current", healthCurrent, 10)

        // 6. Vaccinations logged (10%)
        breakdown["vaccinations"] = DocumentationItem("Vaccinations Logged", hasVaccinationRecords, 10)

        // 7. FCR tracked (10%)
        breakdown["fcr"] = DocumentationItem("FCR Tracked", hasFcrData, 10)

        // 8. Parent male known (10%)
        val maleParentKnown = !product.parentMaleId.isNullOrBlank()
        breakdown["parentMale"] = DocumentationItem("Father Known", maleParentKnown, 10)

        // 9. Parent female known (10%)
        val femaleParentKnown = !product.parentFemaleId.isNullOrBlank()
        breakdown["parentFemale"] = DocumentationItem("Mother Known", femaleParentKnown, 10)

        // 10. Photos uploaded (5%)
        val hasPhotos = photoCount > 0
        breakdown["photos"] = DocumentationItem("Photos Uploaded", hasPhotos, 5)

        // 11. Pedigree depth ≥ 2 generations (10%)
        val goodPedigree = pedigreeDepth >= 2
        breakdown["pedigree"] = DocumentationItem("Pedigree (2+ generations)", goodPedigree, 10)

        // 12. Medical history tracked (5%)
        val hasMedicalHistory = medicalRecordCount > 0 || hasVaccinationRecords
        breakdown["medicalHistory"] = DocumentationItem("Medical History", hasMedicalHistory, 5)

        // Calculate total score
        val earnedWeight = breakdown.values.filter { it.isComplete }.sumOf { it.weight }
        val totalWeight = breakdown.values.sumOf { it.weight }
        val score = if (totalWeight > 0) earnedWeight.toFloat() / totalWeight else 0f

        val badge = TrustBadge.fromScore(score)

        return DocumentationScore(
            overallScore = score,
            breakdown = breakdown,
            trustBadge = badge,
            completedItems = breakdown.values.count { it.isComplete },
            totalItems = breakdown.size,
            missingItems = breakdown.values.filter { !it.isComplete }.map { it.label }
        )
    }
}

/**
 * Result of documentation score calculation.
 */
data class DocumentationScore(
    val overallScore: Float,                     // 0.0 - 1.0
    val breakdown: Map<String, DocumentationItem>,
    val trustBadge: TrustBadge,
    val completedItems: Int,
    val totalItems: Int,
    val missingItems: List<String>               // Labels of what's missing
) {
    val percentComplete: Int get() = (overallScore * 100).toInt()
    val isFullyDocumented: Boolean get() = overallScore >= 0.8f
}

/**
 * Single documentation item with its completion status and weight.
 */
data class DocumentationItem(
    val label: String,
    val isComplete: Boolean,
    val weight: Int
)

/**
 * Trust badge levels displayed on marketplace listings.
 *
 * Higher badges = more documentation = more buyer confidence = justified higher price.
 */
enum class TrustBadge(
    val displayName: String,
    val minScore: Float,
    val emoji: String
) {
    NONE("Undocumented", 0f, ""),
    BASIC("Basic", 0.25f, "★"),
    VERIFIED("Verified", 0.50f, "★★"),
    PREMIUM("Premium", 0.80f, "★★★");

    companion object {
        fun fromScore(score: Float): TrustBadge = when {
            score >= PREMIUM.minScore -> PREMIUM
            score >= VERIFIED.minScore -> VERIFIED
            score >= BASIC.minScore -> BASIC
            else -> NONE
        }
    }
}
