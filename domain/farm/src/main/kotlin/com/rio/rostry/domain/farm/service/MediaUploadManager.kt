package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for media upload orchestration.
 *
 * Manages background media uploads with retry logic, compression,
 * progress tracking, and outbox-backed persistence for reliability.
 */
interface MediaUploadManager {

    /** Upload event sealed hierarchy exposed to consumers. */
    sealed class UploadEvent {
        data class Progress(val remotePath: String, val percent: Int) : UploadEvent()
        data class Success(val remotePath: String, val downloadUrl: String) : UploadEvent()
        data class Failed(val remotePath: String, val error: String) : UploadEvent()
        data class Queued(val remotePath: String) : UploadEvent()
        data class Retrying(val remotePath: String, val attempt: Int) : UploadEvent()
        data class Cancelled(val remotePath: String) : UploadEvent()
    }

    /** Observable stream of upload events. */
    val events: Flow<UploadEvent>

    /** Enqueue a file for upload. */
    fun enqueue(localPath: String, remotePath: String, compress: Boolean = true)

    /** Enqueue to persistent outbox (survives process death). */
    fun enqueueToOutbox(localPath: String, remotePath: String, contextJson: String? = null)

    /** Cancel an active upload. */
    fun cancelUpload(remotePath: String)

    /** Get currently active upload paths. */
    fun getActiveUploadPaths(): Set<String>

    /** Check if a path is currently being uploaded. */
    fun isUploading(remotePath: String): Boolean
}
