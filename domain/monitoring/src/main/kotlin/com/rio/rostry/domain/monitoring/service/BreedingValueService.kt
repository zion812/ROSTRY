package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for the Breeding Value Index (BVI) calculator.
 *
 * Computes a composite breeding value score (0.0–1.0) for a bird based on:
 * - Own phenotypic trait completeness and quality
 * - Show competition performance
 * - Offspring quality (progeny testing)
 * - Parent lineage strength
 * - Health record quality
 */
interface BreedingValueService {

    /**
     * Calculate the full BVI for a single bird.
     *
     * @param birdId Product ID of the bird to evaluate.
     * @return BVI result including sub-scores, rating, and recommendation.
     */
    suspend fun calculateBVI(birdId: String): Result<Map<String, Any>>
}
