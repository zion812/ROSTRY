package com.rio.rostry.utils.media
  
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.entity.UploadTaskEntity
import com.rio.rostry.security.SecurityManager
import com.rio.rostry.workers.MediaUploadWorker
import java.util.UUID
  
/**
 * Manages background media uploads with retry, compression hooks, and progress updates.
 * ViewModels should collect the 'events' flow to track progress and handle partial success scenarios.
 */
@Singleton
class MediaUploadManager @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val firebaseStorageUploader: FirebaseStorageUploader,
) {
    data class UploadTask(
        val localPath: String,
        val remotePath: String,
        val priority: Int = 0,
        val compress: Boolean = true,
        val sizeLimitBytes: Long = 1_500_000L
    )
  
    sealed class UploadEvent {
        data class Progress(val remotePath: String, val percent: Int): UploadEvent()
        data class Success(val remotePath: String, val downloadUrl: String): UploadEvent()
        data class Failed(val remotePath: String, val error: String): UploadEvent()
        data class Queued(val remotePath: String): UploadEvent()
        data class Retrying(val remotePath: String, val attempt: Int): UploadEvent()
        data class Cancelled(val remotePath: String): UploadEvent()
    }
  
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val activeUploads = mutableMapOf<String, Job>()
    private val _events = MutableSharedFlow<UploadEvent>(replay = 0, extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events = _events.asSharedFlow()
  
    // Optional outbox wiring (set via DI at app start)
    @Volatile private var uploadTaskDao: UploadTaskDao? = null
    @Volatile private var appContext: Context? = null
  
    companion object {
        const val MAX_UPLOAD_TIMEOUT_MS = 120_000L  // Configurable timeout for uploads, default 120s
    }
  
    fun attachOutbox(dao: UploadTaskDao, context: Context) {
        this.uploadTaskDao = dao
        this.appContext = context.applicationContext
    }
  
    fun enqueue(task: UploadTask) {
        val job = scope.launch {
            _events.emit(UploadEvent.Queued(task.remotePath))
            try {
                withTimeout(MAX_UPLOAD_TIMEOUT_MS) {
                    var attempt = 0
                    val maxAttempts = 3
                    while (attempt < maxAttempts) {
                        try {
                            if (!connectivityManager.isOnline()) {
                                delay(1000)
                                attempt++
                                _events.emit(UploadEvent.Retrying(task.remotePath, attempt))
                                continue
                            }
                            val result = firebaseStorageUploader.uploadFile(
                                localUriString = task.localPath,
                                remotePath = task.remotePath,
                                compress = task.compress,
                                sizeLimitBytes = task.sizeLimitBytes,
                                onProgress = { p -> scope.launch { _events.emit(UploadEvent.Progress(task.remotePath, p)) } }
                            )
                            _events.emit(UploadEvent.Success(task.remotePath, downloadUrl = result.downloadUrl))
                            break
                        } catch (t: Throwable) {
                            attempt++
                            if (attempt >= maxAttempts) {
                                _events.emit(UploadEvent.Failed(task.remotePath, t.message ?: "Upload failed"))
                                SecurityManager.audit("MEDIA_UPLOAD_TIMEOUT", mapOf("remotePath" to task.remotePath, "attempt" to attempt))
                            } else {
                                _events.emit(UploadEvent.Retrying(task.remotePath, attempt))
                                val backoff = 500L * (1 shl (attempt - 1))
                                delay(backoff)
                            }
                        }
                    }
                }
            } catch (t: TimeoutCancellationException) {
                _events.emit(UploadEvent.Failed(task.remotePath, "Upload timed out"))
                SecurityManager.audit("MEDIA_UPLOAD_TIMEOUT", mapOf("remotePath" to task.remotePath, "attempt" to "timeout"))
            }
            activeUploads.remove(task.remotePath)
        }
        activeUploads[task.remotePath] = job
    }
  
    fun cancelUpload(remotePath: String) {
        activeUploads.remove(remotePath)?.cancel()
        scope.launch { _events.emit(UploadEvent.Cancelled(remotePath)) }
    }
  
    /**
     * Persist upload to DAO-backed outbox and schedule WorkManager.
     * Also emits a Queued event for live sessions.
     */
    fun enqueueToOutbox(localPath: String, remotePath: String, contextJson: String? = null) {
        val dao = uploadTaskDao
        val ctx = appContext
        if (dao == null || ctx == null) {
            // Fallback to in-memory if not attached
            enqueue(UploadTask(localPath = localPath, remotePath = remotePath))
            return
        }
        scope.launch {
            try {
                _events.emit(UploadEvent.Queued(remotePath))
                val now = System.currentTimeMillis()
                val task = UploadTaskEntity(
                    taskId = UUID.randomUUID().toString(),
                    localPath = localPath,
                    remotePath = remotePath,
                    status = "QUEUED",
                    progress = 0,
                    retries = 0,
                    createdAt = now,
                    updatedAt = now,
                    error = null,
                    contextJson = contextJson
                )
                dao.upsert(task)
                scheduleWorker(ctx)
            } catch (_: Throwable) {
                // Swallow; WorkManager/DAO path is best-effort
            }
        }
    }
  
    private fun scheduleWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val req = OneTimeWorkRequestBuilder<MediaUploadWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueue(req)
    }
  
    /** Returns the set of remote paths for currently active (in-memory) uploads. */
    fun getActiveUploadPaths(): Set<String> = activeUploads.keys
  
    /** Checks if a specific remote path is currently being uploaded (in-memory). */
    fun isUploading(remotePath: String): Boolean = activeUploads.containsKey(remotePath)
}
