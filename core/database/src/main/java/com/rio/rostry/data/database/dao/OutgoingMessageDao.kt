package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.OutgoingMessageEntity

@Dao
interface OutgoingMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(msg: OutgoingMessageEntity)

    @Update
    suspend fun update(msg: OutgoingMessageEntity)

    @Query("SELECT * FROM outgoing_messages WHERE status = :status ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getByStatus(status: String, limit: Int = 50): List<OutgoingMessageEntity>

    @Query("UPDATE outgoing_messages SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}
