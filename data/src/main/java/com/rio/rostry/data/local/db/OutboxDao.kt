package com.rio.rostry.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: OutboxEntity)

    @Query("SELECT * FROM outbox ORDER BY createdAt ASC LIMIT 1")
    suspend fun oldest(): OutboxEntity?

    @Query("SELECT * FROM outbox ORDER BY createdAt ASC")
    fun streamAll(): Flow<List<OutboxEntity>>

    @Delete
    suspend fun delete(item: OutboxEntity)

    @Query("UPDATE outbox SET attempt = attempt + 1 WHERE id = :id")
    suspend fun incrementAttempt(id: String)
}
