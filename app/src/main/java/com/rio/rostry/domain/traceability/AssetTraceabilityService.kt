package com.rio.rostry.domain.traceability

import com.rio.rostry.data.database.entity.AssetLifecycleEventEntity
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.domain.repository.AssetLifecycleRepository
import com.rio.rostry.domain.repository.AssetBatchOperationRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AssetTraceabilityService @Inject constructor(
    private val assetRepository: FarmAssetRepository,
    private val lifecycleRepository: AssetLifecycleRepository,
    private val batchRepository: AssetBatchOperationRepository
) {
    /**
     * Reconstructs the entire history of an asset across all systems.
     */
    suspend fun generateTraceabilityReport(assetId: String): TraceabilityReport {
        val assetResult = assetRepository.getAssetById(assetId).firstOrNull()
        val asset = assetResult?.data
        val events = lifecycleRepository.getEventsForAsset(assetId)
        // Extract batches the asset may have belonged to
        
        return TraceabilityReport(
            assetId = assetId,
            currentStatus = asset?.status ?: "UNKNOWN",
            events = events
        )
    }
    
    data class TraceabilityReport(
        val assetId: String,
        val currentStatus: String,
        val events: List<AssetLifecycleEventEntity>
    )
}
