package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts WHERE userId = :userId AND isDismissed = 0 ORDER BY createdAt DESC")
    fun streamAlerts(userId: String): Flow<List<AlertEntity>>

    @Query("SELECT COUNT(*) FROM alerts WHERE userId = :userId AND isRead = 0 AND isDismissed = 0")
    fun countUnread(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :alertId")
    suspend fun markAsRead(alertId: String)

    @Query("UPDATE alerts SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("UPDATE alerts SET isDismissed = 1 WHERE id = :alertId")
    suspend fun dismiss(alertId: String)
    
    @Query("DELETE FROM alerts WHERE createdAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
