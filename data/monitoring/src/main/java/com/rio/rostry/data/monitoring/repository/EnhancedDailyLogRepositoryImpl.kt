package com.rio.rostry.data.monitoring.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.DailyLog
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.monitoring.mapper.toDailyLog
import com.rio.rostry.data.monitoring.mapper.toEntity
import com.rio.rostry.domain.monitoring.repository.EnhancedDailyLogRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of enhanced daily log operations.
 */
@Singleton
class EnhancedDailyLogRepositoryImpl @Inject constructor(
    private val dao: DailyLogDao
) : EnhancedDailyLogRepository {

    override suspend fun createDailyLog(dailyLog: DailyLog): Result<Unit> {
        return try {
            dao.upsert(dailyLog.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateDailyLog(dailyLog: DailyLog): Result<Unit> {
        return try {
            dao.upsert(dailyLog.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getDailyLogById(logId: String): Result<DailyLog?> {
        return try {
            val entity = dao.getById(logId)
            Result.Success(entity?.toDailyLog())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getDailyLogsForAsset(assetId: String): Result<List<DailyLog>> {
        return try {
            // Note: The DAO might need a method to get logs by assetId/productId
            // For now, returning empty list as the original implementation did
            Result.Success(emptyList())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

