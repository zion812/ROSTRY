package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    fun getNotificationById(notificationId: String): Flow<NotificationEntity?>

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotificationsByUserId(userId: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsReadForUser(userId: String)

    @Query("DELETE FROM notifications WHERE notificationId = :notificationId")
    suspend fun deleteNotificationById(notificationId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteNotificationsForUser(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId AND isRead = 1")
    suspend fun deleteReadNotificationsForUser(userId: String)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isBatched = 1 ORDER BY createdAt ASC")
    suspend fun getBatchedNotifications(userId: String): List<NotificationEntity>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isBatched = 1")
    fun observeBatchedCount(userId: String): Flow<Int>

    @Query("UPDATE notifications SET isBatched = 0, displayedAt = :displayedAt WHERE notificationId IN (:notificationIds)")
    suspend fun markBatchDisplayed(notificationIds: List<String>, displayedAt: Long)

    @Query("SELECT * FROM notifications WHERE userId = :userId AND domain = :domain ORDER BY createdAt DESC LIMIT :limit")
    fun getNotificationsByDomain(userId: String, domain: String, limit: Int): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE userId = :userId AND isRead = 1 AND displayedAt < :threshold")
    suspend fun deleteOldReadNotifications(userId: String, threshold: Long)
}