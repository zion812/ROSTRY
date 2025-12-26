package com.rio.rostry.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.images.ImageCompressor
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Repository for Firebase Storage operations with quota enforcement.
 */
@Singleton
class StorageRepository @Inject constructor(
    @Named("verificationStorage") private val firebaseStorage: FirebaseStorage,
    private val usageRepository: StorageUsageRepository,
    private val firebaseAuth: FirebaseAuth,
    private val connectivityManager: ConnectivityManager,
    private val appContext: Context
) {
    data class UploadResult(
        val downloadUrl: String,
        val bytesUploaded: Long,
    )

    /**
     * Uploads a file to Firebase Storage after checking quotas.
     */
    suspend fun uploadFile(
        localUriString: String,
        remotePath: String,
        compress: Boolean = true,
        sizeLimitBytes: Long = 20_000_000L,
        onProgress: (percent: Int) -> Unit = {},
    ): Resource<UploadResult> = withContext(Dispatchers.IO) {
        if (!connectivityManager.isOnline()) return@withContext Resource.Error("Offline")
        
        val userId = firebaseAuth.currentUser?.uid ?: return@withContext Resource.Error("User not authenticated")

        // 1. Pre-upload quota check
        if (!usageRepository.hasEnoughSpace(userId, 0)) {
            return@withContext Resource.Error("Storage quota exceeded. Please upgrade your plan.")
        }

        val localUri = Uri.parse(localUriString)
        val (preparedUri, cleanupFile) = prepareLocalUri(localUri, compress, sizeLimitBytes)
        
        try {
            val ref = firebaseStorage.reference.child(remotePath)
            val uploadTask: UploadTask = ref.putFile(preparedUri)

            val result = suspendCancellableCoroutine<UploadResult> { cont ->
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
            
            // 2. Post-upload quota update
            usageRepository.adjustUsage(userId, result.bytesUploaded)
            
            // 3. Cleanup
            cleanupOriginalIfCached(localUriString)
            
            Resource.Success(result)
        } catch (e: Exception) {
            Timber.e(e, "Upload failed for $remotePath")
            Resource.Error(e.message ?: "Upload failed")
        } finally {
            cleanupFile?.let { runCatching { it.delete() } }
        }
    }

    suspend fun deleteFile(remotePath: String): Resource<Unit> = withContext(Dispatchers.IO) {
        val userId = firebaseAuth.currentUser?.uid ?: return@withContext Resource.Error("User not authenticated")
        
        try {
            val ref = firebaseStorage.reference.child(remotePath)
            // Get size before deletion to update quota
            val metadata = runCatching { 
                suspendCancellableCoroutine<Long> { cont ->
                    ref.metadata.addOnSuccessListener { cont.resume(it.sizeBytes) }
                        .addOnFailureListener { cont.resume(0L) }
                }
            }.getOrDefault(0L)

            suspendCancellableCoroutine<Unit> { cont ->
                ref.delete()
                    .addOnSuccessListener { cont.resume(Unit) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
            
            // Update quota (negative delta)
            if (metadata > 0) {
                usageRepository.adjustUsage(userId, -metadata)
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Delete failed for $remotePath")
            Resource.Error(e.message ?: "Delete failed")
        }
    }

    private fun cleanupOriginalIfCached(localUriString: String) {
        if (!localUriString.startsWith("file://")) return
        try {
            val path = if (localUriString.startsWith("file:///")) localUriString.substring(7) else localUriString.substring(5)
            val file = File(path)
            val cacheDir = appContext.cacheDir.absolutePath
            if (file.absolutePath.startsWith(cacheDir) && file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {}
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

        val tmp = File.createTempFile("upload_src_", ".tmp", appContext.cacheDir)
        cr.openInputStream(uri).use { input ->
            if (input == null) return@withContext uri to null
            FileOutputStream(tmp).use { out ->
                input.copyTo(out)
            }
        }
        val sizeMB = tmp.length() / (1024 * 1024)
        val compressed = if (sizeMB > 10) {
            ImageCompressor.compressForLargeUpload(appContext, tmp)
        } else {
            // Use network type to determine bandwidth mode
            // If not on WiFi, assume lower bandwidth/metered connection => aggressive compression
            val lowBandwidth = !connectivityManager.isOnWifi()
            ImageCompressor.compressForUpload(appContext, tmp, lowBandwidth)
        }
        val finalFile = if (compressed != null && compressed.exists()) compressed else tmp
        return@withContext Uri.fromFile(finalFile) to finalFile
    }
}
