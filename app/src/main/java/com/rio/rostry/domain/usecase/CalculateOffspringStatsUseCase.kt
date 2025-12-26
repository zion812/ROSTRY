package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.BreedingPrediction
import com.rio.rostry.domain.model.Probability
import com.rio.rostry.domain.model.Range
import javax.inject.Inject

class CalculateOffspringStatsUseCase @Inject constructor() {

    operator fun invoke(sire: ProductEntity, dam: ProductEntity): BreedingPrediction {
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

        // Color Probability
        val sireColor = sire.colorTag ?: sire.color ?: "Unknown"
        val damColor = dam.colorTag ?: dam.color ?: "Unknown"
        
        val colorProbs = if (sireColor.equals(damColor, ignoreCase = true) && sireColor != "Unknown") {
            listOf(
                Probability(sireColor, 80),
                Probability("Other/Mixed", 20)
            )
        } else {
            listOf(
                Probability(sireColor, 40),
                Probability(damColor, 40),
                Probability("Mixed", 20)
            )
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
        // Simple logic: if both have "show quality" or similar tags in description/breed
        // For now, just placeholder logic based on simple properties
        if (sire.breed == dam.breed) {
            traits.add("Pure Breed")
        } else {
            traits.add("Cross Breed")
        }
        return traits.toList()
    }
}
