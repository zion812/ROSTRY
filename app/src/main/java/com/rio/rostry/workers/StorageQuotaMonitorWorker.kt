package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.repository.StorageUsageRepository
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Worker that periodically recalculates cloud storage usage.
 */
@HiltWorker
class StorageQuotaMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: StorageUsageRepository,
    private val firebaseAuth: FirebaseAuth
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val WORK_NAME = "storage_quota_monitor_work"
        
        fun enqueuePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<StorageQuotaMonitorWorker>(
                1, TimeUnit.DAYS // Recalculate once a day
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
                request
            )
        }

        fun enqueueOneTime(context: Context) {
            val request = OneTimeWorkRequestBuilder<StorageQuotaMonitorWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        val userId = firebaseAuth.currentUser?.uid ?: return Result.success()
        
        Timber.d("StorageQuotaMonitorWorker: Refreshing usage for $userId")
        
        return when (val resource = repository.refreshUsage(userId)) {
            is Resource.Success -> {
                Timber.d("StorageQuotaMonitorWorker: Successfully refreshed usage for $userId")
                Result.success()
            }
            is Resource.Error -> {
                Timber.e("StorageQuotaMonitorWorker: Failed to refresh usage for $userId: ${resource.message}")
                if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
            is Resource.Loading -> Result.retry()
        }
    }
}
