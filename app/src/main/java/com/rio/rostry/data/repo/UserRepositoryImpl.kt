package com.rio.rostry.data.repo

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.local.UserDao
import com.rio.rostry.data.models.User
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
                val user = firestore.collection("users").document(uid).get().await().toObject(User::class.java)
                if (user != null) {
                    userDao.insertUser(user)
                }
            }
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(email: String, pass: String, phone: String?): Result<AuthResult> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            // Ensure the user is inserted into the local database after profile creation
            userDao.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserUid(): String? = auth.currentUser?.uid
}
