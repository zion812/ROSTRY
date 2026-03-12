package com.rio.rostry.domain.monitoring.usecase

import com.rio.rostry.core.common.Result

/**
 * Domain interface for offspring statistics calculation.
 *
 * Predicts weight range, height range, color probabilities,
 * and likely traits for offspring of a given breeding pair.
 */
interface CalculateOffspringStatsUseCase {

    /**
     * Calculate predicted offspring statistics for a sire and dam pair.
     *
     * @param sireId Product ID of the sire.
     * @param damId Product ID of the dam.
     * @return Prediction results including weight/height ranges and color probabilities.
     */
    suspend fun invoke(sireId: String, damId: String): Result<Map<String, Any>>
}
