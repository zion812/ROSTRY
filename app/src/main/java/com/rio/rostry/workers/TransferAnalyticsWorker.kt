package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.TransferAnalyticsDao
import com.rio.rostry.data.database.entity.TransferAnalyticsEntity
import com.rio.rostry.data.repository.TransferRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

/**
 * Worker to compute and persist transfer analytics daily.
 * Scheduled to run daily at 2 AM via WorkManager.
 */
@HiltWorker
class TransferAnalyticsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transferRepository: TransferRepository,
    private val transferAnalyticsDao: TransferAnalyticsDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            
            // Compute analytics for different periods
            val periods = listOf("daily", "weekly", "monthly")
            
            for (period in periods) {
                val analytics = transferRepository.getTransferAnalytics(period)
                
                // Persist analytics to database
                val entity = TransferAnalyticsEntity(
                    id = UUID.randomUUID().toString(),
                    period = period,
                    totalTransfers = analytics.totalTransfers,
                    completedTransfers = analytics.completedTransfers,
                    pendingTransfers = analytics.pendingTransfers,
                    cancelledTransfers = analytics.cancelledTransfers,
                    totalValue = analytics.totalValue,
                    averageTransferValue = analytics.averageTransferValue,
                    transfersByStatusJson = com.google.gson.Gson().toJson(analytics.transfersByStatus),
                    transfersByTypeJson = com.google.gson.Gson().toJson(analytics.transfersByType),
                    computedAt = now
                )
                
                transferAnalyticsDao.insert(entity)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "transfer_analytics_worker"
    }
}
