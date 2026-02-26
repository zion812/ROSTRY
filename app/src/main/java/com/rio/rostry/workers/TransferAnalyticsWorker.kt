package com.rio.rostry.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.repository.TransferRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker to compute transfer analytics periodically.
 * Scheduled to run daily at 2 AM via WorkManager.
 *
 * Analytics are computed from TransferRepository and logged.
 * Individual transfer analytics records are tracked via TransferAnalyticsEntity
 * which is managed separately by the transfer workflow.
 */
@HiltWorker
class TransferAnalyticsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val transferRepository: TransferRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val periods = listOf("daily", "weekly", "monthly")

            for (period in periods) {
                val analytics = transferRepository.getTransferAnalytics(period)
                Log.i(
                    TAG,
                    "Transfer analytics [$period]: " +
                        "total=${analytics.totalTransfers}, " +
                        "completed=${analytics.completedTransfers}, " +
                        "pending=${analytics.pendingTransfers}, " +
                        "cancelled=${analytics.cancelledTransfers}, " +
                        "totalValue=${analytics.totalValue}, " +
                        "avgValue=${analytics.averageTransferValue}"
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Transfer analytics computation failed", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "TransferAnalyticsWorker"
        const val WORK_NAME = "transfer_analytics_worker"
    }
}
