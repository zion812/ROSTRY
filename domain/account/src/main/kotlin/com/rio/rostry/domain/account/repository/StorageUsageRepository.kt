package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.StorageQuota
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing cloud storage usage and quotas.
 */
interface StorageUsageRepository {
    
    /**
     * Observes storage quota for a user.
     */
    fun observeQuota(userId: String): Flow<StorageQuota?>
    
    /**
     * Gets current storage quota for a user.
     */
    suspend fun getQuota(userId: String): StorageQuota?
    
    /**
     * Recalculates storage usage by scanning cloud storage and updating local cache.
     */
    suspend fun refreshUsage(userId: String): Result<StorageQuota>
    
    /**
     * Checks if enough space is available for an upcoming upload.
     */
    suspend fun hasEnoughSpace(userId: String, incomingBytes: Long): Boolean
    
    /**
     * Incrementally updates usage after a successful upload/delete.
     */
    suspend fun adjustUsage(userId: String, bytesDelta: Long)
}

