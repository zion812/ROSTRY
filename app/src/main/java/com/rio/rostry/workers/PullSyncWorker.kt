package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.utils.network.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PullSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager,
    private val connectivityManager: ConnectivityManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Check if user is authenticated - if not, skip work
        if (FirebaseAuth.getInstance().currentUser == null) {
            return Result.success() // User logged out, no need to sync
        }
        
        // Check if online
        if (!connectivityManager.isOnline()) {
            return Result.retry()
        }

        return try {
            // SyncManager.syncAll() handles pull sync
            syncManager.syncAll()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "PullSyncWorker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<PullSyncWorker>(
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag("session_worker")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
