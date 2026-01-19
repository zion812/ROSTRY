package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedingCompatibilityCalculator @Inject constructor() {

    fun calculateCompatibility(male: ProductEntity, female: ProductEntity): CompatibilityResult {
        var score = 0
        val reasons = mutableListOf<String>()

        // 1. Breed Match (High importance)
        if (male.breed.equals(female.breed, ignoreCase = true)) {
            score += 40
            reasons.add("Breed match: ${male.breed}")
        } else {
            reasons.add("Cross-breeding: ${male.breed} x ${female.breed}")
            score += 10 // Still possible, but lower baseline
        }

        // 2. Lineage Check (Avoid inbreeding) -> Placeholder logic
        // Real logic would traverse detailed pedigree.
        if (male.parentMaleId == female.parentMaleId && male.parentMaleId != null) {
            score -= 50
            reasons.add("WARNING: Shared father (Half-siblings)")
        }
        if (male.parentFemaleId == female.parentFemaleId && male.parentFemaleId != null) {
            score -= 50
            reasons.add("WARNING: Shared mother (Half-siblings)")
        }

        // 3. Age Compatibility
        // Placeholder: Assume adults for now. Real logic checks age in months.
        
        // 4. Color Logic (Optional)
        if (male.color.equals(female.color, ignoreCase = true)) {
            score += 20
            reasons.add("Color match: ${male.color}")
        }

        val totalScore = score.coerceIn(0, 100)
        return CompatibilityResult(
            score = totalScore,
            verdict = when {
                totalScore >= 80 -> "Excellent Match"
                totalScore >= 50 -> "Good Match"
                totalScore >= 20 -> "Fair Match"
                else -> "Not Recommended"
            },
            reasons = reasons
        )
    }

    data class CompatibilityResult(
        val score: Int,
        val verdict: String,
        val reasons: List<String>
    )
}
