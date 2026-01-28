package com.rio.rostry.domain.monitoring

import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.data.database.entity.FarmEventType
import com.rio.rostry.data.repository.CalendarEvent
import com.rio.rostry.data.repository.EventSource
import com.rio.rostry.domain.model.MandatoryVaccine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates vaccination recommendations based on asset age and mandatory schedule.
 */
@Singleton
class VaccinationRecommendationEngine @Inject constructor() {

    fun generateRecommendations(
        assets: List<FarmAssetEntity>,
        existingRecords: List<VaccinationRecordEntity>
    ): List<CalendarEvent> {
        val recommendations = mutableListOf<CalendarEvent>()
        val now = System.currentTimeMillis()

        // Filter for active poultry/birds
        val birdAssets = assets.filter { 
            !it.isDeleted && 
            it.status == "ACTIVE" && 
            (it.assetType == "BATCH" || it.assetType == "FLOCK" || it.assetType == "ANIMAL") 
            // Add category check if needed, e.g. it.category.contains("Chicken")
        }

        for (asset in birdAssets) {
            val birthDate = asset.birthDate ?: continue // Skip if no birth date
            
            // Calculate age in days
            val ageDays = TimeUnit.MILLISECONDS.toDays(now - birthDate).toInt()
            
            for (vaccine in MandatoryVaccine.values()) {
                // Calculate due date window
                val dueStart = birthDate + TimeUnit.DAYS.toMillis(vaccine.minAgeDays.toLong())
                val dueEnd = birthDate + TimeUnit.DAYS.toMillis(vaccine.maxAgeDays.toLong())
                
                // Check if this specific asset + vaccine combo already exists in records
                // We check if any record matches the assetId AND specific vaccine type
                // Note: existingRecords should ideally be filtered for this asset beforehand for performance, 
                // but for now we filter here.
                val isDone = existingRecords.any { record ->
                    record.productId == asset.assetId && 
                    (record.vaccineType.contains(vaccine.displayName, ignoreCase = true) || 
                     record.vaccineType.contains(vaccine.name, ignoreCase = true))
                     // A more robust matching might be needed if users type manually
                }

                if (!isDone) {
                    val isOverdue = now > dueEnd
                    
                    // Create Recommendation Event
                    // We use the start of the window as the calendar date
                    val event = CalendarEvent(
                        id = "REC_${asset.assetId}_${vaccine.name}", // Virtual ID
                        title = "Vaccine Due: ${vaccine.displayName}",
                        description = "Recommended for ${asset.name} (Age: ~${ageDays} days).\n" +
                                      "Method: ${vaccine.method}\n" +
                                      "Benefit: ${vaccine.benefit}\n" +
                                      "Notes: ${vaccine.notes}",
                        date = dueStart,
                        type = FarmEventType.VACCINATION,
                        status = EventStatus.PENDING, // Overdue status is calculated dynamically by date
                        source = EventSource.RECOMMENDATION,
                        originalEntity = null, // Virtual
                        metadata = mapOf(
                            "assetId" to asset.assetId,
                            "vaccineEnum" to vaccine.name,
                            "isMandatory" to "true"
                        )
                    )
                    recommendations.add(event)
                }
            }
        }
        
        return recommendations
    }
}
