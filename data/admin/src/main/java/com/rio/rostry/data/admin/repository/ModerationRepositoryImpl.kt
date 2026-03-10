package com.rio.rostry.data.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.ModerationAction
import com.rio.rostry.domain.admin.repository.ModerationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ModerationRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class ModerationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ModerationRepository {

    private val moderationQueueCollection = firestore.collection("moderation_queue")
    private val moderationActionsCollection = firestore.collection("moderation_actions")

    override fun getPendingItems(): Flow<List<String>> = callbackFlow {
        val listener = moderationQueueCollection
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull {
                    it.id
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun approveItem(itemId: String, itemType: String): Result<Unit> {
        return try {
            moderationQueueCollection.document(itemId)
                .update("status", "APPROVED")
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun rejectItem(itemId: String, itemType: String, reason: String): Result<Unit> {
        return try {
            moderationQueueCollection.document(itemId)
                .update(
                    mapOf(
                        "status" to "REJECTED",
                        "rejectionReason" to reason
                    )
                )
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logAction(action: ModerationAction): Result<Unit> {
        return try {
            moderationActionsCollection.add(action).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getModerationHistory(moderatorId: String): Flow<List<ModerationAction>> = callbackFlow {
        val listener = moderationActionsCollection
            .whereEqualTo("moderatorId", moderatorId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val actions = snapshot?.documents?.mapNotNull {
                    it.toObject(ModerationAction::class.java)
                } ?: emptyList()
                trySend(actions)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getQueueCount(): Result<Int> {
        return try {
            val snapshot = moderationQueueCollection
                .whereEqualTo("status", "PENDING")
                .get()
                .await()
            Result.Success(snapshot.size())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
