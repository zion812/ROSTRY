package com.rio.rostry.ui.farmer.calendar

import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.data.database.entity.FarmEventType
import com.rio.rostry.data.repository.CalendarEvent
import com.rio.rostry.data.repository.EventSource
import com.rio.rostry.domain.service.BatchPerformance
import com.rio.rostry.domain.service.GrowthPrediction
import com.rio.rostry.domain.service.GrowthStatus
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Maps Growth Predictions -> Calendar Events
 * Generates "Projected" events for the calendar based on growth trajectory.
 */
class ProjectedEventMapper @Inject constructor() {

    fun mapPredictionToEvents(
        batchId: String,
        batchName: String,
        prediction: GrowthPrediction
    ): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val now = Instant.now()

        // 1. Optimal Weight Projection
        if (prediction.weeksToOptimalWeight > 0) {
            val projectedDate = now.plus(prediction.weeksToOptimalWeight.toLong(), ChronoUnit.WEEKS)
            
            events.add(
                CalendarEvent(
                    id = "proj_opt_$batchId",
                    title = "Optimal Harvest: $batchName",
                    description = "Projected to reach ${prediction.optimalWeight}g based on current growth.",
                    date = projectedDate.toEpochMilli(),
                    type = FarmEventType.WEIGHING, // Taking weighing/harvest as closest type
                    status = EventStatus.PENDING,
                    source = EventSource.RECOMMENDATION,
                    metadata = mapOf(
                        "isProjected" to "true",
                        "batchId" to batchId,
                        "projectionType" to "OPTIMAL_WEIGHT"
                    )
                )
            )
        }

        // 2. Anomaly Warnings as immediate events
        prediction.anomalies.forEachIndexed { index, anomaly ->
            // Anomalies typically refer to past weeks, but we show them as "Needs Review" today
            events.add(
                CalendarEvent(
                    id = "anomaly_${batchId}_${index}",
                    title = "Growth Alert: $batchName",
                    description = "${anomaly.message} (${anomaly.severity})",
                    date = now.toEpochMilli(),
                    type = FarmEventType.BIOSECURITY, // Needs attention
                    status = EventStatus.PENDING,
                    source = EventSource.RECOMMENDATION,
                    metadata = mapOf(
                        "isProjected" to "true", // It's an insight, not a user created event
                        "batchId" to batchId,
                        "severity" to anomaly.severity.name
                    )
                )
            )
        }
        
        // 3. Status Check-ins
        if (prediction.status == GrowthStatus.BELOW_AVERAGE || prediction.status == GrowthStatus.NEEDS_ATTENTION) {
            // Suggest a health check 3 days from now
            events.add(
                CalendarEvent(
                    id = "health_check_$batchId",
                    title = "Health Check: $batchName",
                    description = "Growth is ${prediction.status}. Recommended to inspect flock.",
                    date = now.plus(3, ChronoUnit.DAYS).toEpochMilli(),
                    type = FarmEventType.BIOSECURITY,
                    status = EventStatus.PENDING,
                    source = EventSource.RECOMMENDATION,
                    metadata = mapOf(
                        "isProjected" to "true",
                        "batchId" to batchId
                    )
                )
            )
        }

        return events
    }
}
