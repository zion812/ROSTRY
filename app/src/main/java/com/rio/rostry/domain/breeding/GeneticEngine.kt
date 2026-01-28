package com.rio.rostry.domain.breeding

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class GeneticEngine @Inject constructor() {

    /**
     * Infers a probable genotype from a visible phenotype for a given trait.
     * Since we don't store genetic testing data, we guess:
     * - Dominant Phenotype -> Heterozygous (Aa) (Statistically safer assumption for variation)
     * - Recessive Phenotype -> Homozygous Recessive (aa)
     */
    fun inferGenotype(traitType: TraitType, phenotypeValue: String?): Genotype {
        if (phenotypeValue.isNullOrBlank()) return Genotype.Unknown

        val traitConfig = TRAIT_CONFIGS[traitType] ?: return Genotype.Unknown
        
        // Check if value matches Recessive
        if (traitConfig.recessiveValues.any { it.equals(phenotypeValue, ignoreCase = true) }) {
            return Genotype.HomozygousRecessive // aa
        }
        
        // Check if value matches Dominant
        if (traitConfig.dominantValues.any { it.equals(phenotypeValue, ignoreCase = true) }) {
            // Assume Heterozygous (Aa) for diversity prediction unless proven otherwise
            // Ideally we'd look at parents/offspring to confirm AA vs Aa, but for now Aa gives more interesting "Predictions"
            return Genotype.Heterozygous // Aa
        }

        return Genotype.Unknown
    }

    /**
     * Simulates a Punnett Square cross between two parents for a specific trait.
     */
    fun simulateCross(sireGeno: Genotype, damGeno: Genotype): TraitPrediction {
        if (sireGeno == Genotype.Unknown || damGeno == Genotype.Unknown) {
            return TraitPrediction(
                probabilities = emptyList(),
                description = "Unknown genetics"
            )
        }

        val outcomes = mutableMapOf<Genotype, Int>()
        
        // Simple Mendelian Inheritance (2 alleles per parent)
        // Sire alleles
        val sireAlleles = getAlleles(sireGeno)
        val damAlleles = getAlleles(damGeno)

        // 2x2 Grid
        for (s in sireAlleles) {
            for (d in damAlleles) {
                val offspringGeno = combineAlleles(s, d)
                outcomes[offspringGeno] = outcomes.getOrDefault(offspringGeno, 0) + 1
            }
        }

        // Convert to probabilities
        val total = 4.0
        val probs = outcomes.map { (geno, count) ->
            ResultProbability(
                genotype = geno,
                percentage = (count / total * 100).toInt(),
                phenotypeDescription = getPhenotypeDescription(geno)
            )
        }.sortedByDescending { it.percentage }

        return TraitPrediction(probs, "Based on Mendelian inheritance")
    }

    private fun getAlleles(g: Genotype): List<Allele> = when (g) {
        Genotype.HomozygousDominant -> listOf(Allele.DOMINANT, Allele.DOMINANT) // AA
        Genotype.Heterozygous -> listOf(Allele.DOMINANT, Allele.RECESSIVE)      // Aa
        Genotype.HomozygousRecessive -> listOf(Allele.RECESSIVE, Allele.RECESSIVE) // aa
        Genotype.Unknown -> listOf(Allele.UNKNOWN, Allele.UNKNOWN)
    }

    private fun combineAlleles(a1: Allele, a2: Allele): Genotype {
        if (a1 == Allele.UNKNOWN || a2 == Allele.UNKNOWN) return Genotype.Unknown
        val dominantCount = (if (a1 == Allele.DOMINANT) 1 else 0) + (if (a2 == Allele.DOMINANT) 1 else 0)
        return when (dominantCount) {
            2 -> Genotype.HomozygousDominant
            1 -> Genotype.Heterozygous
            else -> Genotype.HomozygousRecessive
        }
    }

    private fun getPhenotypeDescription(g: Genotype): String {
        return when (g) {
            Genotype.HomozygousDominant -> "Dominant (Pure)"
            Genotype.Heterozygous -> "Dominant (Carrier)"
            Genotype.HomozygousRecessive -> "Recessive"
            Genotype.Unknown -> "Unknown"
        }
    }

    // --- Data Definitions ---

    enum class Genotype { HomozygousDominant, Heterozygous, HomozygousRecessive, Unknown }
    enum class Allele { DOMINANT, RECESSIVE, UNKNOWN }
    enum class TraitType { COMB, PLUMAGE_COLOR, SHANK_COLOR }

    data class TraitConfig(
        val dominantValues: List<String>,
        val recessiveValues: List<String>
    )

    data class ResultProbability(
        val genotype: Genotype,
        val percentage: Int,
        val phenotypeDescription: String
    )

    data class TraitPrediction(
        val probabilities: List<ResultProbability>,
        val description: String
    )

    companion object {
        // Simplified Poultry Genetics Configuration
        val TRAIT_CONFIGS = mapOf(
            TraitType.COMB to TraitConfig(
                dominantValues = listOf("Rose", "Pea", "Walnut"), // Simplified
                recessiveValues = listOf("Single")
            ),
            TraitType.PLUMAGE_COLOR to TraitConfig(
                dominantValues = listOf("Black", "White"), // Highly dependent on breed, massive simplification
                recessiveValues = listOf("Red", "Buff", "Gold") 
            ),
            TraitType.SHANK_COLOR to TraitConfig(
                dominantValues = listOf("White", "Pink"),
                recessiveValues = listOf("Yellow", "Green", "Blue")
            )
        )
    }
}
