package com.rio.rostry.domain.calendar

import com.rio.rostry.data.database.dao.FarmEventDao
import com.rio.rostry.data.database.entity.FarmEventEntity
import javax.inject.Inject

import com.rio.rostry.data.database.entity.FarmEventType
import kotlinx.coroutines.flow.firstOrNull

class CalendarIntegrationService @Inject constructor(
    private val eventDao: FarmEventDao
) {
    suspend fun getEventsForDateRange(farmerId: String, start: Long, end: Long): List<FarmEventEntity> {
        return eventDao.getEventsByDateRange(farmerId, start, end).firstOrNull() ?: emptyList()
    }
    
    suspend fun createIntegrationEvent(
        farmerId: String, 
        title: String, 
        description: String, 
        date: Long,
        externalId: String? = null
    ) {
        val event = FarmEventEntity(
            eventId = java.util.UUID.randomUUID().toString(),
            farmerId = farmerId,
            title = title,
            description = description,
            scheduledAt = date,
            eventType = FarmEventType.OTHER,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        eventDao.upsert(event)
    }
}
