package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for the breeding analysis service.
 *
 * Provides compatibility calculations, offspring trait predictions,
 * and clutch simulation for breeding pairs.
 */
interface BreedingService {

    /**
     * Calculate breeding compatibility between two birds.
     */
    suspend fun calculateCompatibilityScore(sireId: String, damId: String): Result<Int>

    /**
     * Predict probable offspring traits from a breeding pair.
     */
    suspend fun predictOffspringTraits(sireId: String, damId: String): Result<Map<String, Any>>

    /**
     * Simulate a clutch outcome for a breeding pair.
     */
    suspend fun simulateClutch(sireId: String, damId: String, clutchSize: Int): Result<List<Map<String, Any>>>
}
