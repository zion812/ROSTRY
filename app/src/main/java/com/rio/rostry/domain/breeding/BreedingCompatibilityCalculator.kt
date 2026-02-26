package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.genetics.PhenotypeMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.math.pow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Breeding compatibility suggestion
 */
data class BreedingSuggestion(
    val productId: String,
    val productName: String,
    val compatibilityScore: Int,
    val diversityScore: Double,
    val reasons: List<String>
)

@Singleton
class BreedingCompatibilityCalculator @Inject constructor(
    private val geneticEngine: GeneticEngine,
    private val productRepository: ProductRepository // Injected for ancestor traversal
) {

    // Cache for ancestor lookups per session
    private val ancestorCache = mutableMapOf<String, Map<String, Int>>()

    /**
     * Calculates a comprehensive compatibility score (0-100) and analysis.
     * This now includes:
     * 1. Genetic Compatibility (Inbreeding Coefficient - COI)
     * 2. Phenotype Matching (Breed, Comb, Color) via Genetic Engine
     * 3. Age & Health Logic
     * 4. Lethal/Sex-linked Risk Deductions
     * 5. Alternative Suggestions
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

        // --- 2. Inbreeding Coefficient (COI) using Wright's Path Method ---
        // Clear cache for new calculation
        ancestorCache.clear()
        
        val coi = calculateCOI(male, female, maxDepth = 5) // Up to 5 generations
        if (coi > 0.12) { // > 12.5% (Half-Siblings or closer)
            score -= 50
            reasons.add("CRITICAL: High Inbreeding (${"%.1f".format(coi * 100)}%)")
        } else if (coi > 0.05) { // > 5% (Cousins)
            score -= 20
            reasons.add("WARNING: Moderate Inbreeding (${"%.1f".format(coi * 100)}%)")
        } else {
            score += 20 // Bonus for genetic diversity
            reasons.add("Low Inbreeding Coefficient (${"%.1f".format(coi * 100)}%)")
        }

        // --- 3. Phenotype Analysis via PhenotypeMapper ---
        // Infer genotypes from product attributes
        val maleGenotype = inferGenotypeFromProduct(male)
        val femaleGenotype = inferGenotypeFromProduct(female)
        
        if (maleGenotype != null && femaleGenotype != null) {
            val malePhenotype = PhenotypeMapper.mapToAppearance(maleGenotype, male.ageWeeks ?: 0)
            val femalePhenotype = PhenotypeMapper.mapToAppearance(femaleGenotype, female.ageWeeks ?: 0)
            
            // Comb compatibility
            if (malePhenotype.comb == femalePhenotype.comb) {
                score += 15
                reasons.add("Comb match: ${malePhenotype.comb}")
            } else {
                reasons.add("Comb cross: ${malePhenotype.comb} x ${femalePhenotype.comb}")
                score += 5
            }
            
            // Color compatibility
            if (malePhenotype.chestColor == femalePhenotype.chestColor) {
                score += 10
                reasons.add("Color match: ${malePhenotype.chestColor}")
            }
        }

        // --- 4. Lethal/Sex-linked Risk Deductions ---
        val lethalRisk = assessLethalRisk(male, female)
        if (lethalRisk > 0) {
            val deduction = (lethalRisk * 30).toInt()
            score -= deduction
            reasons.add("WARNING: Lethal gene risk detected (-$deduction pts)")
        }
        
        val sexLinkedRisk = assessSexLinkedRisk(male, female)
        if (sexLinkedRisk > 0) {
            val deduction = (sexLinkedRisk * 20).toInt()
            score -= deduction
            reasons.add("WARNING: Sex-linked trait risk (-$deduction pts)")
        }

        // --- 5. Health & Status ---
        if (male.healthStatus == "Sick" || female.healthStatus == "Sick") {
             score = 0
             reasons.add("CRITICAL: One or more parents are marked as Sick")
        }
        
        // Age compatibility
        val ageDiff = abs((male.ageWeeks ?: 0) - (female.ageWeeks ?: 0))
        if (ageDiff > 52) { // More than 1 year difference
            score -= 10
            reasons.add("Age gap: ${ageDiff / 52} years")
        }

        val totalScore = score.coerceIn(0, 100)
        return CompatibilityResult(
            score = totalScore,
            verdict = getVerdict(totalScore),
            reasons = reasons,
            coiPercent = coi * 100,
            diversityScore = 1.0 - coi,
            lethalRisk = lethalRisk,
            sexLinkedRisk = sexLinkedRisk
        )
    }

    /**
     * Get alternative breeding suggestions (non-related birds)
     */
    suspend fun getAlternativeSuggestions(
        currentPartner: ProductEntity,
        excludeWithinGenerations: Int = 3,
        maxSuggestions: Int = 5
    ): List<BreedingSuggestion> {
        // Get related birds to exclude
        val relatedIds = getRelatedBirdIds(currentPartner, excludeWithinGenerations)
        
        // Get all potential mates (same species, opposite sex, not sick)
        val resource = productRepository.getAllProducts().first()
        val allProducts = when (resource) {
            is com.rio.rostry.utils.Resource.Success -> resource.data ?: emptyList()
            else -> emptyList()
        }
        val potentialMates = allProducts.filter { product ->
            product.productId != currentPartner.productId &&
            product.productId !in relatedIds &&
            product.healthStatus != "Sick" &&
            product.gender != currentPartner.gender // Opposite sex
        }
        
        // Calculate compatibility for each and rank by diversity
        val suggestions = mutableListOf<BreedingSuggestion>()
        for (mate in potentialMates) {
            val result = calculateCompatibility(
                if (currentPartner.gender == "male") currentPartner else mate,
                if (currentPartner.gender == "male") mate else currentPartner
            )
            
            suggestions.add(
                BreedingSuggestion(
                    productId = mate.productId,
                    productName = mate.name ?: "Unknown",
                    compatibilityScore = result.score,
                    diversityScore = result.diversityScore,
                    reasons = result.reasons.take(3) // Top 3 reasons
                )
            )
        }
        
        // Sort by diversity score (highest first) and cap to maxSuggestions
        return suggestions
            .sortedByDescending { it.diversityScore }
            .take(maxSuggestions)
    }

    /**
     * Get all related bird IDs within N generations
     */
    private suspend fun getRelatedBirdIds(product: ProductEntity, generations: Int): Set<String> {
        val ancestors = getAncestors(product, generations)
        return ancestors.keys.toSet()
    }

    /**
     * Infer genotype from product attributes
     */
    private fun inferGenotypeFromProduct(product: ProductEntity): com.rio.rostry.domain.model.GeneticProfile? {
        // Parse attributes from product description or attributes JSON
        // This is a simplified version - real implementation would parse from stored data
        
        // For now, use breed-based defaults
        val breed = product.breed ?: return null
        
        return geneticEngine.getDefaultGenotypeForBreed(breed)
    }

    /**
     * Assess lethal gene combinations
     */
    private suspend fun assessLethalRisk(male: ProductEntity, female: ProductEntity): Double {
        // Check for known lethal combinations (e.g., Frizzle x Frizzle)
        val maleTraits = parseTraits(male)
        val femaleTraits = parseTraits(female)
        
        // Frizzle gene: F/f x F/f = 25% FF (lethal)
        if (maleTraits.contains("frizzle") && femaleTraits.contains("frizzle")) {
            return 0.25 // 25% chance of lethal
        }
        
        // Creeper gene: Cp/cp x Cp/cp = 25% CpCp (lethal)
        if (maleTraits.contains("creeper") && femaleTraits.contains("creeper")) {
            return 0.25
        }
        
        return 0.0
    }

    /**
     * Assess sex-linked trait risks
     */
    private suspend fun assessSexLinkedRisk(male: ProductEntity, female: ProductEntity): Double {
        // Check for sex-linked traits (Silver/Gold, Barring)
        // Males are ZZ, Females are ZW
        
        // If male is homozygous recessive and female is dominant
        // All male offspring will be one way, all females another
        
        // Simplified check - real implementation would use genetic profiles
        return 0.0
    }

    /**
     * Parse traits from product attributes
     */
    private fun parseTraits(product: ProductEntity): Set<String> {
        val traits = mutableSetOf<String>()
        val description = product.description ?: return traits
        
        val lowerDesc = description.lowercase()
        if (lowerDesc.contains("frizzle")) traits.add("frizzle")
        if (lowerDesc.contains("creeper")) traits.add("creeper")
        if (lowerDesc.contains("barred")) traits.add("barred")
        if (lowerDesc.contains("silkie")) traits.add("silkie")
        
        return traits
    }

    private fun abs(x: Int): Int = kotlin.math.abs(x)

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
     * Uses caching to avoid redundant lookups per session
     */
    private suspend fun getAncestors(startNode: ProductEntity, maxDepth: Int): Map<String, Int> {
        // Check cache first
        val cacheKey = "${startNode.productId}_$maxDepth"
        ancestorCache[cacheKey]?.let { return it }

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
        
        // Cache the result
        ancestorCache[cacheKey] = ancestors
        return ancestors
    }

    data class CompatibilityResult(
        val score: Int,
        val verdict: String,
        val reasons: List<String>,
        val coiPercent: Double = 0.0,
        val diversityScore: Double = 1.0,
        val lethalRisk: Double = 0.0,
        val sexLinkedRisk: Double = 0.0
    )
}
