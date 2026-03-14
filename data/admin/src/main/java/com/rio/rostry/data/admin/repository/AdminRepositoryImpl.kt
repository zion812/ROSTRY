package com.rio.rostry.data.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import com.rio.rostry.core.model.AdminMetrics
import com.rio.rostry.domain.admin.model.AdminUserProfile
import com.rio.rostry.domain.admin.repository.AdminRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AdminRepository using Firebase Firestore.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.2 - Data modules provide concrete implementations
 */
@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    private val usersCollection = firestore.collection("users")
    private val metricsCollection = firestore.collection("admin_metrics")
    private val logsCollection = firestore.collection("system_logs")
    private val featureTogglesCollection = firestore.collection("feature_toggles")

    override suspend fun getAdminMetrics(): Result<AdminMetrics> {
        return try {
            val document = metricsCollection.document("current").get().await()
            if (document.exists()) {
                val metrics = document.toObject(AdminMetrics::class.java)
                if (metrics != null) {
                    Result.Success(metrics)
                } else {
                    Result.Error(Exception("Failed to parse metrics data"))
                }
            } else {
                Result.Error(Exception("Metrics not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = usersCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull {
                    it.toObject(User::class.java)
                } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    override fun getUserFullProfile(userId: String): Flow<Result<AdminUserProfile>> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error))
                    return@addSnapshotListener
                }
                val userEntity = snapshot?.toObject(com.rio.rostry.data.database.entity.UserEntity::class.java)
                if (userEntity != null) {
                    trySend(Result.Success(AdminUserProfile(user = userEntity)))
                } else {
                    trySend(Result.Error(Exception("User not found")))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun banUser(userId: String, reason: String): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update(
                    mapOf(
                        "banned" to true,
                        "banReason" to reason
                    )
                )
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unbanUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update(
                    mapOf(
                        "banned" to false,
                        "banReason" to null
                    )
                )
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getSystemLogs(): Flow<List<String>> = callbackFlow {
        val listener = logsCollection
            .orderBy("timestamp")
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val logs = snapshot?.documents?.mapNotNull {
                    it.getString("message")
                } ?: emptyList()
                trySend(logs)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateFeatureToggle(key: String, enabled: Boolean): Result<Unit> {
        return try {
            featureTogglesCollection.document(key)
                .set(mapOf("enabled" to enabled))
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
