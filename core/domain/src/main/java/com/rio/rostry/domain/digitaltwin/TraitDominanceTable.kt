package com.rio.rostry.domain.digitaltwin

import com.rio.rostry.domain.genetics.*

/**
 * 🧬 TraitDominanceTable — Mendelian Dominance Rules for Aseel Traits
 *
 * Defines which alleles are dominant/recessive for each locus, enabling
 * accurate phenotype prediction from genotype combinations.
 *
 * Dominance hierarchy per locus:
 * - E Locus (Base Color): E > ER > e_Wh > e+
 * - S Locus (Silver/Gold): S > s+ (sex-linked)
 * - B Locus (Barring): B > b+ (sex-linked)
 * - Co Locus (Columbian): Co > co+
 * - Pg Locus (Pattern): Pg > pg+
 * - Ml Locus (Melanotic): Ml > ml+
 * - Mo Locus (Mottling): Mo > mo+
 * - Bl Locus (Blue): Bl/Bl+ = Blue, Bl/Bl = Splash (incomplete dominance)
 *
 * Usage:
 * ```kotlin
 * val phenotype = TraitDominanceTable.predictPhenotype(sire.eLocus, dam.eLocus)
 * val offspringProbabilities = TraitDominanceTable.predictOffspringProbabilities(sireProfile, damProfile)
 * ```
 */
object TraitDominanceTable {

    // ==================== E LOCUS (Base Color) ====================

    /**
     * E Locus dominance (highest to lowest):
     * EXTENDED (Solid Black) > BIRCHEN > DOMINANT_WHEATEN > WILD_TYPE > BROWN
     */
    fun getELocusPhenotype(genotype: Pair<AlleleE, AlleleE>): String {
        val alleles = listOf(genotype.first, genotype.second)

        return when {
            alleles.any { it == AlleleE.EXTENDED } -> "Extended Black"
            alleles.any { it == AlleleE.BIRCHEN } -> "Birchen"
            alleles.any { it == AlleleE.DOMINANT_WHEATEN } -> "Wheaten"
            alleles.any { it == AlleleE.WILD_TYPE } -> "Wild Type"
            else -> "Brown"
        }
    }

    /**
     * Predict offspring E-Locus distribution.
     * Returns map of phenotype → probability (0.0-1.0)
     */
    fun predictELocusOffspring(
        sire: Pair<AlleleE, AlleleE>,
        dam: Pair<AlleleE, AlleleE>
    ): Map<String, Float> {
        return predictLocusOffspring(sire, dam) { alleles ->
            getELocusPhenotype(alleles)
        }
    }

    // ==================== S LOCUS (Silver/Gold) ====================

    fun getSLocusPhenotype(genotype: Pair<AlleleS, AlleleS>): String {
        val alleles = listOf(genotype.first, genotype.second)
        return when {
            alleles.any { it == AlleleS.SILVER } -> "Silver"
            else -> "Gold"
        }
    }

    // ==================== Bl LOCUS (Blue) — Incomplete Dominance ====================

    fun getBlLocusPhenotype(genotype: Pair<AlleleBl, AlleleBl>): String {
        return when {
            genotype.first == AlleleBl.BLUE && genotype.second == AlleleBl.BLUE -> "Splash"
            genotype.first == AlleleBl.BLUE || genotype.second == AlleleBl.BLUE -> "Blue"
            else -> "Non-Blue"
        }
    }

    // ==================== COMPOSITE PHENOTYPE PREDICTION ====================

    /**
     * Predict color phenotype from full genotype.
     * Maps the combined locus effects to a local Aseel color name.
     */
    data class PhenotypePrediction(
        val baseColor: String,
        val silverGold: String,
        val blueEffect: String,
        val mottled: Boolean,
        val barred: Boolean,
        val columbian: Boolean,
        val melanotic: Boolean,
        val suggestedLocalType: String, // Maps to LocalBirdType
        val confidence: Float           // 0.0-1.0
    )

    /**
     * Predict comprehensive phenotype from genotype.
     */
    fun predictPhenotype(profile: com.rio.rostry.domain.model.GeneticProfile): PhenotypePrediction {
        val base = getELocusPhenotype(profile.eLocus)
        val sg = getSLocusPhenotype(profile.sLocus)
        val blue = getBlLocusPhenotype(profile.blLocus)
        // AlleleMo.MOTTLED is recessive (mo) — bird is mottled only if homozygous
        val mottled = profile.moLocus.first == AlleleMo.MOTTLED && profile.moLocus.second == AlleleMo.MOTTLED
        val barred = profile.bLocus.first == AlleleB.BARRED || profile.bLocus.second == AlleleB.BARRED
        val columbian = profile.coLocus.first == AlleleCo.COLUMBIAN || profile.coLocus.second == AlleleCo.COLUMBIAN
        val melanotic = profile.mlLocus.first == AlleleMl.MELANOTIC || profile.mlLocus.second == AlleleMl.MELANOTIC

        val localType = mapToLocalType(base, sg, blue, mottled, barred, melanotic)

        return PhenotypePrediction(
            baseColor = base,
            silverGold = sg,
            blueEffect = blue,
            mottled = mottled,
            barred = barred,
            columbian = columbian,
            melanotic = melanotic,
            suggestedLocalType = localType,
            confidence = 0.85f // Genetic prediction confidence
        )
    }

    /**
     * Predict offspring phenotype probabilities from two parents.
     * Returns all possible PhenotypePredictions with probability weights.
     */
    fun predictOffspringDistribution(
        sire: com.rio.rostry.domain.model.GeneticProfile,
        dam: com.rio.rostry.domain.model.GeneticProfile,
        sampleSize: Int = 1000
    ): List<Pair<PhenotypePrediction, Float>> {
        // Monte Carlo simulation of offspring
        val results = mutableMapOf<String, Int>()
        val predictions = mutableMapOf<String, PhenotypePrediction>()

        repeat(sampleSize) {
            val offspring = com.rio.rostry.domain.genetics.BreedingSimulator.breedOne(
                sire, dam, "sim_$it"
            )
            val phenotype = predictPhenotype(offspring)
            val key = phenotype.suggestedLocalType
            results[key] = (results[key] ?: 0) + 1
            predictions[key] = phenotype
        }

        return results.map { (key, count) ->
            Pair(predictions[key]!!, count.toFloat() / sampleSize)
        }.sortedByDescending { it.second }
    }

    // ==================== PRIVATE HELPERS ====================

    /**
     * Generic locus offspring prediction using Punnett square approach.
     */
    private fun <T> predictLocusOffspring(
        p1: Pair<T, T>,
        p2: Pair<T, T>,
        phenotypeMapper: (Pair<T, T>) -> String
    ): Map<String, Float> {
        val combinations = listOf(
            Pair(p1.first, p2.first),
            Pair(p1.first, p2.second),
            Pair(p1.second, p2.first),
            Pair(p1.second, p2.second)
        )

        val phenotypeCount = mutableMapOf<String, Int>()
        combinations.forEach { combo ->
            val phenotype = phenotypeMapper(combo)
            phenotypeCount[phenotype] = (phenotypeCount[phenotype] ?: 0) + 1
        }

        return phenotypeCount.mapValues { it.value.toFloat() / 4f }
    }

    /**
     * Map genetic traits to local Aseel color type (LocalBirdType).
     * This is the culturally-aware mapping that connects Western genetics
     * to Andhra/Telangana traditional classification.
     */
    private fun mapToLocalType(
        base: String,
        silverGold: String,
        blue: String,
        mottled: Boolean,
        barred: Boolean,
        melanotic: Boolean
    ): String {
        return when {
            // KAKI: Extended Black, non-blue
            base == "Extended Black" && blue == "Non-Blue" && !mottled -> "KAKI"

            // SETHU: Wheaten with Silver (very light)
            base == "Wheaten" && silverGold == "Silver" -> "SETHU"

            // DEGA: Wild Type with Gold (red/eagle colored)
            base == "Wild Type" && silverGold == "Gold" && !melanotic -> "DEGA"

            // SAVALA: Extended Black with Columbian restriction (black neck)
            base == "Extended Black" && !mottled && barred -> "SAVALA"

            // PARLA: Barred (black & white alternating)
            barred && base == "Extended Black" -> "PARLA"

            // NEMALI: Wheaten with Gold (yellow/peacock)
            base == "Wheaten" && silverGold == "Gold" -> "NEMALI"

            // POOLA: Mottled (each feather multi-colored)
            mottled -> "POOLA"

            // NALLA_BORA: Dark melanotic with some markings
            melanotic && base == "Extended Black" -> "NALLA_BORA"

            // ABRASU: Wheaten light gold
            base == "Wheaten" -> "ABRASU"

            // Blue variants
            blue == "Blue" -> "KOKKIRAYI"
            blue == "Splash" -> "PINGALA"

            // KOWJU: Mixed (Birchen often = tricolor)
            base == "Birchen" -> "KOWJU"

            // Default: Mixed
            else -> "MAILA"
        }
    }
}
