package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.ui.components.MediaItem

// Growth tracking: weekly records with optional photo and health status
@Entity(
    tableName = "growth_records",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("week"), Index("farmerId"), Index("createdAt")]
)
data class GrowthRecordEntity(
    @PrimaryKey val recordId: String,
    val productId: String,
    val farmerId: String,
    val week: Int,
    val weightGrams: Double?,
    val heightCm: Double?,
    val photoUrl: String? = null,         // Legacy: single photo URL
    val mediaItemsJson: String? = null,   // Structured: JSON array of MediaItem
    val healthStatus: String? = null, // OK, WATCH, SICK
    val milestone: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null,
    // Data Integrity & Audit
    val correctionOf: String? = null,     // Points to original record if this is a correction
    val editCount: Int = 0,               // Number of edits to this record
    val lastEditedBy: String? = null,     // User who made last edit
    val isBatchLevel: Boolean = false,    // True if copied from pre-split batch
    val sourceBatchId: String? = null     // Original batch ID if copied on split
) {
    @Ignore
    fun getMediaItems(): List<MediaItem> {
        if (!mediaItemsJson.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<MediaItem>>() {}.type
                val items: List<MediaItem>? = Gson().fromJson(mediaItemsJson, type)
                if (!items.isNullOrEmpty()) return items
            } catch (_: Exception) { }
        }
        return photoUrl?.takeIf { it.isNotBlank() }?.let {
            listOf(MediaItem(url = it, caption = "Week $week", recordType = "GROWTH", recordId = recordId))
        } ?: emptyList()
    }
}

// Quarantine record with treatment protocol and vet visits
@Entity(
    tableName = "quarantine_records",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("status"), Index("farmerId"), Index("startedAt")]
)
data class QuarantineRecordEntity(
    @PrimaryKey val quarantineId: String,
    val productId: String,
    val farmerId: String,
    val reason: String,
    val protocol: String? = null,
    val medicationScheduleJson: String? = null,
    val statusHistoryJson: String? = null,
    val vetNotes: String? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val updatesCount: Int = 0,
    val endedAt: Long? = null,
    val status: String = "ACTIVE", // ACTIVE, RECOVERED, TRANSFERRED
    val healthScore: Int = 100,
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)

// Mortality record with cause categorization and cost impact
@Entity(
    tableName = "mortality_records",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("productId"), Index("causeCategory"), Index("farmerId"), Index("occurredAt")]
)
data class MortalityRecordEntity(
    @PrimaryKey val deathId: String,
    val productId: String?,
    val farmerId: String,
    val causeCategory: String, // ILLNESS, PREDATOR, ACCIDENT, OTHER
    val circumstances: String? = null,
    val ageWeeks: Int? = null,
    val disposalMethod: String? = null,
    val quantity: Int = 1,
    val financialImpactInr: Double? = null,
    val photoUrls: String? = null,        // Legacy: comma-separated URLs
    val mediaItemsJson: String? = null,   // Structured: JSON array of MediaItem
    val occurredAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null,
    // Data Integrity: Batch tracking for split scenarios
    val affectedProductIds: String? = null,   // JSON array of affected individual IDs after split
    val affectsAllChildren: Boolean = false   // True if applies to all from sourceBatchId
) {
    @Ignore
    fun getMediaItems(): List<MediaItem> {
        if (!mediaItemsJson.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<MediaItem>>() {}.type
                val items: List<MediaItem>? = Gson().fromJson(mediaItemsJson, type)
                if (!items.isNullOrEmpty()) return items
            } catch (_: Exception) { }
        }
        return photoUrls?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.mapIndexed { i, url ->
            MediaItem(url = url, caption = "Photo ${i+1}", recordType = "MORTALITY", recordId = deathId)
        } ?: emptyList()
    }
}

// Vaccination record with batch tracking and efficacy notes
@Entity(
    tableName = "vaccination_records",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("vaccineType"), Index("scheduledAt"), Index("farmerId")]
)
data class VaccinationRecordEntity(
    @PrimaryKey val vaccinationId: String,
    val productId: String,
    val farmerId: String,
    val vaccineType: String,
    val supplier: String? = null,
    val batchCode: String? = null,
    val doseMl: Double? = null,
    val scheduledAt: Long, // reminder scheduling anchor
    val administeredAt: Long? = null,
    val efficacyNotes: String? = null,
    val costInr: Double? = null,
    val photoUrls: String? = null,        // Legacy: comma-separated URLs
    val mediaItemsJson: String? = null,   // Structured: JSON array of MediaItem
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null,
    // Data Integrity & Audit
    val correctionOf: String? = null,     // Points to original if correction
    val editCount: Int = 0,               // Number of edits
    val lastEditedBy: String? = null      // User who made last edit
) {
    @Ignore
    fun getMediaItems(): List<MediaItem> {
        if (!mediaItemsJson.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<MediaItem>>() {}.type
                val items: List<MediaItem>? = Gson().fromJson(mediaItemsJson, type)
                if (!items.isNullOrEmpty()) return items
            } catch (_: Exception) { }
        }
        return photoUrls?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.mapIndexed { i, url ->
            MediaItem(url = url, caption = "Photo ${i+1}", recordType = "VACCINATION", recordId = vaccinationId)
        } ?: emptyList()
    }
}

// Hatching batches and logs
@Entity(
    tableName = "hatching_batches",
    indices = [Index("name"), Index("farmerId"), Index("expectedHatchAt")]
)
data class HatchingBatchEntity(
    @PrimaryKey val batchId: String,
    val name: String,
    val farmerId: String,
    val startedAt: Long = System.currentTimeMillis(),
    val expectedHatchAt: Long? = null,
    val temperatureC: Double? = null,
    val humidityPct: Double? = null,
    val eggsCount: Int? = null,
    val sourceCollectionId: String? = null,
    val notes: String? = null,
    val status: String = "ACTIVE",
    val hatchedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)

@Entity(
    tableName = "hatching_logs",
    foreignKeys = [
        ForeignKey(
            entity = HatchingBatchEntity::class,
            parentColumns = ["batchId"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("batchId"), Index("productId"), Index("farmerId"), Index("createdAt")]
)
data class HatchingLogEntity(
    @PrimaryKey val logId: String,
    val batchId: String,
    val farmerId: String,
    val productId: String?, // chick record, if available
    val eventType: String, // EGG_COLLECTED, SET, CANDLED, HATCHED
    val qualityScore: Int? = null,
    val temperatureC: Double? = null,
    val humidityPct: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = false,
    val syncedAt: Long? = null
)