package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.data.sync.SyncRemote
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

@HiltWorker
class CrdtSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val outboxDao: OutboxDao,
    private val syncRemote: SyncRemote
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pendingDeltas = outboxDao.getPendingByType("FARM_ASSET_DELTA", 50)
        if (pendingDeltas.isEmpty()) return Result.success()

        var successCount = 0
        for (delta in pendingDeltas) {
            try {
                val payload = com.google.gson.Gson().fromJson(delta.payloadJson, DeltaPayload::class.java)
                val result = syncRemote.incrementFarmAssetField(delta.entityId, payload.field, payload.delta)
                
                if (result is Resource.Success) {
                    outboxDao.updateStatus(delta.outboxId, "COMPLETED", System.currentTimeMillis())
                    successCount++
                } else {
                    outboxDao.incrementRetry(delta.outboxId, System.currentTimeMillis())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to process CRDT delta ${delta.outboxId}")
                outboxDao.incrementRetry(delta.outboxId, System.currentTimeMillis())
            }
        }

        return if (successCount == pendingDeltas.size) Result.success() else Result.retry()
    }

    private data class DeltaPayload(val delta: Double, val field: String)

    companion object {
        const val WORK_NAME = "crdt_sync_worker"

        fun schedule(context: Context) {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<CrdtSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    1, TimeUnit.MINUTES
                )
                .addTag("session_worker")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
