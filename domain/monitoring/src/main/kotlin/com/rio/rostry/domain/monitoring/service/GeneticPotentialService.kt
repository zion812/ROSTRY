package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/** Domain interface for genetic potential scoring and analysis. */
interface GeneticPotentialService {
    suspend fun calculateGeneticScore(birdId: String): Result<Map<String, Any>>
    suspend fun getPotentialTraits(birdId: String): Result<List<Map<String, Any>>>
}
