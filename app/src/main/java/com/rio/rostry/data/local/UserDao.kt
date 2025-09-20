package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isDeleted = 0")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE userType = :userType AND isDeleted = 0")
    fun getUsersByType(userType: UserType): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id AND isDeleted = 0")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE phone = :phone AND isDeleted = 0")
    suspend fun getUserByPhone(phone: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteUser(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET verificationStatus = :status, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateVerificationStatus(userId: String, status: VerificationStatus, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM users WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    suspend fun purgeDeletedUsers(beforeTimestamp: Long)
}