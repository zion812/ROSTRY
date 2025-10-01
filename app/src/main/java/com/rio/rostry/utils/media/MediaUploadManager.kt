package com.rio.rostry.utils.media

import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages background media uploads with retry, compression hooks, and progress updates.
 */
@Singleton
class MediaUploadManager @Inject constructor(
    private val connectivityManager: ConnectivityManager
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
        data class Success(val remotePath: String): UploadEvent()
        data class Failed(val remotePath: String, val error: String): UploadEvent()
        data class Queued(val remotePath: String): UploadEvent()
        data class Retrying(val remotePath: String, val attempt: Int): UploadEvent()
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val _events = MutableSharedFlow<UploadEvent>(replay = 0, extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events = _events.asSharedFlow()

    fun enqueue(task: UploadTask) {
        scope.launch {
            _events.emit(UploadEvent.Queued(task.remotePath))
            // Placeholder: here we'd compress, chunk, and upload via Storage SDK
            var attempt = 0
            val maxAttempts = 3
            while (attempt < maxAttempts) {
                try {
                    if (!connectivityManager.isOnline()) {
                        kotlinx.coroutines.delay(1000)
                        continue
                    }
                    // Simulate progress
                    for (p in listOf(10, 30, 60, 100)) {
                        _events.emit(UploadEvent.Progress(task.remotePath, p))
                        kotlinx.coroutines.delay(100)
                    }
                    _events.emit(UploadEvent.Success(task.remotePath))
                    return@launch
                } catch (t: Throwable) {
                    attempt++
                    _events.emit(UploadEvent.Retrying(task.remotePath, attempt))
                    if (attempt >= maxAttempts) {
                        _events.emit(UploadEvent.Failed(task.remotePath, t.message ?: "Upload failed"))
                    } else {
                        kotlinx.coroutines.delay(500L * attempt)
                    }
                }
            }
        }
    }
}
