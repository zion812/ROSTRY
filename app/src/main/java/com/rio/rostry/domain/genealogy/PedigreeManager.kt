package com.rio.rostry.domain.genealogy

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PedigreeManager - Advanced genealogy tracking for birds.
 * 
 * Provides:
 * - Recursive ancestor/descendant traversal
 * - Pedigree tree construction (up to N generations)
 * - Inbreeding coefficient calculation (Wright's formula)
 * - Common ancestor detection
 * - Lineage verification and completeness scoring
 */
@Singleton
class PedigreeManager @Inject constructor(
    private val productRepository: ProductRepository
) {

    /**
     * Data class representing a node in the pedigree tree.
     */
    data class PedigreeNode(
        val bird: ProductEntity?,
        val generation: Int,
        val position: PedigreePosition,
        val sire: PedigreeNode? = null,    // Male parent
        val dam: PedigreeNode? = null      // Female parent
    )

    enum class PedigreePosition {
        SUBJECT, SIRE, DAM, PATERNAL_GRANDSIRE, PATERNAL_GRANDDAM,
        MATERNAL_GRANDSIRE, MATERNAL_GRANDDAM
    }

    /**
     * Lineage quality score based on completeness.
     */
    data class LineageScore(
        val totalScore: Int,              // 0-100
        val generationsComplete: Int,     // How many full generations
        val knownAncestors: Int,          // Total known ancestors
        val maxPossibleAncestors: Int,    // Max for requested depth
        val recommendation: String        // Text recommendation
    )

    /**
     * Build a pedigree tree for a bird up to specified generations.
     * 
     * @param birdId The subject bird's ID
     * @param generations Number of ancestor generations to include (default 3)
     * @return PedigreeNode tree or null if bird not found
     */
    suspend fun buildPedigree(birdId: String, generations: Int = 3): PedigreeNode? {
        val bird = productRepository.getById(birdId) ?: return null
        return buildPedigreeRecursive(bird, 0, generations, PedigreePosition.SUBJECT)
    }

    private suspend fun buildPedigreeRecursive(
        bird: ProductEntity?,
        currentGen: Int,
        maxGen: Int,
        position: PedigreePosition
    ): PedigreeNode? {
        if (bird == null || currentGen >= maxGen) {
            return bird?.let { PedigreeNode(it, currentGen, position) }
        }

        val sire = bird.parentMaleId?.let { productRepository.getById(it) }
        val dam = bird.parentFemaleId?.let { productRepository.getById(it) }

        val sireNode = buildPedigreeRecursive(
            sire, currentGen + 1, maxGen,
            when (position) {
                PedigreePosition.SUBJECT -> PedigreePosition.SIRE
                PedigreePosition.SIRE -> PedigreePosition.PATERNAL_GRANDSIRE
                PedigreePosition.DAM -> PedigreePosition.MATERNAL_GRANDSIRE
                else -> position
            }
        )

        val damNode = buildPedigreeRecursive(
            dam, currentGen + 1, maxGen,
            when (position) {
                PedigreePosition.SUBJECT -> PedigreePosition.DAM
                PedigreePosition.SIRE -> PedigreePosition.PATERNAL_GRANDDAM
                PedigreePosition.DAM -> PedigreePosition.MATERNAL_GRANDDAM
                else -> position
            }
        )

        return PedigreeNode(
            bird = bird,
            generation = currentGen,
            position = position,
            sire = sireNode,
            dam = damNode
        )
    }

    /**
     * Get all ancestors of a bird up to a specified depth.
     * Returns a flat list of ancestors with their generation level.
     */
    suspend fun getAncestors(birdId: String, maxGenerations: Int = 5): List<Pair<ProductEntity, Int>> {
        val ancestors = mutableListOf<Pair<ProductEntity, Int>>()
        val visited = mutableSetOf<String>()
        collectAncestorsRecursive(birdId, 1, maxGenerations, ancestors, visited)
        return ancestors
    }

    private suspend fun collectAncestorsRecursive(
        birdId: String,
        currentGen: Int,
        maxGen: Int,
        ancestors: MutableList<Pair<ProductEntity, Int>>,
        visited: MutableSet<String>
    ) {
        if (currentGen > maxGen || birdId in visited) return
        visited.add(birdId)

        val bird = productRepository.getById(birdId) ?: return

        bird.parentMaleId?.let { sireId ->
            productRepository.getById(sireId)?.let { sire ->
                ancestors.add(sire to currentGen)
                collectAncestorsRecursive(sireId, currentGen + 1, maxGen, ancestors, visited)
            }
        }

        bird.parentFemaleId?.let { damId ->
            productRepository.getById(damId)?.let { dam ->
                ancestors.add(dam to currentGen)
                collectAncestorsRecursive(damId, currentGen + 1, maxGen, ancestors, visited)
            }
        }
    }

    /**
     * Get all descendants (offspring) of a bird.
     */
    suspend fun getDescendants(birdId: String, maxGenerations: Int = 3): List<Pair<ProductEntity, Int>> {
        val descendants = mutableListOf<Pair<ProductEntity, Int>>()
        val visited = mutableSetOf<String>()
        collectDescendantsRecursive(birdId, 1, maxGenerations, descendants, visited)
        return descendants
    }

    private suspend fun collectDescendantsRecursive(
        birdId: String,
        currentGen: Int,
        maxGen: Int,
        descendants: MutableList<Pair<ProductEntity, Int>>,
        visited: MutableSet<String>
    ) {
        if (currentGen > maxGen || birdId in visited) return
        visited.add(birdId)

        val offspring = productRepository.getOffspring(birdId)
        offspring.forEach { child ->
            descendants.add(child to currentGen)
            collectDescendantsRecursive(child.productId, currentGen + 1, maxGen, descendants, visited)
        }
    }

    /**
     * Calculate inbreeding coefficient using Wright's formula (simplified).
     * 
     * F = Sum of [(1/2)^(n1+n2+1) * (1 + Fa)]
     * Where:
     * - n1 = generations from subject to common ancestor via sire
     * - n2 = generations from subject to common ancestor via dam
     * - Fa = inbreeding coefficient of common ancestor
     * 
     * @return Inbreeding coefficient (0.0 = no inbreeding, 1.0 = complete)
     */
    suspend fun calculateInbreedingCoefficient(birdId: String, maxDepth: Int = 6): Double {
        val sireAncestors = mutableMapOf<String, Int>() // ancestorId -> generation
        val damAncestors = mutableMapOf<String, Int>()

        val bird = productRepository.getById(birdId) ?: return 0.0

        // Collect ancestors from sire side
        bird.parentMaleId?.let { sireId ->
            collectAncestorMap(sireId, 1, maxDepth, sireAncestors)
        }

        // Collect ancestors from dam side
        bird.parentFemaleId?.let { damId ->
            collectAncestorMap(damId, 1, maxDepth, damAncestors)
        }

        // Find common ancestors
        val commonAncestors = sireAncestors.keys.intersect(damAncestors.keys)

        if (commonAncestors.isEmpty()) return 0.0

        // Calculate Wright's coefficient
        var inbreedingCoeff = 0.0
        for (ancestorId in commonAncestors) {
            val n1 = sireAncestors[ancestorId] ?: continue
            val n2 = damAncestors[ancestorId] ?: continue
            
            // Simplified: assume Fa (ancestor's inbreeding) = 0
            val pathContribution = Math.pow(0.5, (n1 + n2 + 1).toDouble())
            inbreedingCoeff += pathContribution
        }

        return inbreedingCoeff.coerceIn(0.0, 1.0)
    }

    private suspend fun collectAncestorMap(
        birdId: String,
        currentGen: Int,
        maxGen: Int,
        ancestorMap: MutableMap<String, Int>
    ) {
        if (currentGen > maxGen) return
        
        val bird = productRepository.getById(birdId) ?: return
        
        // Store the closest occurrence
        if (birdId !in ancestorMap || ancestorMap[birdId]!! > currentGen) {
            ancestorMap[birdId] = currentGen
        }

        bird.parentMaleId?.let { collectAncestorMap(it, currentGen + 1, maxGen, ancestorMap) }
        bird.parentFemaleId?.let { collectAncestorMap(it, currentGen + 1, maxGen, ancestorMap) }
    }

    /**
     * Check if two birds would produce inbred offspring.
     * Returns the projected coefficient and recommendation.
     */
    suspend fun checkPairingCompatibility(maleId: String, femaleId: String): PairingAnalysis {
        // Get ancestors for both
        val maleAncestors = mutableMapOf<String, Int>()
        val femaleAncestors = mutableMapOf<String, Int>()

        collectAncestorMap(maleId, 0, 5, maleAncestors)
        collectAncestorMap(femaleId, 0, 5, femaleAncestors)

        // Include self
        maleAncestors[maleId] = 0
        femaleAncestors[femaleId] = 0

        val commonAncestors = maleAncestors.keys.intersect(femaleAncestors.keys)

        // Projected offspring coefficient
        var projectedCoeff = 0.0
        for (ancestorId in commonAncestors) {
            val n1 = maleAncestors[ancestorId] ?: continue
            val n2 = femaleAncestors[ancestorId] ?: continue
            projectedCoeff += Math.pow(0.5, (n1 + n2 + 1).toDouble())
        }

        val recommendation = when {
            projectedCoeff == 0.0 -> PairingRecommendation.EXCELLENT
            projectedCoeff < 0.0625 -> PairingRecommendation.GOOD
            projectedCoeff < 0.125 -> PairingRecommendation.CAUTION
            projectedCoeff < 0.25 -> PairingRecommendation.WARNING
            else -> PairingRecommendation.AVOID
        }

        return PairingAnalysis(
            projectedInbreedingCoeff = projectedCoeff,
            commonAncestorCount = commonAncestors.size,
            recommendation = recommendation,
            message = getRecommendationMessage(recommendation, projectedCoeff)
        )
    }

    data class PairingAnalysis(
        val projectedInbreedingCoeff: Double,
        val commonAncestorCount: Int,
        val recommendation: PairingRecommendation,
        val message: String
    )

    enum class PairingRecommendation {
        EXCELLENT, GOOD, CAUTION, WARNING, AVOID
    }

    private fun getRecommendationMessage(rec: PairingRecommendation, coeff: Double): String {
        val formattedCoeff = String.format("%.2f%%", coeff * 100)
        return when (rec) {
            PairingRecommendation.EXCELLENT -> 
                "No common ancestry detected. Genetic diversity is maximized."
            PairingRecommendation.GOOD -> 
                "Distant common ancestry ($formattedCoeff). Pairing is safe."
            PairingRecommendation.CAUTION -> 
                "Some common ancestry ($formattedCoeff). Monitor offspring health."
            PairingRecommendation.WARNING -> 
                "Significant common ancestry ($formattedCoeff). Consider alternative pairings."
            PairingRecommendation.AVOID -> 
                "High inbreeding risk ($formattedCoeff). Pairing not recommended."
        }
    }

    /**
     * Calculate lineage completeness score.
     */
    suspend fun calculateLineageScore(birdId: String, generations: Int = 3): LineageScore {
        val ancestors = getAncestors(birdId, generations)
        val knownCount = ancestors.size

        // Calculate max possible: 2 + 4 + 8 + ... = 2^(n+1) - 2
        val maxPossible = (1..generations).sumOf { 1 shl it }

        val completeness = if (maxPossible > 0) (knownCount.toDouble() / maxPossible * 100).toInt() else 0

        // Count complete generations
        var completeGens = 0
        for (gen in 1..generations) {
            val expectedAtGen = 1 shl gen
            val actualAtGen = ancestors.count { it.second == gen }
            if (actualAtGen >= expectedAtGen) completeGens = gen
            else break
        }

        val recommendation = when {
            completeness >= 90 -> "Excellent lineage documentation"
            completeness >= 70 -> "Good lineage, some gaps"
            completeness >= 50 -> "Moderate lineage, consider researching parents"
            completeness >= 25 -> "Limited lineage, significant gaps"
            else -> "Minimal lineage data available"
        }

        return LineageScore(
            totalScore = completeness,
            generationsComplete = completeGens,
            knownAncestors = knownCount,
            maxPossibleAncestors = maxPossible,
            recommendation = recommendation
        )
    }
}
