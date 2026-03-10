package com.rio.rostry.domain.monitoring

import com.rio.rostry.core.model.DailyLog
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.monitoring.repository.EnhancedDailyLogRepository
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
        val existingLogsResult = dailyLogRepository.getDailyLogsForAsset(assetId)
        val existingLogs = when (existingLogsResult) {
            is Result.Success -> existingLogsResult.data
            else -> return
        }
        val todayLog = existingLogs.find { it.logDate == date }

        if (todayLog != null) {
            val updated = todayLog.copy(
                weightGrams = weightGrams ?: todayLog.weightGrams,
                feedKg = (feedIntakeGrams?.div(1000.0)) ?: todayLog.feedKg,
                notes = healthObservations ?: todayLog.notes,
                updatedAt = System.currentTimeMillis()
            )
            dailyLogRepository.updateDailyLog(updated)
        } else {
            val newLog = DailyLog(
                logId = java.util.UUID.randomUUID().toString(),
                productId = assetId,
                farmerId = farmerId,
                logDate = date,
                weightGrams = weightGrams,
                feedKg = feedIntakeGrams?.div(1000.0),
                notes = healthObservations,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            dailyLogRepository.createDailyLog(newLog)
        }
    }

    suspend fun getLogsForAsset(assetId: String): List<DailyLog> {
        return when (val result = dailyLogRepository.getDailyLogsForAsset(assetId)) {
            is Result.Success -> result.data
            else -> emptyList()
        }
    }
}
