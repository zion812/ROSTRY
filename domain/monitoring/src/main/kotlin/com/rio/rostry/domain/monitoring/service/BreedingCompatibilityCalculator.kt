package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for the breeding compatibility calculator.
 *
 * Provides comprehensive compatibility scoring including genetic COI analysis,
 * phenotype matching, lethal gene risk assessment, and alternative mate suggestions.
 */
interface BreedingCompatibilityCalculator {

    /**
     * Calculate compatibility between a male and female bird.
     *
     * @return Compatibility result with score (0-100), verdict, reasons, and risk factors.
     */
    suspend fun calculateCompatibility(maleId: String, femaleId: String): Result<Map<String, Any>>

    /**
     * Get alternative breeding partner suggestions, excluding closely related birds.
     *
     * @param currentPartnerId The bird to find alternatives for.
     * @param excludeWithinGenerations Exclude birds within this many generations of relatedness.
     * @param maxSuggestions Maximum number of suggestions to return.
     */
    suspend fun getAlternativeSuggestions(
        currentPartnerId: String,
        excludeWithinGenerations: Int = 3,
        maxSuggestions: Int = 5
    ): Result<List<Map<String, Any>>>
}
