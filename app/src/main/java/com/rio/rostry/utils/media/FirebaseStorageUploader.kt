package com.rio.rostry.utils.media

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask
import com.rio.rostry.utils.images.ImageCompressor
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseStorageUploader(
    private val firebaseStorage: FirebaseStorage,
    private val imageCompressor: ImageCompressor,
    private val connectivityManager: ConnectivityManager,
    private val appContext: Context,
) {
    data class UploadResult(
        val downloadUrl: String,
        val bytesUploaded: Long,
    )

    suspend fun uploadFile(
        localUriString: String,
        remotePath: String,
        compress: Boolean = true,
        sizeLimitBytes: Long = 5_000_000L,
        onProgress: (percent: Int) -> Unit = {},
    ): UploadResult = withContext(Dispatchers.IO) {
        if (!connectivityManager.isOnline()) throw IllegalStateException("Offline")

        val localUri = Uri.parse(localUriString)
        val (preparedUri, cleanupFile) = prepareLocalUri(localUri, compress, sizeLimitBytes)
        try {
            val ref = firebaseStorage.reference.child(remotePath)
            val uploadTask: UploadTask = ref.putFile(preparedUri)

            suspendCancellableCoroutine<UploadResult> { cont ->
                uploadTask
                    .addOnProgressListener { snap ->
                        val percent = if (snap.totalByteCount > 0) ((100.0 * snap.bytesTransferred) / snap.totalByteCount).toInt() else 0
                        onProgress(percent.coerceIn(0, 100))
                    }
                    .addOnSuccessListener { taskSnap ->
                        ref.downloadUrl
                            .addOnSuccessListener { url ->
                                cont.safeResume(UploadResult(downloadUrl = url.toString(), bytesUploaded = taskSnap.bytesTransferred))
                            }
                            .addOnFailureListener { e ->
                                cont.safeResumeWithException(e)
                            }
                    }
                    .addOnFailureListener { e ->
                        cont.safeResumeWithException(e)
                    }
                cont.invokeOnCancellation {
                    uploadTask.cancel()
                }
            }
        } finally {
            // Clean temporary file if created
            cleanupFile?.let { runCatching { it.delete() } }
        }
    }

    suspend fun uploadBatch(
        tasks: List<Triple<String, String, Boolean>>, // (localUri, remotePath, compress)
        sizeLimitBytes: Long = 5_000_000L,
        onItemProgress: (index: Int, percent: Int) -> Unit = { _, _ -> },
    ): List<Result<UploadResult>> {
        val results = mutableListOf<Result<UploadResult>>()
        for ((index, t) in tasks.withIndex()) {
            val (local, remote, compress) = t
            val r = runCatching {
                uploadFile(
                    localUriString = local,
                    remotePath = remote,
                    compress = compress,
                    sizeLimitBytes = sizeLimitBytes,
                    onProgress = { p -> onItemProgress(index, p) }
                )
            }
            results.add(r)
        }
        return results
    }

    private fun CancellableContinuation<UploadResult>.safeResume(value: UploadResult) {
        if (isActive) resume(value)
    }
    private fun CancellableContinuation<UploadResult>.safeResumeWithException(e: Exception) {
        if (isActive) resumeWithException(e)
    }

    private suspend fun prepareLocalUri(
        uri: Uri,
        compress: Boolean,
        sizeLimitBytes: Long,
    ): Pair<Uri, File?> = withContext(Dispatchers.IO) {
        val cr = appContext.contentResolver
        val type = cr.getType(uri) ?: ""
        val isImage = type.startsWith("image/")
        if (!compress || !isImage) return@withContext uri to null

        // Copy to cache file first
        val tmp = File.createTempFile("upload_src_", ".tmp", appContext.cacheDir)
        cr.openInputStream(uri).use { input ->
            if (input == null) return@withContext uri to null
            FileOutputStream(tmp).use { out ->
                input.copyTo(out)
            }
        }
        val lowBandwidth = sizeLimitBytes <= 1_500_000L
        val compressed = ImageCompressor.compressForUpload(appContext, tmp, lowBandwidth)
        val finalFile = compressed ?: tmp
        return@withContext Uri.fromFile(finalFile) to finalFile
    }
}
