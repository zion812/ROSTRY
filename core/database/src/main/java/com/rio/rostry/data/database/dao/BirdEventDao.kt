package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.BirdEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the bird_events table — unified event log.
 */
@Dao
interface BirdEventDao {

    // ==================== INSERTS ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: BirdEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<BirdEventEntity>)

    // ==================== QUERIES (by bird) ====================

    /** Get all events for a bird, most recent first */
    @Query("SELECT * FROM bird_events WHERE birdId = :birdId ORDER BY eventDate DESC")
    fun getEventsForBird(birdId: String): Flow<List<BirdEventEntity>>

    /** Get events for a bird by type */
    @Query("SELECT * FROM bird_events WHERE birdId = :birdId AND eventType = :eventType ORDER BY eventDate DESC")
    fun getEventsForBirdByType(birdId: String, eventType: String): Flow<List<BirdEventEntity>>

    /** Get the latest event of a specific type for a bird */
    @Query("SELECT * FROM bird_events WHERE birdId = :birdId AND eventType = :eventType ORDER BY eventDate DESC LIMIT 1")
    suspend fun getLatestEvent(birdId: String, eventType: String): BirdEventEntity?

    /** Get recent events for a bird (last N) */
    @Query("SELECT * FROM bird_events WHERE birdId = :birdId ORDER BY eventDate DESC LIMIT :limit")
    suspend fun getRecentEvents(birdId: String, limit: Int = 20): List<BirdEventEntity>

    // ==================== QUERIES (by owner) ====================

    /** Get all events for an owner's birds */
    @Query("SELECT * FROM bird_events WHERE ownerId = :ownerId ORDER BY eventDate DESC LIMIT :limit")
    fun getEventsForOwner(ownerId: String, limit: Int = 100): Flow<List<BirdEventEntity>>

    // ==================== QUERIES (by type) ====================

    /** Get all fight events for a bird */
    @Query("""
        SELECT * FROM bird_events 
        WHERE birdId = :birdId 
        AND eventType IN ('FIGHT_WIN', 'FIGHT_LOSS', 'FIGHT_DRAW') 
        ORDER BY eventDate DESC
    """)
    fun getFightHistory(birdId: String): Flow<List<BirdEventEntity>>

    /** Get breeding history for a bird */
    @Query("""
        SELECT * FROM bird_events 
        WHERE birdId = :birdId 
        AND eventType IN ('BREEDING_ATTEMPT', 'BREEDING_SUCCESS', 'BREEDING_FAILURE', 'CLUTCH_LAID', 'HATCHING_COMPLETE') 
        ORDER BY eventDate DESC
    """)
    fun getBreedingHistory(birdId: String): Flow<List<BirdEventEntity>>

    /** Get health events for a bird */
    @Query("""
        SELECT * FROM bird_events 
        WHERE birdId = :birdId 
        AND eventType IN ('VACCINATION', 'ILLNESS', 'INJURY', 'RECOVERY', 'DEWORMING') 
        ORDER BY eventDate DESC
    """)
    fun getHealthHistory(birdId: String): Flow<List<BirdEventEntity>>

    /** Get weight recordings over time (for growth charts) */
    @Query("""
        SELECT * FROM bird_events 
        WHERE birdId = :birdId AND eventType = 'WEIGHT_RECORDED' 
        ORDER BY eventDate ASC
    """)
    fun getWeightHistory(birdId: String): Flow<List<BirdEventEntity>>

    // ==================== SCORE IMPACT ====================

    /** Sum all score deltas for a bird (for composite score aggregation) */
    @Query("""
        SELECT 
            COALESCE(SUM(morphologyScoreDelta), 0) as morphDelta,
            COALESCE(SUM(geneticsScoreDelta), 0) as genDelta,
            COALESCE(SUM(performanceScoreDelta), 0) as perfDelta,
            COALESCE(SUM(healthScoreDelta), 0) as healthDelta,
            COALESCE(SUM(marketScoreDelta), 0) as marketDelta
        FROM bird_events WHERE birdId = :birdId
    """)
    suspend fun getScoreDeltas(birdId: String): ScoreDeltaSummary

    // ==================== COUNTS ====================

    /** Count events by type for a bird */
    @Query("SELECT COUNT(*) FROM bird_events WHERE birdId = :birdId AND eventType = :eventType")
    suspend fun countEvents(birdId: String, eventType: String): Int

    /** Count fight wins for a bird */
    @Query("SELECT COUNT(*) FROM bird_events WHERE birdId = :birdId AND eventType = 'FIGHT_WIN'")
    suspend fun countFightWins(birdId: String): Int

    /** Count total fights for a bird */
    @Query("SELECT COUNT(*) FROM bird_events WHERE birdId = :birdId AND eventType IN ('FIGHT_WIN', 'FIGHT_LOSS', 'FIGHT_DRAW')")
    suspend fun countTotalFights(birdId: String): Int

    // ==================== SYNC ====================

    @Query("SELECT * FROM bird_events WHERE dirty = 1")
    suspend fun getDirtyEvents(): List<BirdEventEntity>

    @Query("UPDATE bird_events SET dirty = 0, syncedAt = :syncedAt WHERE eventId = :eventId")
    suspend fun markSynced(eventId: String, syncedAt: Long = System.currentTimeMillis())

    // ==================== DELETE ====================

    /** Events are normally append-only, but this is for data cleanup */
    @Query("DELETE FROM bird_events WHERE birdId = :birdId")
    suspend fun deleteAllForBird(birdId: String)
}

/**
 * Projection class for score delta aggregation queries.
 */
data class ScoreDeltaSummary(
    val morphDelta: Int,
    val genDelta: Int,
    val perfDelta: Int,
    val healthDelta: Int,
    val marketDelta: Int
)
