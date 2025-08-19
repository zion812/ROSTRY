package com.rio.rostry.data.repository

import com.rio.rostry.data.models.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

class UserProfileRepository(firestore: FirebaseFirestore? = null) {
    private val firestoreInstance = firestore ?: FirebaseFirestore.getInstance()
    private val usersCollection = firestoreInstance.collection("users")

    suspend fun getProfile(userId: String): Result<UserProfile?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val profile = document.toObject(UserProfile::class.java)
                Result.success(profile)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        return try {
            usersCollection.document(profile.uid).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun DocumentSnapshot.toObject(clazz: Class<UserProfile>): UserProfile? {
        return try {
            this.toObject(clazz)
        } catch (e: Exception) {
            null
        }
    }
}