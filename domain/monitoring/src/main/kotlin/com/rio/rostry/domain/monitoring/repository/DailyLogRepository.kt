package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.DailyLog
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for daily log operations.
 */
interface DailyLogRepository {
    /**
     * Get daily logs for an asset.
     */
    suspend fun getDailyLogsForAsset(assetId: String): Result<List<DailyLog>>
    
    /**
     * Create a daily log.
     */
    suspend fun createDailyLog(dailyLog: DailyLog): Result<Unit>
    
    /**
     * Update a daily log.
     */
    suspend fun updateDailyLog(dailyLog: DailyLog): Result<Unit>
    
    /**
     * Get daily log by ID.
     */
    suspend fun getDailyLogById(logId: String): Result<DailyLog?>
}
