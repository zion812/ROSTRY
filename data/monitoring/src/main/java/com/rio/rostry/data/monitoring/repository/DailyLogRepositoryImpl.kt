package com.rio.rostry.data.monitoring.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.DailyLog
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.monitoring.repository.DailyLogRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DailyLogRepository using Firebase Firestore.
 */
@Singleton
class DailyLogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DailyLogRepository {

    private val dailyLogsCollection = firestore.collection("daily_logs")

    override suspend fun getDailyLogsForAsset(assetId: String): Result<List<DailyLog>> {
        return try {
            val query = dailyLogsCollection.whereEqualTo("productId", assetId).get().await()
            val logs = query.documents.mapNotNull { it.toObject(DailyLog::class.java) }
            Result.Success(logs)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createDailyLog(dailyLog: DailyLog): Result<Unit> {
        return try {
            dailyLogsCollection.document(dailyLog.logId).set(dailyLog).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateDailyLog(dailyLog: DailyLog): Result<Unit> {
        return try {
            dailyLogsCollection.document(dailyLog.logId).set(dailyLog).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getDailyLogById(logId: String): Result<DailyLog?> {
        return try {
            val document = dailyLogsCollection.document(logId).get().await()
            val log = document.toObject(DailyLog::class.java)
            Result.Success(log)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
