package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.WatchedLineageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedLineageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(watch: WatchedLineageEntity)

    @Update
    suspend fun update(watch: WatchedLineageEntity)

    @Delete
    suspend fun delete(watch: WatchedLineageEntity)

    @Query("DELETE FROM watched_lineages WHERE watchId = :watchId")
    suspend fun deleteById(watchId: String)

    @Query("SELECT * FROM watched_lineages ORDER BY lastUpdateReceivedAt DESC")
    fun getAllWatched(): Flow<List<WatchedLineageEntity>>

    @Query("SELECT * FROM watched_lineages WHERE assetId = :assetId LIMIT 1")
    suspend fun findByAssetId(assetId: String): WatchedLineageEntity?

    @Query("SELECT * FROM watched_lineages WHERE lineageHash = :hash")
    fun getByLineageHash(hash: String): Flow<List<WatchedLineageEntity>>

    @Query("SELECT COUNT(*) FROM watched_lineages")
    fun observeWatchCount(): Flow<Int>

    @Query("UPDATE watched_lineages SET isDiscoveryFeedEnabled = :enabled WHERE watchId = :watchId")
    suspend fun setFeedEnabled(watchId: String, enabled: Boolean)
}
