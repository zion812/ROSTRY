package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.FarmAlert
import kotlinx.coroutines.flow.Flow

/**
 * Repository for farm-related alerts and status updates.
 */
interface FarmAlertRepository {
    /**
     * Observe unread alerts for a farmer.
     */
    fun observeUnread(farmerId: String): Flow<List<FarmAlert>>
    
    /**
     * Count unread alerts for a farmer.
     */
    suspend fun countUnread(farmerId: String): Int
    
    /**
     * Insert a farm alert.
     */
    suspend fun insert(alert: FarmAlert)
    
    /**
     * Mark an alert as read.
     */
    suspend fun markRead(alertId: String)
    
    /**
     * Delete expired alerts.
     */
    suspend fun cleanupExpired()
    
    /**
     * Get alerts by farmer.
     */
    suspend fun getByFarmer(farmerId: String, limit: Int = 50): List<FarmAlert>
    
    /**
     * Get dirty (unsynced) alerts.
     */
    suspend fun getDirty(): List<FarmAlert>
    
    /**
     * Clear dirty flag for alerts.
     */
    suspend fun clearDirty(alertIds: List<String>, syncedAt: Long)
}
