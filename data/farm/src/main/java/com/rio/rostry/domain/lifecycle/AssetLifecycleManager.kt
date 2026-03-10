package com.rio.rostry.domain.lifecycle

import com.rio.rostry.core.model.AssetLifecycleEvent
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.farm.repository.FarmAssetRepository
import com.rio.rostry.domain.farm.repository.AssetLifecycleRepository
import java.util.UUID
import javax.inject.Inject

class AssetLifecycleManager @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val lifecycleRepository: AssetLifecycleRepository
) {
    suspend fun recordStageChange(assetId: String, farmerId: String, fromStage: String?, toStage: String, notes: String? = null) {
        val event = AssetLifecycleEvent(
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
            mediaItemsJson = null
        )
        lifecycleRepository.recordEvent(event)
    }

    suspend fun getLifecycleHistory(assetId: String): Result<List<AssetLifecycleEvent>> {
        return lifecycleRepository.getEventsForAsset(assetId)
    }
}
