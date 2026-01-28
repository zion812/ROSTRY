package com.rio.rostry.domain.monitoring

import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.data.database.entity.FarmEventType
import com.rio.rostry.data.repository.CalendarEvent
import com.rio.rostry.data.repository.EventSource
import com.rio.rostry.domain.model.MandatoryDeworming
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates deworming recommendations based on asset age and mandatory schedule.
 * Supports both one-off milestones and recurring adult schedules.
 */
@Singleton
class DewormingRecommendationEngine @Inject constructor() {

    // Note: We currently re-use VaccinationRecordEntity for Deworming records 
    // because the structure is identical (Product, Late, Note). 
    // In the future, a generic 'HealthRecordEntity' might be better, 
    // but for now we look for records with vaccineType = "DEWORMING_..." or similar logic?
    // Actually, VaccinationRecordEntity has a 'vaccineType' field. We can store "Deworming: Albendazole" there.
    // The engine needs to know how to identify deworming records.
    
    fun generateRecommendations(
        assets: List<FarmAssetEntity>,
        completedDewormingEvents: List<com.rio.rostry.data.database.entity.FarmEventEntity>
    ): List<CalendarEvent> {
        val recommendations = mutableListOf<CalendarEvent>()
        val now = System.currentTimeMillis()

        // Filter for active poultry/birds
        val allBirds = assets.filter { 
            !it.isDeleted && 
            it.status == "ACTIVE" && 
            (it.assetType == "BATCH" || it.assetType == "FLOCK" || it.assetType == "ANIMAL")
        }

        val adultBirds = mutableListOf<FarmAssetEntity>()
        
        // --- 1. Juvenile Individual Processing ---
        for (asset in allBirds) {
            val birthDate = asset.birthDate ?: continue
            val ageDays = TimeUnit.MILLISECONDS.toDays(now - birthDate).toInt()
            
            // Collect adults for separate processing
            if (ageDays > 180) { // > 6 Months
                adultBirds.add(asset)
            }

            // Get completed deworming events for this asset
            val assetDewormingHistory = completedDewormingEvents.filter { 
                it.productId == asset.assetId || 
                (it.description.contains(asset.name) || it.title.contains(asset.name))
            }.sortedByDescending { it.completedAt ?: 0L }

            for (deworming in MandatoryDeworming.values()) {
                // Skip Recurring/Adult logic here (handled in Step 2)
                if (deworming.isRecurring) continue 
                
                // Eligibility
                if (ageDays < deworming.minAgeDays) continue
                if (deworming.maxAgeDays != null && ageDays > deworming.maxAgeDays) continue

                // Check History (One-Time Milestone)
                val isDone = assetDewormingHistory.any { event ->
                    val doneAt = event.completedAt ?: event.scheduledAt
                    val doneAge = TimeUnit.MILLISECONDS.toDays(doneAt - birthDate).toInt()
                    doneAge in deworming.minAgeDays..(deworming.maxAgeDays ?: 9999)
                }

                if (!isDone) {
                    recommendations.add(createEvent(asset, deworming, birthDate, now))
                }
            }
        }
        
        // --- 2. Adult Farm-Wide Processing ---
        if (adultBirds.isNotEmpty()) {
            val adultDewormingSchedule = MandatoryDeworming.ADULT_LAYERS
            
            // Check global "Farm Wide" deworming history or any adult deworming
            // We look for events that are "Adult Deworming" type
            val lastFarmWideDeworming = completedDewormingEvents.filter { 
               it.title.contains("Adult", ignoreCase = true) || 
               it.metadata?.contains("ADULT_LAYERS") == true
            }.maxByOrNull { it.completedAt ?: 0L }?.completedAt
            
            val daysSinceLast = if (lastFarmWideDeworming != null) {
                TimeUnit.MILLISECONDS.toDays(now - lastFarmWideDeworming)
            } else {
                999L // Never done
            }
            
            if (daysSinceLast >= (adultDewormingSchedule.recurrenceDays ?: 60)) {
                // Recommendation Needed
                val names = adultBirds.take(3).joinToString(", ") { it.name }
                val count = adultBirds.size
                val description = "Unified Deworming for $count flocks/birds ($names...).\n" +
                        "Recommended every 2 months for all adults > 6 months.\n" +
                        "Meds: ${adultDewormingSchedule.medications}"
                
                val event = CalendarEvent(
                    id = "REC_DEWORM_FARM_WIDE_ADULT", // Fixed ID for single recurrence
                    title = "Farm-Wide Adult Deworming",
                    description = description,
                    date = now, // Due now
                    type = FarmEventType.DEWORMING,
                    status = EventStatus.PENDING,
                    source = EventSource.RECOMMENDATION,
                    originalEntity = null,
                    metadata = mapOf(
                        "isFarmWide" to "true",
                        "dewormingEnum" to "ADULT_LAYERS",
                        "affectedCount" to count.toString()
                    )
                )
                recommendations.add(event)
            }
        }
        
        return recommendations
    }

    private fun createEvent(
        asset: FarmAssetEntity,
        deworming: MandatoryDeworming,
        birthDate: Long,
        targetDate: Long
    ): CalendarEvent {
        // Build readable date
        val ageDays = TimeUnit.MILLISECONDS.toDays(targetDate - birthDate).toInt()
        
        return CalendarEvent(
            id = "REC_DEWORM_${asset.assetId}_${deworming.name}",
            title = "Deworming Due: ${deworming.displayName}",
            description = "Recommended for ${asset.name} (Age: ~$ageDays days).\n" +
                          "Meds: ${deworming.medications}\n" +
                          "Method: ${deworming.method}\n" +
                          "Note: ${deworming.notes}",
            date = targetDate,
            type = FarmEventType.DEWORMING,
            status = EventStatus.PENDING,
            source = EventSource.RECOMMENDATION,
            originalEntity = null,
            metadata = mapOf(
                "assetId" to asset.assetId,
                "dewormingEnum" to deworming.name,
                "isHealth" to "true"
            )
        )
    }
}
