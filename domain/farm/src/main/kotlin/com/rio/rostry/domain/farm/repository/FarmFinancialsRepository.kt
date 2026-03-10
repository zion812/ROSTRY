package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.FarmFinancials
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for farm financials operations.
 */
interface FarmFinancialsRepository {
    /**
     * Get farm financials by farmer ID.
     */
    suspend fun getFarmFinancials(farmerId: String): Result<FarmFinancials?>
    
    /**
     * Update farm financials.
     */
    suspend fun updateFarmFinancials(financials: FarmFinancials): Result<Unit>
    
    /**
     * Get financial summary for a farm.
     */
    suspend fun getFinancialSummary(farmId: String): Result<Map<String, Double>>
}
