package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result

/** Domain interface for breeding ROI (return on investment) analysis. */
interface BreedingROIService {
    suspend fun calculateROI(userId: String): Result<Map<String, Any>>
    suspend fun projectRevenue(breedingPairId: String): Result<Map<String, Any>>
}
