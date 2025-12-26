package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.rio.rostry.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserIgnore(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsersIgnore(users: List<UserEntity>): List<Long>

    @Transaction
    suspend fun upsertUser(user: UserEntity) {
        insertUserIgnore(user)
        updateUser(user)
    }

    @Transaction
    suspend fun upsertUsers(users: List<UserEntity>) {
        insertUsersIgnore(users)
        updateUsers(users)
    }

    /* Kept for potential strict replace needs, but check usage! */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserReplace(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Update
    suspend fun updateUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun findById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId IN (:ids)")
    suspend fun getUsersByIds(ids: List<String>): List<UserEntity>

    @Query("SELECT * FROM users ORDER BY fullName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :query || '%' OR userId LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>
}
