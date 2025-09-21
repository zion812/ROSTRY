package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus

interface UserRepository {

    fun getCurrentUser(): Flow<Resource<UserEntity?>>

    suspend fun refreshCurrentUser(userId: String): Resource<Unit>

    suspend fun updateUserProfile(userEntity: UserEntity): Resource<Unit>

    fun getUserById(userId: String): Flow<Resource<UserEntity?>>

    suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit>

    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit>

    // Add other user-related operations like:
    // suspend fun signInWithEmail(email: String, password: String): Resource<UserEntity>
    // suspend fun signUpWithEmail(email: String, password: String, fullName: String): Resource<UserEntity>
    // suspend fun signOut(): Resource<Unit>
    // suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    // suspend fun deleteAccount(): Resource<Unit>
}
