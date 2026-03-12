package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for the flock productivity analysis service.
 *
 * Provides aggregated flock-level metrics including feed conversion,
 * mortality rates, growth benchmarks, and overall productivity scoring.
 */
interface FlockProductivityService {

    /**
     * Analyze overall flock productivity for a user.
     *
     * @param userId The farmer whose flock to analyze.
     * @return Aggregated productivity summary.
     */
    suspend fun analyzeFlockProductivity(userId: String): Result<Map<String, Any>>
}
