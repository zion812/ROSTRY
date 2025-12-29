package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.database.dao.VerificationRequestDao
import com.rio.rostry.data.database.entity.VerificationRequestEntity
import com.rio.rostry.data.repository.StorageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for step-by-step verification document upload.
 * 
 * Implements a 3-step upload process:
 * 1. Upload Govt ID image → store URL in local entity
 * 2. Upload Farm Photo → store URL in local entity
 * 3. Write VerificationRequest document to Firestore
 * 
 * On failure at any step:
 * - Updates local entity status to DRAFT
 * - Returns Result.retry() with exponential backoff
 * 
 * This ensures robustness and allows the user to retry later.
 */
@HiltWorker
class VerificationUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val verificationRequestDao: VerificationRequestDao,
    private val storageRepository: StorageRepository,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_REQUEST_ID = "request_id"
        const val KEY_USER_ID = "user_id"
        const val KEY_GOVT_ID_URI = "govt_id_uri"
        const val KEY_FARM_PHOTO_URI = "farm_photo_uri"
        
        private const val FIRESTORE_COLLECTION = "verification_requests"
        
        /**
         * Enqueue a verification upload work request.
         */
        fun enqueue(
            context: Context,
            requestId: String,
            userId: String,
            govtIdUri: String,
            farmPhotoUri: String
        ) {
            val inputData = workDataOf(
                KEY_REQUEST_ID to requestId,
                KEY_USER_ID to userId,
                KEY_GOVT_ID_URI to govtIdUri,
                KEY_FARM_PHOTO_URI to farmPhotoUri
            )
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = OneTimeWorkRequestBuilder<VerificationUploadWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .addTag("verification_upload_$requestId")
                .build()
            
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    override suspend fun doWork(): Result {
        val requestId = inputData.getString(KEY_REQUEST_ID) ?: return Result.failure()
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()
        val govtIdUri = inputData.getString(KEY_GOVT_ID_URI) ?: return Result.failure()
        val farmPhotoUri = inputData.getString(KEY_FARM_PHOTO_URI) ?: return Result.failure()
        
        return try {
            // Get or create local entity
            var request = verificationRequestDao.getById(requestId)
            if (request == null) {
                request = VerificationRequestEntity(
                    requestId = requestId,
                    userId = userId,
                    status = VerificationRequestEntity.STATUS_DRAFT,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                verificationRequestDao.insert(request)
            }
            
            // Step 1: Upload Govt ID
            val govtIdUrl = if (request.govtIdUrl.isNullOrBlank()) {
                val remotePath = "verification/$userId/govt_id_${System.currentTimeMillis()}.jpg"
                val uploadResult = storageRepository.uploadFile(govtIdUri, remotePath)
                when (uploadResult) {
                    is com.rio.rostry.utils.Resource.Success -> {
                        val url = uploadResult.data!!.downloadUrl
                        verificationRequestDao.updateGovtIdUrl(requestId, url)
                        url
                    }
                    else -> {
                        // Step 1 failed - mark as draft and retry
                        verificationRequestDao.updateStatus(requestId, VerificationRequestEntity.STATUS_DRAFT)
                        return Result.retry()
                    }
                }
            } else {
                request.govtIdUrl
            }
            
            // Step 2: Upload Farm Photo
            val farmPhotoUrl = if (request.farmPhotoUrl.isNullOrBlank()) {
                val remotePath = "verification/$userId/farm_photo_${System.currentTimeMillis()}.jpg"
                val uploadResult = storageRepository.uploadFile(farmPhotoUri, remotePath)
                when (uploadResult) {
                    is com.rio.rostry.utils.Resource.Success -> {
                        val url = uploadResult.data!!.downloadUrl
                        verificationRequestDao.updateFarmPhotoUrl(requestId, url)
                        url
                    }
                    else -> {
                        // Step 2 failed - mark as draft and retry
                        verificationRequestDao.updateStatus(requestId, VerificationRequestEntity.STATUS_DRAFT)
                        return Result.retry()
                    }
                }
            } else {
                request.farmPhotoUrl
            }
            
            // Step 3: Write to Firestore
            val firestoreData = hashMapOf(
                "requestId" to requestId,
                "userId" to userId,
                "govtIdUrl" to govtIdUrl,
                "farmPhotoUrl" to farmPhotoUrl,
                "status" to VerificationRequestEntity.STATUS_PENDING,
                "submittedAt" to System.currentTimeMillis(),
                "createdAt" to request.createdAt
            )
            
            try {
                firestore.collection(FIRESTORE_COLLECTION)
                    .document(userId)
                    .set(firestoreData)
                    .await()
                
                // Mark as submitted locally
                verificationRequestDao.markSubmitted(requestId)
                
                Result.success()
            } catch (e: Exception) {
                // Step 3 failed - mark as draft and retry
                verificationRequestDao.updateStatus(requestId, VerificationRequestEntity.STATUS_DRAFT)
                Result.retry()
            }
            
        } catch (e: Exception) {
            // Unexpected error - mark as draft and retry
            try {
                verificationRequestDao.updateStatus(requestId, VerificationRequestEntity.STATUS_DRAFT)
            } catch (_: Exception) { }
            
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
