package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.MedicalEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for MedicalEventEntity operations.
 * 
 * Provides comprehensive queries for tracking health events like:
 * - Illnesses and diagnoses
 * - Injuries and treatments
 * - Health checkups
 * - Deworming
 */
@Dao
interface MedicalEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: MedicalEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<MedicalEventEntity>)

    @Update
    suspend fun update(event: MedicalEventEntity)

    @Query("DELETE FROM medical_events WHERE eventId = :eventId")
    suspend fun delete(eventId: String)

    @Query("SELECT * FROM medical_events WHERE eventId = :eventId")
    suspend fun findById(eventId: String): MedicalEventEntity?

    @Query("SELECT * FROM medical_events WHERE birdId = :birdId ORDER BY eventDate DESC")
    fun observeByBird(birdId: String): Flow<List<MedicalEventEntity>>

    @Query("SELECT * FROM medical_events WHERE birdId = :birdId ORDER BY eventDate DESC")
    suspend fun getByBird(birdId: String): List<MedicalEventEntity>

    @Query("SELECT * FROM medical_events WHERE farmerId = :farmerId ORDER BY eventDate DESC")
    fun observeByFarmer(farmerId: String): Flow<List<MedicalEventEntity>>

    @Query("SELECT * FROM medical_events WHERE farmerId = :farmerId ORDER BY eventDate DESC")
    suspend fun getByFarmer(farmerId: String): List<MedicalEventEntity>

    // Active health issues (not resolved)
    @Query("""
        SELECT * FROM medical_events 
        WHERE farmerId = :farmerId 
        AND status = 'ACTIVE' 
        ORDER BY eventDate DESC
    """)
    fun observeActiveIssues(farmerId: String): Flow<List<MedicalEventEntity>>

    // Filter by event type
    @Query("""
        SELECT * FROM medical_events 
        WHERE farmerId = :farmerId 
        AND eventType = :eventType 
        ORDER BY eventDate DESC
    """)
    fun observeByType(farmerId: String, eventType: String): Flow<List<MedicalEventEntity>>

    // Health stats
    @Query("SELECT COUNT(*) FROM medical_events WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun countActiveIssues(farmerId: String): Int

    @Query("SELECT COUNT(*) FROM medical_events WHERE farmerId = :farmerId AND eventType = 'ILLNESS' AND eventDate >= :since")
    suspend fun countIllnessesSince(farmerId: String, since: Long): Int

    // Cost tracking
    @Query("SELECT SUM(cost) FROM medical_events WHERE farmerId = :farmerId AND eventDate >= :since")
    suspend fun totalMedicalCostSince(farmerId: String, since: Long): Double?

    // Birds with active health issues
    @Query("SELECT DISTINCT birdId FROM medical_events WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun getBirdsWithActiveIssues(farmerId: String): List<String>
}
