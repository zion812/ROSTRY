package com.rio.rostry.domain.breeding

import com.rio.rostry.data.database.dao.BirdTraitRecordDao
import com.rio.rostry.data.database.dao.ShowRecordDao
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

/** Enhanced prediction using real trait record data */
data class EnhancedTraitPrediction(
    val colorPrediction: Map<String, Double>,
    val breedType: String,
    val predictedTraits: Map<String, TraitRange>,   // traitName -> predicted range
    val showPotential: String,                       // "Elite", "Competitive", "Moderate", "Unknown"
    val combinedParentBvi: Float,                    // average of parents
    val predictedQuality: String,                    // "Show Quality", "Breeder Quality", "Pet Quality"
    val traitHighlights: List<String>,               // Notable observations
    val dataConfidence: String                       // "High", "Medium", "Low"
)

data class TraitRange(
    val predicted: Float,   // Mid-parent value
    val low: Float,         // Conservative estimate
    val high: Float,        // Optimistic estimate
    val unit: String?
)

@Singleton
class BreedingService @Inject constructor(
    private val pedigreeRepository: PedigreeRepository,
    private val geneticPotentialService: com.rio.rostry.domain.service.GeneticPotentialService,
    private val traitRecordDao: BirdTraitRecordDao,
    private val showRecordDao: ShowRecordDao
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

        val sirePedigreeResult = pedigreeRepository.getFullPedigree(sire.productId, 3)
        val damPedigreeResult = pedigreeRepository.getFullPedigree(dam.productId, 3)

        if (sirePedigreeResult is Resource.Error || damPedigreeResult is Resource.Error) {
            emit(Resource.Error("Failed to fetch pedigrees for analysis"))
            return@flow
        }

        val sireTree = sirePedigreeResult.data ?: return@flow emit(Resource.Error("Sire pedigree data missing"))
        val damTree = damPedigreeResult.data ?: return@flow emit(Resource.Error("Dam pedigree data missing"))

        val sireAncestors = flattenAncestors(sireTree).map { it.bird.id }.toSet()
        val damAncestors = flattenAncestors(damTree).map { it.bird.id }.toSet()

        val commonAncestors = sireAncestors.intersect(damAncestors)
        val commonCount = commonAncestors.size
        
        val sameSire = sire.parentMaleId != null && sire.parentMaleId == dam.parentMaleId
        val sameDam = sire.parentFemaleId != null && sire.parentFemaleId == dam.parentFemaleId
        
        var inbreedingRisk = 0.0
        val warnings = mutableListOf<String>()

        if (sameSire && sameDam) {
            inbreedingRisk = 0.25
            warnings.add("Full Siblings: Very High risk of genetic defects")
        } else if (sameSire || sameDam) {
            inbreedingRisk = 0.125
            warnings.add("Half Siblings: Moderate risk")
        } else if (commonCount > 0) {
            inbreedingRisk = (commonCount.toDouble() / 14.0) * 0.1 
            warnings.add("Linebreeding: Common ancestors detected ($commonCount)")
        }

        var score = 100
        score -= (inbreedingRisk * 200).toInt()

        val riskLevel = when {
            inbreedingRisk >= 0.25 -> RiskLevel.CRITICAL
            inbreedingRisk >= 0.125 -> RiskLevel.HIGH
            inbreedingRisk > 0.05 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }
        
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
     * Legacy trait prediction (kept for backward compatibility)
     */
    fun predictOffspring(sire: ProductEntity, dam: ProductEntity): TraitPrediction {
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
        
        val estimatedQuality = if (sireBreed == damBreed) "Breeder Quality" else "Pet Quality"

        return TraitPrediction(
            possibleColors = possibleColors,
            possibleBreeds = possibleBreeds,
            estimatedQuality = estimatedQuality
        )
    }

    /**
     * Enhanced offspring prediction using real trait records from both parents.
     * Uses mid-parent regression with environmental variance estimation.
     */
    suspend fun predictOffspringEnhanced(
        sire: ProductEntity,
        dam: ProductEntity
    ): EnhancedTraitPrediction {
        // 1. Color/breed prediction (same as legacy)
        val colorPrediction = mutableMapOf<String, Double>()
        val sireColor = sire.color ?: "Unknown"
        val damColor = dam.color ?: "Unknown"
        if (sireColor == damColor) {
            colorPrediction[sireColor] = 0.75
            colorPrediction["Other/Recessive"] = 0.25
        } else {
            colorPrediction[sireColor] = 0.5
            colorPrediction[damColor] = 0.5
        }

        val sireBreed = sire.breed ?: "Unknown"
        val damBreed = dam.breed ?: "Unknown"
        val breedType = if (sireBreed.equals(damBreed, true)) sireBreed else "$sireBreed × $damBreed"

        // 2. Trait prediction from real data
        val sireTraits = traitRecordDao.getByBird(sire.productId)
        val damTraits = traitRecordDao.getByBird(dam.productId)

        val sireLatest = sireTraits.groupBy { it.traitName }
            .mapValues { (_, recs) -> recs.maxByOrNull { it.recordedAt }!! }
        val damLatest = damTraits.groupBy { it.traitName }
            .mapValues { (_, recs) -> recs.maxByOrNull { it.recordedAt }!! }

        val allTraitNames = (sireLatest.keys + damLatest.keys).distinct()
        val predictedTraits = mutableMapOf<String, TraitRange>()
        val highlights = mutableListOf<String>()

        for (traitName in allTraitNames) {
            val sireRec = sireLatest[traitName]
            val damRec = damLatest[traitName]
            val sireVal = sireRec?.traitValue?.toFloatOrNull()
            val damVal = damRec?.traitValue?.toFloatOrNull()

            if (sireVal != null && damVal != null) {
                // Mid-parent value with regression to mean (heritability ~0.3-0.5 for most poultry traits)
                val heritability = 0.4f
                val midParent = (sireVal + damVal) / 2f
                val populationMean = 5f // Assume normalized 0-10 scale
                val predicted = populationMean + heritability * (midParent - populationMean)
                val variance = 1.5f // Environmental noise

                predictedTraits[traitName] = TraitRange(
                    predicted = predicted,
                    low = (predicted - variance).coerceAtLeast(0f),
                    high = (predicted + variance).coerceAtMost(10f),
                    unit = sireRec.traitUnit ?: damRec.traitUnit
                )

                // Highlights
                if (sireVal >= 8f && damVal >= 8f) {
                    val name = traitName.replace("_", " ").replaceFirstChar { it.uppercase() }
                    highlights.add("Both parents excel at $name → strong inheritance likely")
                } else if (kotlin.math.abs(sireVal - damVal) >= 4f) {
                    val name = traitName.replace("_", " ").replaceFirstChar { it.uppercase() }
                    highlights.add("$name shows high variation between parents — offspring may vary")
                }
            } else {
                // Only one parent has data
                val value = sireVal ?: damVal ?: continue
                val unit = sireRec?.traitUnit ?: damRec?.traitUnit
                predictedTraits[traitName] = TraitRange(
                    predicted = value * 0.7f, // Regress more toward mean (less confident)
                    low = (value * 0.5f).coerceAtLeast(0f),
                    high = (value * 0.9f).coerceAtMost(10f),
                    unit = unit
                )
            }
        }

        // 3. Show potential from parents
        val sireWins = showRecordDao.countWins(sire.productId)
        val sireShows = showRecordDao.countTotal(sire.productId)
        val damWins = showRecordDao.countWins(dam.productId)
        val damShows = showRecordDao.countTotal(dam.productId)
        val totalWins = sireWins + damWins
        val totalShows = sireShows + damShows

        val showPotential = when {
            totalShows == 0 -> "Unknown"
            totalWins.toFloat() / totalShows >= 0.4f -> "Elite"
            totalWins.toFloat() / totalShows >= 0.2f -> "Competitive"
            else -> "Moderate"
        }

        if (totalWins > 0) {
            highlights.add("Parents have $totalWins wins across $totalShows shows")
        }

        // 4. Data confidence
        val dataPoints = sireTraits.size + damTraits.size
        val dataConfidence = when {
            dataPoints >= 20 && allTraitNames.size >= 6 -> "High"
            dataPoints >= 8 && allTraitNames.size >= 3 -> "Medium"
            else -> "Low"
        }

        // 5. Quality prediction from combined metrics
        val predictedQuality = when {
            showPotential == "Elite" && sireBreed.equals(damBreed, true) -> "Show Quality"
            showPotential != "Unknown" && predictedTraits.values.any { it.predicted >= 7f } -> "Breeder Quality"
            sireBreed.equals(damBreed, true) -> "Breeder Quality"
            else -> "Pet Quality"
        }

        val combinedBvi = 0f // Will be filled by caller if needed

        return EnhancedTraitPrediction(
            colorPrediction = colorPrediction,
            breedType = breedType,
            predictedTraits = predictedTraits,
            showPotential = showPotential,
            combinedParentBvi = combinedBvi,
            predictedQuality = predictedQuality,
            traitHighlights = highlights.take(5),
            dataConfidence = dataConfidence
        )
    }

    /**
     * Advanced genetic analysis using potential service.
     */
    suspend fun analyzePairingPotential(sire: ProductEntity, dam: ProductEntity): com.rio.rostry.domain.service.GeneticPotentialResult {
        val sirePotential = geneticPotentialService.analyzeLineage(sire.productId)
        val damPotential = geneticPotentialService.analyzeLineage(dam.productId)
        
        val newStrength = (sirePotential.lineageStrength + damPotential.lineageStrength) / 2
        
        val newTraits = mutableMapOf<String, Float>()
        sirePotential.traitPotential.forEach { (trait, score) ->
            newTraits[trait] = (score + (damPotential.traitPotential[trait] ?: 0f)) / 2
        }
        
        return com.rio.rostry.domain.service.GeneticPotentialResult(
            lineageStrength = newStrength,
            traitPotential = newTraits,
            notableAncestors = sirePotential.notableAncestors + damPotential.notableAncestors,
            dataQuality = "Projected"
        )
    }
}
