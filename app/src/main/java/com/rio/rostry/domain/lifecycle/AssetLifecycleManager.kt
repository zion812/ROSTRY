package com.rio.rostry.domain.lifecycle

import com.rio.rostry.data.database.entity.AssetLifecycleEventEntity
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.domain.repository.AssetLifecycleRepository
import java.util.UUID
import javax.inject.Inject

class AssetLifecycleManager @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val lifecycleRepository: AssetLifecycleRepository
) {
    suspend fun recordStageChange(assetId: String, farmerId: String, fromStage: String?, toStage: String, notes: String? = null) {
        val event = AssetLifecycleEventEntity(
            eventId = UUID.randomUUID().toString(),
            assetId = assetId,
            farmerId = farmerId,
            eventType = "STAGE_CHANGE",
            fromStage = fromStage,
            toStage = toStage,
            eventData = "{}",
            triggeredBy = "USER",
            occurredAt = System.currentTimeMillis(),
            recordedAt = System.currentTimeMillis(),
            recordedBy = farmerId,
            notes = notes,
            mediaItemsJson = null,
            dirty = true,
            syncedAt = null
        )
        lifecycleRepository.recordEvent(event)
        
        // Find existing asset and attempt to update its stage if possible. 
        // This is a simplified interaction demonstrating the manager layer.
    }

    suspend fun getLifecycleHistory(assetId: String): List<AssetLifecycleEventEntity> {
        return lifecycleRepository.getEventsForAsset(assetId)
    }
}
