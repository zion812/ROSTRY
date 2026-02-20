package com.rio.rostry.domain.health

import com.rio.rostry.data.database.entity.AssetHealthRecordEntity
import com.rio.rostry.data.database.dao.AssetHealthRecordDao
import javax.inject.Inject

class AssetHealthManager @Inject constructor(
    private val healthRecordDao: AssetHealthRecordDao
) {
    suspend fun recordHealthEvent(
        assetId: String,
        farmerId: String,
        recordType: String,
        recordData: String,
        healthScore: Int,
        veterinarianId: String? = null,
        veterinarianNotes: String? = null,
        followUpRequired: Boolean = false,
        followUpDate: Long? = null,
        costInr: Double? = null,
        mediaItemsJson: String? = null
    ) {
        val record = AssetHealthRecordEntity(
            recordId = java.util.UUID.randomUUID().toString(),
            assetId = assetId,
            farmerId = farmerId,
            recordType = recordType,
            recordData = recordData,
            healthScore = healthScore,
            veterinarianId = veterinarianId,
            veterinarianNotes = veterinarianNotes,
            followUpRequired = followUpRequired,
            followUpDate = followUpDate,
            costInr = costInr,
            mediaItemsJson = mediaItemsJson,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            dirty = true,
            syncedAt = null
        )
        healthRecordDao.insert(record)
    }

    suspend fun getHealthHistory(assetId: String): List<AssetHealthRecordEntity> {
        return healthRecordDao.getRecordsForAsset(assetId)
    }
}
