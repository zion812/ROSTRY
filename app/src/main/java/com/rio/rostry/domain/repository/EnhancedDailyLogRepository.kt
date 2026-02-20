package com.rio.rostry.domain.repository

import com.rio.rostry.data.database.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

interface EnhancedDailyLogRepository {
    suspend fun createDailyLog(dailyLog: DailyLogEntity)
    suspend fun updateDailyLog(dailyLog: DailyLogEntity)
    suspend fun getDailyLogById(logId: String): DailyLogEntity?
    suspend fun getDailyLogsForAsset(assetId: String): List<DailyLogEntity>
}
