package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Asset Lifecycle Documentation model containing all aggregated data.
 */
data class AssetDocumentation(
    val asset: FarmAssetEntity,
    val activities: List<FarmActivityLogEntity>,
    val vaccinations: List<VaccinationRecordEntity>,
    val growthRecords: List<GrowthRecordEntity>,
    val mortalityRecords: List<MortalityRecordEntity>,
    val dailyLogs: List<DailyBirdLogEntity>,
    val lifecycleEvents: List<LifecycleEventEntity>,
    // Computed summaries
    val totalFeedKg: Double,
    val totalExpensesInr: Double,
    val totalMortality: Int,
    val currentWeight: Double?,
    val vaccinationCount: Int,
    val daysActive: Int
)

/**
 * Timeline entry for lifecycle visualization.
 */
data class LifecycleTimelineEntry(
    val date: Long,
    val title: String,
    val description: String,
    val type: TimelineEventType,
    val mediaUrl: String? = null
)

enum class TimelineEventType {
    BIRTH, VACCINATION, GROWTH_UPDATE, MORTALITY, STAGE_CHANGE, SALE, HEALTH_EVENT, FEED, OTHER
}

/**
 * Service to aggregate all asset-related data for documentation and export.
 */
@Singleton
class AssetDocumentationService @Inject constructor(
    private val farmAssetDao: FarmAssetDao,
    private val farmActivityLogDao: FarmActivityLogDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val growthRecordDao: GrowthRecordDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val dailyBirdLogDao: DailyBirdLogDao,
    private val lifecycleEventDao: LifecycleEventDao
) {

    /**
     * Load complete documentation for a given asset.
     */
    suspend fun loadDocumentation(assetId: String): AssetDocumentation? {
        val asset = farmAssetDao.findById(assetId) ?: return null
        
        // Load all related records
        val activities = farmActivityLogDao.observeForProduct(assetId).first()
        val vaccinations = vaccinationRecordDao.observeForProduct(assetId).first()
        val growthRecords = growthRecordDao.observeForProduct(assetId).first()
        val mortalityRecords = mortalityRecordDao.getByProduct(assetId)
        val dailyLogs = dailyBirdLogDao.getLogsForBird(assetId).first()
        val lifecycleEvents = lifecycleEventDao.observeForProduct(assetId).first()
        
        // Compute summaries
        val totalFeedKg = activities
            .filter { it.activityType == "FEED" }
            .sumOf { it.quantity ?: 0.0 }
        
        val totalExpensesInr = activities
            .filter { it.amountInr != null && it.amountInr > 0 }
            .sumOf { it.amountInr ?: 0.0 }
        
        val totalMortality = mortalityRecords.sumOf { it.quantity.toInt() }
        
        val currentWeight = growthRecords
            .maxByOrNull { it.createdAt }
            ?.weightGrams
        
        val daysActive = asset.birthDate?.let { birth ->
            val diffMs = System.currentTimeMillis() - birth
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } ?: 0
        
        return AssetDocumentation(
            asset = asset,
            activities = activities,
            vaccinations = vaccinations,
            growthRecords = growthRecords,
            mortalityRecords = mortalityRecords,
            dailyLogs = dailyLogs,
            lifecycleEvents = lifecycleEvents,
            totalFeedKg = totalFeedKg,
            totalExpensesInr = totalExpensesInr,
            totalMortality = totalMortality,
            currentWeight = currentWeight,
            vaccinationCount = vaccinations.size,
            daysActive = daysActive
        )
    }

    /**
     * Generate a chronological timeline of all lifecycle events.
     */
    suspend fun generateTimeline(assetId: String): List<LifecycleTimelineEntry> {
        val doc = loadDocumentation(assetId) ?: return emptyList()
        val timeline = mutableListOf<LifecycleTimelineEntry>()
        
        // Birth event
        doc.asset.birthDate?.let { birthDate ->
            timeline.add(
                LifecycleTimelineEntry(
                    date = birthDate,
                    title = "Birth / Acquisition",
                    description = "Asset added: ${doc.asset.name}",
                    type = TimelineEventType.BIRTH
                )
            )
        }
        
        // Vaccinations
        doc.vaccinations.forEach { vax ->
            timeline.add(
                LifecycleTimelineEntry(
                    date = vax.administeredAt ?: vax.scheduledAt,
                    title = "Vaccination: ${vax.vaccineType}",
                    description = vax.efficacyNotes ?: "Administered",
                    type = TimelineEventType.VACCINATION
                )
            )
        }
        
        // Growth records
        doc.growthRecords.forEach { growth ->
            timeline.add(
                LifecycleTimelineEntry(
                    date = growth.createdAt,
                    title = "Growth Update",
                    description = "Weight: ${growth.weightGrams}g",
                    type = TimelineEventType.GROWTH_UPDATE
                )
            )
        }
        
        // Mortality events
        doc.mortalityRecords.forEach { mortality ->
            timeline.add(
                LifecycleTimelineEntry(
                    date = mortality.occurredAt,
                    title = "Mortality",
                    description = "${mortality.quantity} - ${mortality.causeCategory}",
                    type = TimelineEventType.MORTALITY
                )
            )
        }
        
        // Lifecycle events (stage transitions)
        doc.lifecycleEvents.forEach { event ->
            timeline.add(
                LifecycleTimelineEntry(
                    date = event.timestamp,
                    title = event.type.replace("_", " "),
                    description = event.notes ?: "",
                    type = TimelineEventType.STAGE_CHANGE
                )
            )
        }
        
        // Sort by date
        return timeline.sortedBy { it.date }
    }

    /**
     * Gather all media items associated with this asset from activities and logs.
     */
    suspend fun getAssetMedia(assetId: String): List<com.rio.rostry.ui.components.MediaItem> {
        val doc = loadDocumentation(assetId) ?: return emptyList()
        val mediaList = mutableListOf<com.rio.rostry.ui.components.MediaItem>()
        
        // 1. From Activities
        doc.activities.forEach { activity ->
            mediaList.addAll(activity.getMediaItems())
        }
        
        // 2. From Lifecycle Events (if they have imageUrl)
        // Note: LifecycleEventEntity does not have direct image url. 
        // It's mainly a metadata record. If we need images, we might need to fetch related timeline events or logs.
        // For now, we'll skip LifecycleEventEntity images as they don't exist directly.
        
        return mediaList
    }

    companion object {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        
        fun formatDate(timestamp: Long): String {
            return dateFormat.format(Date(timestamp))
        }
    }
}
