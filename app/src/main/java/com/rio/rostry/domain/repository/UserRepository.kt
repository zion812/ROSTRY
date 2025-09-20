package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByType(userType: UserType): Flow<List<User>>
    suspend fun getUserById(id: String): User?
    suspend fun getUserByPhone(phone: String): User?
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus)
}