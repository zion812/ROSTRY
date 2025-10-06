package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.utils.network.ConnectivityManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Processes persisted media upload tasks from UploadTaskDao.
 * Uses simple simulated upload pipeline here; integrate storage SDK as needed.
 */
@HiltWorker
class MediaUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val uploadTaskDao: UploadTaskDao,
    private val connectivityManager: ConnectivityManager,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pending = uploadTaskDao.getPending(limit = 25)
            if (pending.isEmpty()) return@withContext Result.success()
            for (task in pending) {
                try {
                    if (!connectivityManager.isOnline()) {
                        Timber.d("UploadWorker: offline, retry later")
                        return@withContext Result.retry()
                    }
                    // Simulate incremental progress; replace with actual upload implementation
                    for (pct in listOf(10, 35, 65, 100)) {
                        uploadTaskDao.updateProgress(task.taskId, pct, System.currentTimeMillis())
                        kotlinx.coroutines.delay(150)
                    }
                    uploadTaskDao.markSuccess(task.taskId, System.currentTimeMillis())
                } catch (t: Throwable) {
                    Timber.w(t, "UploadWorker: task ${task.taskId} failed")
                    uploadTaskDao.markFailed(task.taskId, t.message, System.currentTimeMillis())
                    // Continue with next; WorkManager will re-run based on retry policy if needed
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "UploadWorker: fatal error")
            Result.retry()
        }
    }
}
