package com.rio.rostry.data.account.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.account.repository.StorageRepository
import com.rio.rostry.domain.account.repository.StorageUsageRepository
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Simplified StorageRepository implementation for modularized data layer.
 */
@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val usageRepository: StorageUsageRepository,
    private val firebaseAuth: FirebaseAuth
) : StorageRepository {

    override suspend fun uploadFile(
        localUriString: String,
        remotePath: String,
        compress: Boolean,
        sizeLimitBytes: Long,
        onProgress: (percent: Int) -> Unit
    ): Result<StorageRepository.UploadResult> = withContext(Dispatchers.IO) {
        val userId = firebaseAuth.currentUser?.uid
            ?: return@withContext Result.Error(Exception("User not authenticated"))

        val localUri = Uri.parse(localUriString)
        val ref = firebaseStorage.reference.child(remotePath)
        val uploadTask: UploadTask = ref.putFile(localUri)

        return@withContext try {
            val uploadResult = suspendCancellableCoroutine<StorageRepository.UploadResult> { cont ->
                uploadTask
                    .addOnProgressListener { snap ->
                        val percent = if (snap.totalByteCount > 0) {
                            ((100.0 * snap.bytesTransferred) / snap.totalByteCount).toInt()
                        } else 0
                        onProgress(percent.coerceIn(0, 100))
                    }
                    .addOnSuccessListener { taskSnap ->
                        ref.downloadUrl
                            .addOnSuccessListener { url ->
                                cont.safeResume(
                                    StorageRepository.UploadResult(
                                        downloadUrl = url.toString(),
                                        bytesUploaded = taskSnap.bytesTransferred
                                    )
                                )
                            }
                            .addOnFailureListener { e -> cont.safeResumeWithException(e) }
                    }
                    .addOnFailureListener { e -> cont.safeResumeWithException(e) }

                cont.invokeOnCancellation { uploadTask.cancel() }
            }

            usageRepository.adjustUsage(userId, uploadResult.bytesUploaded)
            Result.Success(uploadResult)
        } catch (e: Exception) {
            
            Result.Error(e)
        }
    }

    override suspend fun deleteFile(remotePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        val userId = firebaseAuth.currentUser?.uid
            ?: return@withContext Result.Error(Exception("User not authenticated"))

        val ref = firebaseStorage.reference.child(remotePath)

        return@withContext try {
            val sizeBytes = runCatching {
                suspendCancellableCoroutine<Long> { cont ->
                    ref.metadata
                        .addOnSuccessListener { cont.resume(it.sizeBytes) }
                        .addOnFailureListener { cont.resume(0L) }
                }
            }.getOrDefault(0L)

            suspendCancellableCoroutine<Unit> { cont ->
                ref.delete()
                    .addOnSuccessListener { cont.resume(Unit) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }

            if (sizeBytes > 0) {
                usageRepository.adjustUsage(userId, -sizeBytes)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            
            Result.Error(e)
        }
    }

    private fun CancellableContinuation<StorageRepository.UploadResult>.safeResume(
        value: StorageRepository.UploadResult
    ) {
        if (isActive) resume(value)
    }

    private fun CancellableContinuation<StorageRepository.UploadResult>.safeResumeWithException(
        e: Exception
    ) {
        if (isActive) resumeWithException(e)
    }
}

