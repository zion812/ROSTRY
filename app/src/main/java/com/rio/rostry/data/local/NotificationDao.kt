package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE isDeleted = 0")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE id = :id AND isDeleted = 0")
    suspend fun getNotificationById(id: String): Notification?

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDeleted = 0")
    fun getNotificationsByUserId(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 AND isDeleted = 0")
    fun getUnreadNotificationsByUserId(userId: String): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE type = :type AND isDeleted = 0")
    fun getNotificationsByType(type: String): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Update
    suspend fun updateNotification(notification: Notification)

    @Query("UPDATE notifications SET isRead = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markNotificationAsRead(id: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notifications SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteNotification(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM notifications WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    suspend fun purgeDeletedNotifications(beforeTimestamp: Long)
}