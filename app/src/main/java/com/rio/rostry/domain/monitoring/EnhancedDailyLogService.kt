package com.rio.rostry.domain.monitoring

import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.domain.repository.EnhancedDailyLogRepository
import javax.inject.Inject

class EnhancedDailyLogService @Inject constructor(
    private val dailyLogRepository: EnhancedDailyLogRepository
) {
    suspend fun logDailyMetrics(
        assetId: String,
        farmerId: String,
        weightGrams: Double?,
        feedIntakeGrams: Double?,
        waterIntakeMl: Double?,
        healthObservations: String?,
        date: Long
    ) {
        val existingLogs = dailyLogRepository.getDailyLogsForAsset(assetId)
        val todayLog = existingLogs.find { it.logDate == date }
        
        if (todayLog != null) {
            val updated = todayLog.copy(
                weightGrams = weightGrams ?: todayLog.weightGrams,
                feedKg = (feedIntakeGrams?.div(1000.0)) ?: todayLog.feedKg,
                notes = healthObservations ?: todayLog.notes,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            dailyLogRepository.updateDailyLog(updated)
        } else {
            val newLog = DailyLogEntity(
                logId = java.util.UUID.randomUUID().toString(),
                productId = assetId,
                farmerId = farmerId,
                logDate = date,
                weightGrams = weightGrams,
                feedKg = feedIntakeGrams?.div(1000.0),
                notes = healthObservations,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )
            dailyLogRepository.createDailyLog(newLog)
        }
    }
    
    suspend fun getLogsForAsset(assetId: String): List<DailyLogEntity> {
        return dailyLogRepository.getDailyLogsForAsset(assetId)
    }
}
