package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for the mate recommendation engine.
 *
 * Finds the best breeding partners for a focal bird based on
 * genetic diversity, trait complementarity, and health status.
 */
interface MateRecommendationService {

    /**
     * Find the best mates for a given bird.
     *
     * @param focalBirdId The bird to find mates for.
     * @param limit Maximum number of recommendations to return.
     * @return Ranked list of recommended mates with compatibility scores.
     */
    suspend fun findBestMates(focalBirdId: String, limit: Int = 5): Result<List<Map<String, Any>>>
}
