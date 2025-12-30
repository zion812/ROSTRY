package com.rio.rostry.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.entity.UploadTaskEntity
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.data.repository.StorageRepository
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.TimeoutCancellationException
import timber.log.Timber
import java.io.InputStream

@HiltWorker
class MediaUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val uploadTaskDao: UploadTaskDao,
    private val connectivityManager: ConnectivityManager,
    private val storageRepository: StorageRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Set foreground IMMEDIATELY to avoid ANR - before any database access
        try {
            val notification = WorkerBaseHelper.createNotification(
                context = applicationContext,
                channelId = "media_upload_channel",
                channelName = "Media Uploads",
                title = "Uploading media...",
                content = "Syncing your photos to cloud",
                isOngoing = true
            )
            
            val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                androidx.work.ForegroundInfo(
                    NOTIFICATION_ID, 
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                androidx.work.ForegroundInfo(NOTIFICATION_ID, notification)
            }
            
            setForeground(foregroundInfo)
        } catch (e: Exception) {
            Timber.w(e, "UploadWorker: Failed to set foreground, continuing anyway")
            // Continue even if foreground fails - don't block worker
        }
        
        // Now do the heavy work on IO dispatcher with timeout protection
        return withContext(Dispatchers.IO) {
            try {
                // Add timeout to prevent ANR - give up after 90 seconds
                withTimeout(UPLOAD_TIMEOUT_MS) {
                    performUploads()
                }
            } catch (e: TimeoutCancellationException) {
                Timber.e(e, "UploadWorker: Timeout after ${UPLOAD_TIMEOUT_MS}ms")
                Result.retry()
            } catch (e: Exception) {
                Timber.e(e, "UploadWorker: fatal error")
                Result.retry()
            }
        }
    }
    
    private suspend fun performUploads(): Result {
        val pending = try {
            uploadTaskDao.getPending(limit = 25)
        } catch (e: Exception) {
            Timber.e(e, "UploadWorker: Failed to get pending tasks")
            return Result.retry()
        }
        
        if (pending.isEmpty()) return Result.success()

        // Process in small batches of 3 concurrent uploads
        pending.chunked(3).forEach { batch ->
            if (!connectivityManager.isOnline()) return Result.retry()
            
            batch.map { task ->
                CoroutineScope(Dispatchers.IO).async { uploadOneTask(task) }
            }.awaitAll()
        }
        
        return Result.success()
    }
    
    companion object {
        private const val NOTIFICATION_ID = 2001
        private const val UPLOAD_TIMEOUT_MS = 90_000L // 90 seconds to prevent ANR
    }

    private suspend fun uploadOneTask(task: UploadTaskEntity) {
        try {
            // Validate file accessibility
            val uri = android.net.Uri.parse(task.localPath)
            val isAccessible = withContext(Dispatchers.IO) {
                try {
                    applicationContext.contentResolver.openInputStream(uri)?.use { it.available() > 0 } ?: false
                } catch (e: Exception) {
                    Timber.w("UploadWorker: file inaccessible ${task.localPath}: ${e.message}")
                    false
                }
            }

            if (!isAccessible) {
                uploadTaskDao.markFailed(task.taskId, "File not found or permission lost", System.currentTimeMillis())
                return
            }

            val start = System.currentTimeMillis()
            Timber.i("UploadWorker: start taskId=${task.taskId} path=${task.remotePath}")
            
            val result = storageRepository.uploadFile(
                localUriString = task.localPath,
                remotePath = task.remotePath,
                compress = true,
                sizeLimitBytes = com.rio.rostry.utils.validation.VerificationValidationService.MAX_FILE_SIZE,
                onProgress = { pct ->
                    CoroutineScope(Dispatchers.IO).launch {
                        uploadTaskDao.updateProgress(task.taskId, pct, System.currentTimeMillis())
                    }
                },
            )

            when (result) {
                is Resource.Success -> {
                    val uploadResult = result.data!!
                    val jsonObject = try {
                        org.json.JSONObject(task.contextJson ?: "{}")
                    } catch (_: Exception) {
                        org.json.JSONObject()
                    }
                    jsonObject.put("downloadUrl", uploadResult.downloadUrl)
                    
                    uploadTaskDao.markSuccess(task.taskId, jsonObject.toString(), System.currentTimeMillis())
                    Timber.i("UploadWorker: success taskId=${task.taskId} in ${System.currentTimeMillis() - start}ms")
                    
                    cleanupCachedFile(task.localPath)
                }
                is Resource.Error -> {
                    val isQuota = result.message?.contains("quota") == true
                    Timber.e("UploadWorker: task ${task.taskId} failed: ${result.message}")
                    val now = System.currentTimeMillis()
                    if (isQuota || task.retries + 1 >= 3) {
                        uploadTaskDao.markFailed(task.taskId, result.message ?: "Terminal failure", now)
                        cleanupCachedFile(task.localPath)
                    } else {
                        uploadTaskDao.incrementRetries(task.taskId, now)
                    }
                }
                is Resource.Loading -> { /* Handled via progress */ }
            }
            
        } catch (t: Throwable) {
            Timber.w(t, "UploadWorker: task ${task.taskId} failed (retries=${task.retries})")
            val now = System.currentTimeMillis()
            if (task.retries + 1 >= 3) {
                uploadTaskDao.markFailed(task.taskId, t.message ?: "Max retries reached", now)
                cleanupCachedFile(task.localPath)
            } else {
                uploadTaskDao.incrementRetries(task.taskId, now)
            }
        }
    }

    private fun cleanupCachedFile(localPath: String) {
        if (!localPath.startsWith("file://")) return
        try {
            val path = if (localPath.startsWith("file:///")) localPath.substring(7) else localPath.substring(5)
            val file = java.io.File(path)
            val cacheDir = applicationContext.cacheDir.absolutePath
            if (file.absolutePath.startsWith(cacheDir) && file.exists()) {
                val deleted = file.delete()
                Timber.d("Cleanup cached file: $path, deleted: $deleted")
            }
        } catch (e: Exception) {
            Timber.w(e, "Cleanup failed for $localPath")
        }
    }
}
