package com.rio.rostry.domain.monitoring.health

import com.rio.rostry.core.model.TaskRecurrence
import com.rio.rostry.domain.monitoring.repository.TaskSchedulingRepository
import javax.inject.Inject

/**
 * Generates standard vaccination schedules for newly bred or acquired assets.
 * Moved from app/domain/health/ to domain:monitoring as part of Phase 2 architecture cleanup.
 * 
 * Now depends on TaskSchedulingRepository from domain:monitoring instead of app-module interface.
 */
class VaccinationProtocolEngine @Inject constructor(
    private val taskSchedulingRepository: TaskSchedulingRepository
) {
    /**
     * Generates a standard vaccination schedule for a newly bred or acquired asset based on standard protocol.
     */
    suspend fun generateProtocolForAsset(assetId: String, farmerId: String, hatchDate: Long) {
        val protocolDays = listOf(7, 14, 28)
        val protocolNames = listOf("ND Vaccine", "IBD Vaccine", "ND Booster")
        
        for (i in protocolDays.indices) {
            val dueTime = hatchDate + (protocolDays[i] * 24L * 60L * 60L * 1000L)
            
            val taskRecurrence = TaskRecurrence(
                recurrenceId = java.util.UUID.randomUUID().toString(),
                taskId = "VACC-$assetId-${protocolDays[i]}",
                pattern = "ONE_TIME",
                interval = 0,
                daysOfWeek = null,
                endDate = dueTime,
                maxOccurrences = 1,
                currentOccurrence = 0,
                lastGenerated = null,
                nextDue = dueTime,
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            taskSchedulingRepository.createRecurrence(taskRecurrence)
        }
    }
}

