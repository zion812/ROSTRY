package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.DailyLog

/**
 * Repository contract for enhanced daily log operations.
 */
interface EnhancedDailyLogRepository {
    suspend fun createDailyLog(dailyLog: DailyLog): Result<Unit>
    suspend fun updateDailyLog(dailyLog: DailyLog): Result<Unit>
    suspend fun getDailyLogById(logId: String): Result<DailyLog?>
    suspend fun getDailyLogsForAsset(assetId: String): Result<List<DailyLog>>
}

