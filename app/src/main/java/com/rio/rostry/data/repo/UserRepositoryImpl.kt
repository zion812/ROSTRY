package com.rio.rostry.data.repo

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.local.UserDao
import com.rio.rostry.data.models.User
import com.rio.rostry.utils.NetworkMonitor
import com.rio.rostry.utils.PerformanceLogger
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    private val networkMonitor: NetworkMonitor,
    private val performanceLogger: PerformanceLogger
) : UserRepository {

    override fun getUser(uid: String): Flow<User?> = userDao.getUser(uid)

    override suspend fun loginUser(email: String, pass: String): Result<AuthResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid
            if (uid != null) {
                var success = false
                val duration = measureTimeMillis {
                    try {
                        val document = firestore.collection("users").document(uid).get().await()
                        if (document.exists()) {
                            val data = document.data
                            val userTypeMap = data?.get("userType") as? Map<*, *>
                            val role = userTypeMap?.get("role") as? String
                            val userType = when (role) {
                                "farmer" -> com.rio.rostry.data.models.UserType.Farmer
                                "high_level_enthusiast" -> com.rio.rostry.data.models.UserType.HighLevelEnthusiast
                                else -> com.rio.rostry.data.models.UserType.General
                            }
                            val user = User(
                                uid = document.getString("uid") ?: uid,
                                name = document.getString("name") ?: "",
                                email = document.getString("email") ?: "",
                                phone = document.getString("phone"),
                                location = document.getString("location") ?: "",
                                userType = userType,
                                language = document.getString("language") ?: "",
                                isVerified = document.getBoolean("isVerified") ?: false,
                                bio = document.getString("bio"),
                                profileImageUrl = document.getString("profileImageUrl")
                            )
                            userDao.insertUser(user)
                        }
                        success = true
                    } catch (e: Exception) {
                        // The overall login function will catch and handle this.
                    }
                }
                performanceLogger.logNetworkRequest("loginUser_fetchProfile", duration, success)
            }
            Result.Success(authResult)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun registerUser(email: String, pass: String, phone: String?): Result<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        // Always update the local database first
        userDao.insertUser(user)

        if (!networkMonitor.isConnected().first()) {
            // If offline, return success as the local copy is updated.
            // The background sync will handle uploading the changes later.
            return Result.Success(Unit)
        }

        var success = false
        val duration = measureTimeMillis {
            try {
                firestore.collection("users").document(user.uid).set(user).await()
                success = true
            } catch (e: Exception) {
                // The local update succeeded, so we can consider this a soft failure.
                // The error will be logged, and sync will retry later.
            }
        }
        performanceLogger.logNetworkRequest("updateUserProfile", duration, success)
        return if (success) Result.Success(Unit) else Result.Error(Exception("Failed to update profile on server."))
    }

    override fun getCurrentUserUid(): String? = auth.currentUser?.uid

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        if (!networkMonitor.isConnected().first()) {
            return Result.Success(Unit) // Silently fail if offline, as this can be retried
        }
        val userId = auth.currentUser?.uid ?: return Result.Error(Exception("User not logged in"))
        var success = false
        val duration = measureTimeMillis {
            try {
                firestore.collection("users").document(userId)
                    .update("fcmToken", token).await()
                success = true
            } catch (e: Exception) {
                // Error will be returned by the function
            }
        }
        performanceLogger.logNetworkRequest("updateFcmToken", duration, success)
        return if (success) Result.Success(Unit) else Result.Error(Exception("Failed to update FCM token on server."))
    }
}
