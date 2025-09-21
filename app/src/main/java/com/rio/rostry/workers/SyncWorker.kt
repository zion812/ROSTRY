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
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "RostrySyncWorker"
    }

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker started")
        return try {
            when (val res = syncManager.syncAll()) {
                is Resource.Success -> {
                    val pushed = res.data?.pushed ?: 0
                    val pulled = res.data?.pulled ?: 0
                    Timber.d("SyncWorker completed: pushed=$pushed, pulled=$pulled")
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
