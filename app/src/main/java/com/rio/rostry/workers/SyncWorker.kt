package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager,
    private val usageTracker: com.rio.rostry.data.monitoring.FirebaseUsageTracker
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "RostrySyncWorker"
    }

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker started")
        
        // Quota Check
        if (usageTracker.isQuotaExceeded()) {
            Timber.w("SyncWorker skipped: Firebase Daily Quota Exceeded")
            return Result.failure() // Or success() to stop retrying, but failure logs it better? 
            // Using success to avoid backoff retry loops when we know it's a quota issue for the whole day.
            // Actually, returning failure triggers retry with backoff. 
            // If quota is exceeded, we want to stop until tomorrow. 
            // But WorkManager doesn't have "retry tomorrow".
            // So we return success() so it doesn't retry immediately, but next periodic schedule will run.
            return Result.success()
        }

        return try {
            when (val res = syncManager.syncAll()) {
                is Resource.Success -> {
                    val pushed = res.data?.pushed ?: 0
                    val pulled = res.data?.pulled ?: 0
                    Timber.d("SyncWorker completed: pushed=$pushed, pulled=$pulled")
                    
                    // Track usage (Estimation)
                    // We assume 1 read per pull and 1 write per push approximately
                    usageTracker.trackReads(pulled)
                    usageTracker.trackWrites(pushed)
                    
                    Result.success()
                }
                is Resource.Error -> {
                    Timber.e("SyncManager error: ${res.message}")
                    Result.retry()
                }
                is Resource.Loading -> Result.success()
            }
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed")
            Result.failure()
        }
    }
}
