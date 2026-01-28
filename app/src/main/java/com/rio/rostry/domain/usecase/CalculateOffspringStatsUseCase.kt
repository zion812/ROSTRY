package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.breeding.GeneticEngine
import com.rio.rostry.domain.model.BreedingPrediction
import com.rio.rostry.domain.model.Probability
import com.rio.rostry.domain.model.Range
import javax.inject.Inject

class CalculateOffspringStatsUseCase @Inject constructor(
    private val geneticEngine: GeneticEngine
) {

    suspend operator fun invoke(sire: ProductEntity, dam: ProductEntity): BreedingPrediction {
        // Weight Calculation: Average +/- 10%
        val sireWeight = sire.weightGrams ?: 3000.0 // Default 3kg if missing
        val damWeight = dam.weightGrams ?: 2500.0   // Default 2.5kg if missing
        val avgWeight = (sireWeight + damWeight) / 2
        val minWeight = avgWeight * 0.9
        val maxWeight = avgWeight * 1.1

        // Height Calculation: Average +/- 5%
        val sireHeight = sire.heightCm ?: 60.0
        val damHeight = dam.heightCm ?: 50.0
        val avgHeight = (sireHeight + damHeight) / 2
        val minHeight = avgHeight * 0.95
        val maxHeight = avgHeight * 1.05

        // Color Probability using Genetic Engine Punnett Square
        val sireColorGeno = geneticEngine.inferGenotype(GeneticEngine.TraitType.PLUMAGE_COLOR, sire.color ?: sire.colorTag)
        val damColorGeno = geneticEngine.inferGenotype(GeneticEngine.TraitType.PLUMAGE_COLOR, dam.color ?: dam.colorTag)
        
        val colorPrediction = geneticEngine.simulateCross(sireColorGeno, damColorGeno)
        
        // Map GeneticEngine results to Probability model
        val colorProbs = if (colorPrediction.probabilities.isNotEmpty()) {
            colorPrediction.probabilities.map { 
                Probability(
                    item = it.phenotypeDescription,
                    percentage = it.percentage
                )
            }
        } else {
             // Fallback for Unknown genetics
             val sireColor = sire.colorTag ?: sire.color ?: "Unknown"
             val damColor = dam.colorTag ?: dam.color ?: "Unknown"
             if (sireColor.equals(damColor, ignoreCase = true) && sireColor != "Unknown") {
                listOf(Probability(sireColor, 80), Probability("Other", 20))
             } else {
                listOf(Probability(sireColor, 40), Probability(damColor, 40), Probability("Mixed", 20))
             }
        }

        return BreedingPrediction(
            weightRange = Range(minWeight, maxWeight),
            heightRange = Range(minHeight, maxHeight),
            colorProbabilities = colorProbs,
            likelyTraits = combineTraits(sire, dam)
        )
    }

    private fun combineTraits(sire: ProductEntity, dam: ProductEntity): List<String> {
        val traits = mutableSetOf<String>()
        if (sire.breed == dam.breed) {
            traits.add("Pure Breed")
        } else {
            traits.add("Cross Breed")
        }
        
        // Add Comb prediction if data exists
        // simplified usage
        val sireComb = geneticEngine.inferGenotype(GeneticEngine.TraitType.COMB, "Rose") // Mock reading form attributes
        val damComb = geneticEngine.inferGenotype(GeneticEngine.TraitType.COMB, "Single")
        val combPred = geneticEngine.simulateCross(sireComb, damComb)
        if(combPred.probabilities.isNotEmpty()) {
             traits.add("Comb: " + combPred.probabilities.first().phenotypeDescription)
        }
        
        return traits.toList()
    }
}
