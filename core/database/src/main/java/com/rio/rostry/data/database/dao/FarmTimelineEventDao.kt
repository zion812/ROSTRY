package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.FarmTimelineEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmTimelineEventDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: FarmTimelineEventEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<FarmTimelineEventEntity>)
    
    @Update
    suspend fun update(event: FarmTimelineEventEntity)
    
    @Query("SELECT * FROM farm_timeline_events WHERE eventId = :eventId")
    suspend fun findById(eventId: String): FarmTimelineEventEntity?
    
    // ========================================
    // Timeline Queries
    // ========================================
    
    /** Get public timeline for a farmer (for public profile view) */
    @Query("SELECT * FROM farm_timeline_events WHERE farmerId = :farmerId AND isPublic = 1 ORDER BY eventDate DESC LIMIT :limit")
    fun observePublicTimeline(farmerId: String, limit: Int = 50): Flow<List<FarmTimelineEventEntity>>
    
    /** Get all timeline for a farmer (for farmer's own view) */
    @Query("SELECT * FROM farm_timeline_events WHERE farmerId = :farmerId ORDER BY eventDate DESC LIMIT :limit")
    fun observeFullTimeline(farmerId: String, limit: Int = 100): Flow<List<FarmTimelineEventEntity>>
    
    /** Get timeline by event type */
    @Query("SELECT * FROM farm_timeline_events WHERE farmerId = :farmerId AND eventType = :type ORDER BY eventDate DESC LIMIT :limit")
    fun observeByType(farmerId: String, type: String, limit: Int = 20): Flow<List<FarmTimelineEventEntity>>
    
    /** Get milestones only */
    @Query("SELECT * FROM farm_timeline_events WHERE farmerId = :farmerId AND isMilestone = 1 ORDER BY eventDate DESC")
    fun observeMilestones(farmerId: String): Flow<List<FarmTimelineEventEntity>>
    
    /** Get recent events for dashboard */
    @Query("SELECT * FROM farm_timeline_events WHERE farmerId = :farmerId ORDER BY eventDate DESC LIMIT 5")
    fun getRecentEvents(farmerId: String): Flow<List<FarmTimelineEventEntity>>
    
    // ========================================
    // Visibility Control
    // ========================================
    
    @Query("UPDATE farm_timeline_events SET isPublic = :isPublic, updatedAt = :updatedAt, dirty = 1 WHERE eventId = :eventId")
    suspend fun setEventVisibility(eventId: String, isPublic: Boolean, updatedAt: Long)
    
    @Query("UPDATE farm_timeline_events SET isPublic = 0, updatedAt = :updatedAt, dirty = 1 WHERE farmerId = :farmerId AND eventType = :eventType")
    suspend fun hideAllByType(farmerId: String, eventType: String, updatedAt: Long)
    
    // ========================================
    // Duplicate Prevention
    // ========================================
    
    /** Check if event already exists for a source record */
    @Query("SELECT COUNT(*) > 0 FROM farm_timeline_events WHERE sourceType = :sourceType AND sourceId = :sourceId")
    suspend fun existsForSource(sourceType: String, sourceId: String): Boolean
    
    // ========================================
    // Event Counts (for badges/stats)
    // ========================================
    
    @Query("SELECT COUNT(*) FROM farm_timeline_events WHERE farmerId = :farmerId AND eventType = :type")
    suspend fun countByType(farmerId: String, type: String): Int
    
    @Query("SELECT SUM(trustPointsEarned) FROM farm_timeline_events WHERE farmerId = :farmerId")
    suspend fun getTotalTrustPoints(farmerId: String): Int?
    
    // ========================================
    // Sync
    // ========================================
    
    @Query("SELECT * FROM farm_timeline_events WHERE dirty = 1 LIMIT :limit")
    suspend fun getDirtyEvents(limit: Int = 100): List<FarmTimelineEventEntity>
    
    @Query("UPDATE farm_timeline_events SET dirty = 0, syncedAt = :syncedAt WHERE eventId = :eventId")
    suspend fun markSynced(eventId: String, syncedAt: Long)
    
    @Query("DELETE FROM farm_timeline_events WHERE farmerId = :farmerId")
    suspend fun deleteAllForFarmer(farmerId: String)
}
