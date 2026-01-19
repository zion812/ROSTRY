package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.data.database.entity.FarmEventEntity
import com.rio.rostry.data.database.entity.FarmEventType
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmEventDao {
    @Query("SELECT * FROM farm_events WHERE farmerId = :farmerId ORDER BY scheduledAt ASC")
    fun getEventsByFarmer(farmerId: String): Flow<List<FarmEventEntity>>

    @Query("SELECT * FROM farm_events WHERE farmerId = :farmerId AND scheduledAt BETWEEN :startDate AND :endDate ORDER BY scheduledAt ASC")
    fun getEventsByDateRange(farmerId: String, startDate: Long, endDate: Long): Flow<List<FarmEventEntity>>

    @Query("SELECT * FROM farm_events WHERE farmerId = :farmerId AND status = 'PENDING' AND scheduledAt >= :currentTime ORDER BY scheduledAt ASC LIMIT :limit")
    fun getUpcomingEvents(farmerId: String, currentTime: Long, limit: Int = 10): Flow<List<FarmEventEntity>>

    @Query("SELECT * FROM farm_events WHERE farmerId = :farmerId AND eventType = :type ORDER BY scheduledAt ASC")
    fun getEventsByType(farmerId: String, type: FarmEventType): Flow<List<FarmEventEntity>>

    @Upsert
    suspend fun upsert(event: FarmEventEntity)

    @Query("UPDATE farm_events SET status = 'COMPLETED', completedAt = :completedAt WHERE eventId = :eventId")
    suspend fun markCompleted(eventId: String, completedAt: Long)

    @Query("DELETE FROM farm_events WHERE eventId = :eventId")
    suspend fun deleteEvent(eventId: String)
    
    @Query("SELECT * FROM farm_events WHERE farmerId = :farmerId AND status = 'PENDING' AND scheduledAt BETWEEN :startTime AND :endTime")
    suspend fun getPendingEventsInRange(farmerId: String, startTime: Long, endTime: Long): List<FarmEventEntity>
}
