package com.rio.rostry.data.account.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.account.repository.FeedbackRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FeedbackRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Account Domain repository migration
 */
@Singleton
class FeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FeedbackRepository {

    override suspend fun submitFeedback(
        userId: String,
        content: String,
        type: String
    ): Result<Boolean> {
        return try {
            val feedback = hashMapOf(
                "userId" to userId,
                "content" to content,
                "type" to type,
                "createdAt" to System.currentTimeMillis(),
                "status" to "OPEN"
            )
            firestore.collection("grievances").add(feedback).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

