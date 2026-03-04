package com.rio.rostry.domain.model

import com.rio.rostry.domain.genetics.*

/**
 * Represents the Genetic Profile (Genotype) of a bird.
 * This determines the visual appearance (Phenotype).
 */
data class GeneticProfile(
    val id: String,
    
    // Base Color (E Locus)
    val eLocus: Pair<AlleleE, AlleleE>,
    
    // Sex Linked Traits (Hens only have one relevant allele, second is null/empty marker)
    val sLocus: Pair<AlleleS, AlleleS>, // Silver/Gold
    val bLocus: Pair<AlleleB, AlleleB>, // Barring
    
    // Autosomal Traits
    val coLocus: Pair<AlleleCo, AlleleCo>, // Columbian
    val pgLocus: Pair<AllelePg, AllelePg>, // Pattern
    val mlLocus: Pair<AlleleMl, AlleleMl>, // Melanotic
    val moLocus: Pair<AlleleMo, AlleleMo>, // Mottling
    val blLocus: Pair<AlleleBl, AlleleBl>  // Blue
) {
    /**
     * Returns a string representation of the genotype (e.g., "E/e+ S/s B/B")
     */
    fun toGenotypeString(): String {
        return buildString {
            append("${eLocus.first.symbol}/${eLocus.second.symbol} ")
            append("${sLocus.first.symbol}/${sLocus.second.symbol} ")
            append("${bLocus.first.symbol}/${bLocus.second.symbol} ")
            append("${coLocus.first.symbol}/${coLocus.second.symbol} ")
            append("${pgLocus.first.symbol}/${pgLocus.second.symbol} ")
            append("${mlLocus.first.symbol}/${mlLocus.second.symbol} ")
            append("${moLocus.first.symbol}/${moLocus.second.symbol} ")
            append("${blLocus.first.symbol}/${blLocus.second.symbol}")
        }
    }
}
