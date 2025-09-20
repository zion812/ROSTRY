package com.rio.rostry.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.sync.SyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class SyncWorker @Inject constructor(
    @ApplicationContext appContext: Context,
    params: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("SyncWorker: Starting sync operation")
            
            // Perform sync using our SyncManager
            val success = syncManager.syncAll()
            
            if (success) {
                Timber.d("SyncWorker: Sync operation completed successfully")
                Result.success()
            } else {
                Timber.d("SyncWorker: Sync operation failed or skipped")
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker: Error during sync operation")
            Result.failure()
        }
    }
}