package com.rio.rostry.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FeedbackRepository {

    override suspend fun submitFeedback(userId: String, content: String, type: String): Resource<Boolean> {
        return try {
            val feedback = hashMapOf(
                "userId" to userId,
                "content" to content,
                "type" to type,
                "createdAt" to System.currentTimeMillis(),
                "status" to "OPEN" // Admin can manage this later
            )
            firestore.collection("grievances").add(feedback).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit feedback")
        }
    }
}
