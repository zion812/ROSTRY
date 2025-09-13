package com.rio.rostry.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.local.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun streamByUser(userId: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    // De-duplication helper
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND title = :title AND message = :message")
    suspend fun countByUserAndTitleMessage(userId: String, title: String, message: String): Int

    // De-duplication with time window
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND title = :title AND message = :message AND createdAt >= :since")
    suspend fun countByUserAndTitleMessageSince(userId: String, title: String, message: String, since: Long): Int
}
