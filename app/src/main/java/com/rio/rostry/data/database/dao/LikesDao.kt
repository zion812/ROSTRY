package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.LikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(like: LikeEntity)

    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun delete(postId: String, userId: String)

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    fun countLikes(postId: String): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE postId = :postId AND userId = :userId)")
    fun isLiked(postId: String, userId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE postId = :postId AND userId = :userId)")
    suspend fun isLikedSuspend(postId: String, userId: String): Boolean

    @Query("SELECT COUNT(*) FROM likes WHERE userId = :userId")
    suspend fun countByUser(userId: String): Int
}
