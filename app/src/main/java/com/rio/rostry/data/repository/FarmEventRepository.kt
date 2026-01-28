package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FarmEventDao
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.data.database.entity.FarmEventEntity
import com.rio.rostry.data.database.entity.FarmEventType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmEventRepository @Inject constructor(
    private val farmEventDao: FarmEventDao,
    private val taskDao: TaskDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val farmAssetDao: com.rio.rostry.data.database.dao.FarmAssetDao,
    private val vaccinationRecommendationEngine: com.rio.rostry.domain.monitoring.VaccinationRecommendationEngine,
    private val dewormingRecommendationEngine: com.rio.rostry.domain.monitoring.DewormingRecommendationEngine
) {

    fun getCalendarEvents(farmerId: String): Flow<List<CalendarEvent>> {
        val farmEventsFlow = farmEventDao.getEventsByFarmer(farmerId)
        // For simplicity in this iteration, we iterate all and map. 
        // In a real optimized scenario we might want to filter by date range if provided.
        // But the requirement says "getCalendarEvents" which aggregates.
        
        // We probably want to map these entities to a domain model 'CalendarEvent' 
        // that normalizes the different sources. since I haven't defined a domain model yet
        // I'll define a simple data class here or just use a wrapper. 
        // The plan didn't strictly ask for a domain layer model, 
        // but "Aggregate events from multiple sources" implies a unified model.
        
        // Let's use the UI model approach or a domain model. 
        // Since I'm in data/repository, I should return a Domain Model or a unified Data Model.
        // I will create a simple CalendarEvent model inside this file or strictly utilize FarmEventEntity 
        // by converting other types to it? converting others to FarmEventEntity might be confusing 
        // if IDs collide or if we try to save them back. 
        // Better to return a sealed class or a unified UI model.
        
        // I will define a unified model `CalendarEvent` here for now.
        
        return combine(
        
            farmEventsFlow,
            taskDao.observeByFarmer(farmerId),
            vaccinationRecordDao.observeByFarmer(farmerId),
            farmAssetDao.getAllAssets(farmerId)
        ) { farmEvents: List<FarmEventEntity>, tasks: List<com.rio.rostry.data.database.entity.TaskEntity>, vaccinations: List<com.rio.rostry.data.database.entity.VaccinationRecordEntity>, assets: List<com.rio.rostry.data.database.entity.FarmAssetEntity> -> 
            // Explicitly map each list to avoid type inference issues
            val events: List<CalendarEvent> = farmEvents.map { it.toCalendarEvent() }
            
            val taskEvents = tasks.map { task ->
                CalendarEvent(
                    id = task.taskId,
                    title = task.title,
                    description = task.description ?: "",
                    date = task.dueAt,
                    type = mapTaskTypeToEventType(task.taskType),
                    status = if (task.completedAt != null) EventStatus.COMPLETED else EventStatus.PENDING,
                    source = EventSource.TASK
                )
            }
            
            val vaccinationEvents = vaccinations.map { record ->
                CalendarEvent(
                    id = record.vaccinationId,
                    title = "Vaccination: ${record.vaccineType}",
                    description = "Dose: ${record.doseMl ?: 0.0}ml${if (!record.batchCode.isNullOrBlank()) " (Batch: ${record.batchCode})" else ""}",
                    date = record.scheduledAt,
                    type = FarmEventType.VACCINATION,
                    status = if (record.administeredAt != null) EventStatus.COMPLETED else EventStatus.PENDING,
                    source = EventSource.VACCINATION_RECORD
                )
            }
            
            val recommendations: List<CalendarEvent> = vaccinationRecommendationEngine.generateRecommendations(assets, vaccinations)
            
            // Filter Completed Deworming Events for History Check
            val completedDeworming = farmEvents.filter { 
                it.eventType == FarmEventType.DEWORMING && it.status == EventStatus.COMPLETED 
            }
            val dewormingRecommendations: List<CalendarEvent> = dewormingRecommendationEngine.generateRecommendations(assets, completedDeworming)
            
            val allEvents = events + taskEvents + vaccinationEvents + recommendations + dewormingRecommendations
            allEvents.sortedBy { it.date }
        }
    }

    suspend fun createEvent(event: FarmEventEntity) {
        farmEventDao.upsert(event)
    }

    suspend fun updateEvent(event: FarmEventEntity) {
        farmEventDao.upsert(event)
    }

    suspend fun deleteEvent(eventId: String) {
        farmEventDao.deleteEvent(eventId)
    }
    
    suspend fun getUpcomingReminders(farmerId: String): List<FarmEventEntity> {
        val now = System.currentTimeMillis()
        // Simple logic: events in next 24h that are pending
        val end = now + 24 * 60 * 60 * 1000
        return farmEventDao.getPendingEventsInRange(farmerId, now, end)
    }

    private fun FarmEventEntity.toCalendarEvent() = CalendarEvent(
        id = eventId,
        title = title,
        description = description,
        date = scheduledAt,
        type = eventType,
        status = status,
        source = EventSource.FARM_EVENT,
        originalEntity = this
    )

    private fun mapTaskTypeToEventType(taskType: String): FarmEventType {
        return when (taskType) {
            "VACCINATION" -> FarmEventType.VACCINATION
            "FEEDING" -> FarmEventType.FEEDING
            "CLEANING" -> FarmEventType.CLEANING
            else -> FarmEventType.OTHER
        }
    }
}

data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String,
    val date: Long,
    val type: FarmEventType,
    val status: EventStatus,
    val source: EventSource,
    val originalEntity: FarmEventEntity? = null,
    val metadata: Map<String, String>? = null
)

enum class EventSource {
    FARM_EVENT, TASK, VACCINATION_RECORD, RECOMMENDATION
}
