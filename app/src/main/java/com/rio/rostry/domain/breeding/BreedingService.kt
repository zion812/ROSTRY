package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeRepository
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class CompatibilityResult(
    val score: Int, // 0-100
    val riskLevel: RiskLevel,
    val inbreedingCoefficient: Double,
    val warnings: List<String>,
    val recommendations: List<String>
)

enum class RiskLevel {
    LOW, MODERATE, HIGH, CRITICAL
}

data class TraitPrediction(
    val possibleColors: Map<String, Double>, // Color -> Probability
    val possibleBreeds: Map<String, Double>, // Breed -> Probability
    val estimatedQuality: String // "Show", "Breeder", "Pet"
)

@Singleton
class BreedingService @Inject constructor(
    private val pedigreeRepository: PedigreeRepository,
    private val geneticPotentialService: com.rio.rostry.domain.service.GeneticPotentialService
) {

    /**
     * Calculates the breeding compatibility between a Sire (Male) and Dam (Female).
     * Checks for common ancestors up to 3 generations to estimate inbreeding risk.
     */
    fun calculateCompatibility(sire: ProductEntity, dam: ProductEntity): Flow<Resource<CompatibilityResult>> = flow {
        emit(Resource.Loading())

        if (sire.gender?.lowercase() != "male" || dam.gender?.lowercase() != "female") {
            emit(Resource.Error("Invalid gender combination: Sire must be male, Dam must be female"))
            return@flow
        }

        // Fetch full pedigrees
        // We use emit to suspend and fetch
        val sirePedigreeResult = pedigreeRepository.getFullPedigree(sire.productId, 3)
        val damPedigreeResult = pedigreeRepository.getFullPedigree(dam.productId, 3)

        if (sirePedigreeResult is Resource.Error || damPedigreeResult is Resource.Error) {
            emit(Resource.Error("Failed to fetch pedigrees for analysis"))
            return@flow
        }

        val sireTree = sirePedigreeResult.data ?: return@flow emit(Resource.Error("Sire pedigree data missing"))
        val damTree = damPedigreeResult.data ?: return@flow emit(Resource.Error("Dam pedigree data missing"))

        // Flatten trees to find common ancestors by Bird ID (Product ID)
        val sireAncestors = flattenAncestors(sireTree).map { it.bird.id }.toSet()
        val damAncestors = flattenAncestors(damTree).map { it.bird.id }.toSet()

        val commonAncestors = sireAncestors.intersect(damAncestors)
        val commonCount = commonAncestors.size
        
        // Direct sibling check: if parents match
        val sameSire = sire.parentMaleId != null && sire.parentMaleId == dam.parentMaleId
        val sameDam = sire.parentFemaleId != null && sire.parentFemaleId == dam.parentFemaleId
        
        var inbreedingRisk = 0.0
        val warnings = mutableListOf<String>()

        if (sameSire && sameDam) {
            inbreedingRisk = 0.25 // Full siblings
            warnings.add("Full Siblings: Very High risk of genetic defects")
        } else if (sameSire || sameDam) {
            inbreedingRisk = 0.125 // Half siblings
            warnings.add("Half Siblings: Moderate risk")
        } else if (commonCount > 0) {
            // Rough estimate for linebreeding based on shared ancestors in 3 gens
            inbreedingRisk = (commonCount.toDouble() / 14.0) * 0.1 
            warnings.add("Linebreeding: Common ancestors detected ($commonCount)")
        }

        // Calculate Score (100 - penalty)
        var score = 100
        score -= (inbreedingRisk * 200).toInt() // Significant penalty for inbreeding

        val riskLevel = when {
            inbreedingRisk >= 0.25 -> RiskLevel.CRITICAL
            inbreedingRisk >= 0.125 -> RiskLevel.HIGH
            inbreedingRisk > 0.05 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }
        
        // Cap score
        score = score.coerceIn(0, 100)

        val recommendations = mutableListOf<String>()
        if (score > 80) recommendations.add("Excellent Match")
        else if (score > 60) recommendations.add("Good Match")
        else recommendations.add("Consider alternative pairing due to genetic overlap")

        emit(Resource.Success(
            CompatibilityResult(
                score = score,
                riskLevel = riskLevel,
                inbreedingCoefficient = inbreedingRisk,
                warnings = warnings,
                recommendations = recommendations
            )
        ))
    }

    private fun flattenAncestors(tree: PedigreeTree): List<PedigreeTree> {
        val list = mutableListOf<PedigreeTree>()
        tree.sire?.let { 
            list.add(it)
            list.addAll(flattenAncestors(it))
        }
        tree.dam?.let { 
            list.add(it)
            list.addAll(flattenAncestors(it))
        }
        return list
    }

    /**
     * Probabilistic trait prediction based on basic Mendelian inheritance (simplified)
     */
    fun predictOffspring(sire: ProductEntity, dam: ProductEntity): TraitPrediction {
        // Color Prediction
        val possibleColors = mutableMapOf<String, Double>()
        val sireColor = sire.color ?: "Unknown"
        val damColor = dam.color ?: "Unknown"
        
        if (sireColor == damColor) {
            possibleColors[sireColor] = 0.75
            possibleColors["Other/Recessive"] = 0.25
        } else {
            possibleColors[sireColor] = 0.5
            possibleColors[damColor] = 0.5
        }

        // Breed Prediction
        val possibleBreeds = mutableMapOf<String, Double>()
        val sireBreed = sire.breed ?: "Unknown"
        val damBreed = dam.breed ?: "Unknown"
        
        if (sireBreed == damBreed) {
            possibleBreeds[sireBreed] = 0.95
            possibleBreeds["Mixed"] = 0.05
        } else {
            possibleBreeds["Mixed (${sireBreed} x ${damBreed})"] = 0.9
            possibleBreeds[sireBreed] = 0.05
            possibleBreeds[damBreed] = 0.05
        }
        
        // Quality Estimation
        val estimatedQuality = if (sireBreed == damBreed) "Breeder Quality" else "Pet Quality"

        return TraitPrediction(
            possibleColors = possibleColors,
            possibleBreeds = possibleBreeds,
            estimatedQuality = estimatedQuality
        )
    }

    /**
     * Advanced genetic analysis using potential service.
     */
    suspend fun analyzePairingPotential(sire: ProductEntity, dam: ProductEntity): com.rio.rostry.domain.service.GeneticPotentialResult {
        // Simplified: Averaging the potential of both parents
        val sirePotential = geneticPotentialService.analyzeLineage(sire.productId)
        val damPotential = geneticPotentialService.analyzeLineage(dam.productId)
        
        val newStrength = (sirePotential.lineageStrength + damPotential.lineageStrength) / 2
        
        // Merge traits
        val newTraits = mutableMapOf<String, Float>()
        sirePotential.traitPotential.forEach { (trait, score) ->
            newTraits[trait] = (score + (damPotential.traitPotential[trait] ?: 0f)) / 2
        }
        
        return com.rio.rostry.domain.service.GeneticPotentialResult(
            lineageStrength = newStrength,
            traitPotential = newTraits,
            notableAncestors = sirePotential.notableAncestors + damPotential.notableAncestors
        )
    }
}
