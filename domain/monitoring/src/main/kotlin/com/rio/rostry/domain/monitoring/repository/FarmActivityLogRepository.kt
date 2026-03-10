package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.FarmActivityLog
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for farm activity log management.
 * 
 * Handles farm-level activities like expenses, sanitation, maintenance, etc.
 * Includes Smart Chore auto-completion logic - when a log is created,
 * matching pending tasks are automatically completed.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface FarmActivityLogRepository {
    /**
     * Observe all activity logs for a farmer.
     */
    fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLog>>
    
    /**
     * Observe the latest activity log for a farmer.
     */
    fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLog?>
    
    /**
     * Observe activity logs for a farmer filtered by type.
     */
    fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLog>>
    
    /**
     * Observe activity logs for a farmer within a time range.
     */
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLog>>
    
    /**
     * Observe activity logs for a specific product.
     */
    fun observeForProduct(productId: String): Flow<List<FarmActivityLog>>
    
    /**
     * Insert or update an activity log.
     */
    suspend fun upsert(log: FarmActivityLog)
    
    /**
     * Log a new activity and auto-complete matching tasks.
     */
    suspend fun logActivity(
        farmerId: String,
        productId: String?,
        activityType: String,
        amount: Double? = null,
        quantity: Double? = null,
        category: String? = null,
        description: String? = null,
        notes: String? = null
    ): FarmActivityLog
    
    /**
     * Get total expenses for a farmer within a time range.
     */
    suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double
    
    /**
     * Get an activity log by ID.
     */
    suspend fun getById(activityId: String): FarmActivityLog?
    
    /**
     * Delete an activity log.
     */
    suspend fun deleteActivity(activityId: String)
    
    /**
     * Get total feed quantity for a specific asset.
     */
    suspend fun getTotalFeedQuantityForAsset(assetId: String): Double
}
