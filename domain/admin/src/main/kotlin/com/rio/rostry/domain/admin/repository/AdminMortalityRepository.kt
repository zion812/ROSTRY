package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.OutbreakAlert
import com.rio.rostry.core.model.RegionalMortality
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for admin mortality operations.
 * Provides aggregated mortality statistics and outbreak detection.
 */
interface AdminMortalityRepository {
    /**
     * Get aggregated mortality stats per region (e.g., City or State).
     */
    fun getRegionalMortalityStats(): Flow<Result<List<RegionalMortality>>>

    /**
     * Detect potential outbreaks based on mortality rate thresholds.
     */
    fun getPotentialOutbreaks(): Flow<Result<List<OutbreakAlert>>>
}

