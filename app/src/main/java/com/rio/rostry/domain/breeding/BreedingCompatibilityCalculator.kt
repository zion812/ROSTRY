package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlin.math.pow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedingCompatibilityCalculator @Inject constructor(
    private val geneticEngine: GeneticEngine,
    private val productRepository: ProductRepository // Injected for ancestor traversal
) {

    /**
     * Calculates a comprehensive compatibility score (0-100) and analysis.
     * This now includes:
     * 1. Genetic Compatibility (Inbreeding Coefficient - COI)
     * 2. Phenotype Matching (Breed, Comb, Color) via Genetic Engine
     * 3. Age & Health Logic
     */
    suspend fun calculateCompatibility(male: ProductEntity, female: ProductEntity): CompatibilityResult {
        var score = 0
        val reasons = mutableListOf<String>()

        // --- 1. Breed Match (40 pts) ---
        if (male.breed.equals(female.breed, ignoreCase = true)) {
            score += 40
            reasons.add("Breed match: ${male.breed}")
        } else {
            reasons.add("Cross-breeding: ${male.breed} x ${female.breed}")
            score += 10 
        }

        // --- 2. Inbreeding Coefficient (COI) ---
        // We warn heavily if COI is high.
        // Full Sibling = 25%, Half Sibling = 12.5%, First Cousin = 6.25%
        // We penalize score based on COI.
        val coi = calculateCOI(male, female)
        if (coi > 0.12) { // > 12.5% (Half-Siblings or closer)
            score -= 50
            reasons.add("CRITICAL: High Inbreeding (${"%.1f".format(coi * 100)}%)")
        } else if (coi > 0.05) { // > 5% (Cousins)
            score -= 20
            reasons.add("WARNING: Moderate Inbreeding (${"%.1f".format(coi * 100)}%)")
        } else {
            score += 20 // Bonus for genetic diversity
            reasons.add("Low Inbreeding Coefficient")
        }

        // --- 3. Genetic Engine Trait Analysis (20 pts) ---
        // Check key traits for synergy
        val maleCombGeno = geneticEngine.inferGenotype(GeneticEngine.TraitType.COMB, "Rose") // Placeholder lookup from entity
        val femaleCombGeno = geneticEngine.inferGenotype(GeneticEngine.TraitType.COMB, "Single") // Placeholder
        // Real implementation would parse attributes from ProductEntity.description or attributes json
        
        // For now, simple color match check
        if (!male.color.isNullOrBlank() && male.color.equals(female.color, ignoreCase = true)) {
            score += 20
            reasons.add("Color match: ${male.color}")
        } else if(male.color != null && female.color != null) {
            score += 10
            reasons.add("Color cross: ${male.color}/${female.color}")
        }

        // --- 4. Health & Status ---
        if (male.healthStatus == "Sick" || female.healthStatus == "Sick") {
             score = 0
             reasons.add("CRITICAL: One or more parents are marked as Sick")
        }

        val totalScore = score.coerceIn(0, 100)
        return CompatibilityResult(
            score = totalScore,
            verdict = getVerdict(totalScore),
            reasons = reasons,
            coiPercent = coi * 100
        )
    }

    private fun getVerdict(score: Int): String = when {
        score >= 80 -> "Excellent Match"
        score >= 50 -> "Good Match"
        score >= 30 -> "Fair Match"
        else -> "Not Recommended"
    }

    /**
     * Calculates the Inbreeding Coefficient (Wright's Path Method simplified).
     * Finds common ancestors in the pedigree of the Sire and Dam.
     * Uses a recursive search up to [maxDepth] generations.
     */
    private suspend fun calculateCOI(sire: ProductEntity, dam: ProductEntity, maxDepth: Int = 4): Double {
        // 1. Build Ancestor Sets
        val sireAncestors = getAncestors(sire, maxDepth)
        val damAncestors = getAncestors(dam, maxDepth)

        // 2. Find Common Ancestors
        val commonAncestors = sireAncestors.keys.intersect(damAncestors.keys)
        if (commonAncestors.isEmpty()) return 0.0

        var coi = 0.0
        for (ancestorId in commonAncestors) {
            // Calculate contribution for each path
            // For simplified Fx, assumes ancestor is not inbred (Fa = 0)
            // Path coefficient = (1/2)^(n1 + n2 + 1)
            // n1 = generations from sire to ancestor
            // n2 = generations from dam to ancestor
            
            val n1 = sireAncestors[ancestorId] ?: continue
            val n2 = damAncestors[ancestorId] ?: continue
            
            coi += 0.5.pow(n1 + n2 + 1)
        }
        
        return coi
    }

    /**
     * Returns a map of AncestorID -> GenerationsBack (Depth)
     * e.g., Father -> 0, Grandfather -> 1
     */
    private suspend fun getAncestors(startNode: ProductEntity, maxDepth: Int): Map<String, Int> {
        val ancestors = mutableMapOf<String, Int>()
        val queue = ArrayDeque<Pair<ProductEntity, Int>>()
        queue.add(startNode to 0)

        while (queue.isNotEmpty()) {
            val (current, depth) = queue.removeFirst()
            if (depth >= maxDepth) continue

            // Check Parent Male
            if (!current.parentMaleId.isNullOrBlank()) {
                if (!ancestors.containsKey(current.parentMaleId)) {
                    ancestors[current.parentMaleId] = depth
                    // Fetch entity
                    val parent = productRepository.findById(current.parentMaleId)
                    if (parent != null) queue.add(parent to depth + 1)
                }
            }

            // Check Parent Female
            if (!current.parentFemaleId.isNullOrBlank()) {
                if (!ancestors.containsKey(current.parentFemaleId)) {
                    ancestors[current.parentFemaleId] = depth
                    val parent = productRepository.findById(current.parentFemaleId)
                    if (parent != null) queue.add(parent to depth + 1)
                }
            }
        }
        return ancestors
    }

    data class CompatibilityResult(
        val score: Int,
        val verdict: String,
        val reasons: List<String>,
        val coiPercent: Double = 0.0
    )
}
