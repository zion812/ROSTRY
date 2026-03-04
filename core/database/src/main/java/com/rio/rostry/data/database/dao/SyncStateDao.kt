package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.SyncStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncStateDao {
    @Upsert
    suspend fun upsert(state: SyncStateEntity)

    @Query("SELECT * FROM sync_state WHERE id = 'global' LIMIT 1")
    fun observe(): Flow<SyncStateEntity?>

    @Query("SELECT * FROM sync_state WHERE id = 'global' LIMIT 1")
    suspend fun get(): SyncStateEntity?
}
