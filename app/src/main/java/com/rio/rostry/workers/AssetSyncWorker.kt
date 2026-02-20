package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.domain.sync.OfflineSyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AssetSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: OfflineSyncManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Trigger the sync process for asset management
            syncManager.triggerSync()
            Result.success()
        } catch (e: Exception) {
            // If the error is due to network or temporary issue, retry
            Result.retry()
        }
    }
}
