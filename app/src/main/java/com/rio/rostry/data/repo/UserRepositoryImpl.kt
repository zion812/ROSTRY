package com.rio.rostry.data.repo

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.local.UserDao
import com.rio.rostry.data.models.User
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : UserRepository {

    override fun getUser(uid: String): Flow<User?> = userDao.getUser(uid)

    override suspend fun loginUser(email: String, pass: String): Result<AuthResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid
            if (uid != null) {
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
                        isVerified = document.getBoolean("isVerified") ?: false
                    )
                    userDao.insertUser(user)
                }
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
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            // Ensure the user is inserted into the local database after profile creation
            userDao.insertUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCurrentUserUid(): String? = auth.currentUser?.uid

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.Error(Exception("User not logged in"))
        return try {
            firestore.collection("users").document(userId)
                .update("fcmToken", token).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
