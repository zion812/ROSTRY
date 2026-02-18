package com.rio.rostry.domain.genetics

import com.rio.rostry.domain.model.GeneticProfile

/**
 * Simulates breeding between two birds to predict offspring genotypes.
 */
object BreedingSimulator {

    data class OffspringPrediction(
        val geneticProfile: GeneticProfile,
        val probability: Float // 0.0 - 1.0 (e.g. 0.25 for 25%)
    )

    /**
     * Generates a single random offspring genotype based on Mendelian inheritance (Monte Carlo).
     */
    fun breedOne(sire: GeneticProfile, dam: GeneticProfile, offspringId: String): GeneticProfile {
        return GeneticProfile(
            id = offspringId,
            eLocus = inherit(sire.eLocus, dam.eLocus),
            sLocus = inherit(sire.sLocus, dam.sLocus),
            bLocus = inherit(sire.bLocus, dam.bLocus),
            coLocus = inherit(sire.coLocus, dam.coLocus),
            pgLocus = inherit(sire.pgLocus, dam.pgLocus),
            mlLocus = inherit(sire.mlLocus, dam.mlLocus),
            moLocus = inherit(sire.moLocus, dam.moLocus),
            blLocus = inherit(sire.blLocus, dam.blLocus)
        )
    }

    // Helper: Select one allele from parent 1, one from parent 2
    private fun <T> inherit(p1: Pair<T, T>, p2: Pair<T, T>): Pair<T, T> {
        val allele1 = if (Math.random() < 0.5) p1.first else p1.second
        val allele2 = if (Math.random() < 0.5) p2.first else p2.second
        // Sort/Order? Usually doesn't matter for Pair, but for display standardizing helps (Dominant first?)
        // For now just return as is.
        return Pair(allele1, allele2)
    }
}
