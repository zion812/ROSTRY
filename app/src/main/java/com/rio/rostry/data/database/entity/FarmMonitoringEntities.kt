package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    indices = [Index("productId"), Index("week")]
)
data class GrowthRecordEntity(
    @PrimaryKey val recordId: String,
    val productId: String,
    val week: Int,
    val weightGrams: Double?,
    val heightCm: Double?,
    val photoUrl: String? = null,
    val healthStatus: String? = null, // OK, WATCH, SICK
    val milestone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

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
    indices = [Index("productId"), Index("status")]
)
data class QuarantineRecordEntity(
    @PrimaryKey val quarantineId: String,
    val productId: String,
    val reason: String,
    val protocol: String? = null,
    val medicationScheduleJson: String? = null,
    val vetNotes: String? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val status: String = "ACTIVE" // ACTIVE, RECOVERED, TRANSFERRED
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
    indices = [Index("productId"), Index("causeCategory")]
)
data class MortalityRecordEntity(
    @PrimaryKey val deathId: String,
    val productId: String?,
    val causeCategory: String, // ILLNESS, PREDATOR, ACCIDENT, OTHER
    val circumstances: String? = null,
    val ageWeeks: Int? = null,
    val disposalMethod: String? = null,
    val financialImpactInr: Double? = null,
    val occurredAt: Long = System.currentTimeMillis()
)

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
    indices = [Index("productId"), Index("vaccineType"), Index("scheduledAt")]
)
data class VaccinationRecordEntity(
    @PrimaryKey val vaccinationId: String,
    val productId: String,
    val vaccineType: String,
    val supplier: String? = null,
    val batchCode: String? = null,
    val doseMl: Double? = null,
    val scheduledAt: Long, // reminder scheduling anchor
    val administeredAt: Long? = null,
    val efficacyNotes: String? = null,
    val costInr: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// Hatching batches and logs
@Entity(
    tableName = "hatching_batches",
    indices = [Index("name")]
)
data class HatchingBatchEntity(
    @PrimaryKey val batchId: String,
    val name: String,
    val startedAt: Long = System.currentTimeMillis(),
    val expectedHatchAt: Long? = null,
    val temperatureC: Double? = null,
    val humidityPct: Double? = null,
    val notes: String? = null
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
    indices = [Index("batchId"), Index("productId")]
)
data class HatchingLogEntity(
    @PrimaryKey val logId: String,
    val batchId: String,
    val productId: String?, // chick record, if available
    val eventType: String, // EGG_COLLECTED, SET, CANDLED, HATCHED
    val qualityScore: Int? = null,
    val temperatureC: Double? = null,
    val humidityPct: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
