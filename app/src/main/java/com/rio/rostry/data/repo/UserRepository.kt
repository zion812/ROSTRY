package com.rio.rostry.data.repo

import com.google.firebase.auth.AuthResult
import com.rio.rostry.data.models.User
import com.rio.rostry.utils.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(uid: String): Flow<User?>
    suspend fun loginUser(email: String, pass: String): Result<AuthResult>
    suspend fun registerUser(email: String, pass: String, phone: String?): Result<AuthResult>
    suspend fun updateUserProfile(user: User): Result<Unit>
    fun getCurrentUserUid(): String?
    suspend fun updateFcmToken(token: String): Result<Unit>
}
