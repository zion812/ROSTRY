package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.domain.manager.MediaCacheManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MediaCacheCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val mediaCacheManager: MediaCacheManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Target 1GB cache limit
            val targetLimitBytes = 1024L * 1024L * 1024L 
            val result = mediaCacheManager.enforceLruPolicy(targetLimitBytes)
            
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
