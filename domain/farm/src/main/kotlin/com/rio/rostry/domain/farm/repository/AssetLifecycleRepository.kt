package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.AssetLifecycleEvent
import com.rio.rostry.core.common.Result

/**
 * Repository contract for asset lifecycle event tracking.
 * 
 * Tracks important lifecycle events for farm assets including state changes,
 * ownership transfers, health events, and other significant milestones.
 */
interface AssetLifecycleRepository {
    
    /**
     * Record a new lifecycle event for an asset.
     */
    suspend fun recordEvent(event: AssetLifecycleEvent): Result<Unit>
    
    /**
     * Get all lifecycle events for a specific asset.
     */
    suspend fun getEventsForAsset(assetId: String): Result<List<AssetLifecycleEvent>>
    
    /**
     * Get a specific lifecycle event by ID.
     */
    suspend fun getEventById(eventId: String): Result<AssetLifecycleEvent?>
    
    /**
     * Sync pending offline events to the server.
     */
    suspend fun syncPendingEvents(): Result<Unit>
}
