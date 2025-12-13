package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.utils.media.FirebaseStorageUploader
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private val firebaseStorageUploader: FirebaseStorageUploader,
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

                    // Validate file exists (avoid inline lambda with continue)
                    val uri = android.net.Uri.parse(task.localPath)
                    val stream = applicationContext.contentResolver.openInputStream(uri)
                    if (stream == null) {
                        Timber.w("UploadWorker: file not found ${task.localPath}")
                        uploadTaskDao.markFailed(task.taskId, "File not found", System.currentTimeMillis())
                        continue
                    } else {
                        stream.close()
                    }

                    val start = System.currentTimeMillis()
                    Timber.i("UploadWorker: start taskId=${task.taskId} path=${task.remotePath}")
                    // Use worker's coroutineContext to ensure child coroutines cancel with the worker
                    val workerScope = kotlinx.coroutines.CoroutineScope(coroutineContext)
                    val result = firebaseStorageUploader.uploadFile(
                        localUriString = task.localPath,
                        remotePath = task.remotePath,
                        compress = true,
                        sizeLimitBytes = 10_000_000L, // 10MB to match Firebase Storage rules
                        onProgress = { pct ->
                            if (pct == 25 || pct == 50 || pct == 75 || pct == 100) {
                                Timber.d("UploadWorker: progress ${pct}% for ${task.taskId}")
                            }
                            workerScope.launch {
                                uploadTaskDao.updateProgress(task.taskId, pct, System.currentTimeMillis())
                            }
                        },
                    )
                    val contextJson = "{" + "\"downloadUrl\":\"" + result.downloadUrl + "\"}" 
                    uploadTaskDao.markSuccess(task.taskId, contextJson, System.currentTimeMillis())
                    Timber.i("UploadWorker: success taskId=${task.taskId} in ${System.currentTimeMillis() - start}ms")
                } catch (t: Throwable) {
                    Timber.w(t, "UploadWorker: task ${task.taskId} failed (retries=${task.retries})")
                    val now = System.currentTimeMillis()
                    if (task.retries + 1 >= 3) {
                        uploadTaskDao.markFailed(task.taskId, t.message, now)
                        continue
                    } else {
                        uploadTaskDao.incrementRetries(task.taskId, now)
                        val backoff = 1000L * (1 shl task.retries.coerceAtLeast(0))
                        delay(backoff)
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "UploadWorker: fatal error")
            Result.retry()
        }
    }
}
