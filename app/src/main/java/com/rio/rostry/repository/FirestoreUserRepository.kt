package com.rio.rostry.repository

import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository : FirestoreRepository() {
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = "users"

    suspend fun createUser(user: User): Flow<Result<String>> {
        return addDocument(usersCollection, user)
    }

    suspend fun updateUser(userId: String, user: User): Flow<Result<Unit>> {
        return updateDocument(usersCollection, userId, user)
    }

    suspend fun getUser(userId: String): Flow<Result<User?>> {
        return getDocument(usersCollection, userId, User::class.java)
    }

    suspend fun getCurrentUser(): Flow<Result<User?>> = flow {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            getUser(currentUser.uid).collect { result ->
                emit(result)
            }
        } else {
            emit(Result.success(null))
        }
    }
}