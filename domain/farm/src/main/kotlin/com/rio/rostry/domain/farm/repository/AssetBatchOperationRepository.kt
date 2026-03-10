package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.AssetBatchOperation
import com.rio.rostry.core.common.Result

/**
 * Repository contract for asset batch operations.
 * 
 * Handles bulk operations on multiple farm assets such as batch updates,
 * mass transfers, or group modifications.
 */
interface AssetBatchOperationRepository {
    
    /**
     * Create a new batch operation.
     */
    suspend fun createOperation(operation: AssetBatchOperation): Result<Unit>
    
    /**
     * Update an existing batch operation.
     */
    suspend fun updateOperation(operation: AssetBatchOperation): Result<Unit>
    
    /**
     * Get a batch operation by ID.
     */
    suspend fun getOperationById(operationId: String): Result<AssetBatchOperation?>
    
    /**
     * Get all batch operations for a specific farmer.
     */
    suspend fun getOperationsByFarmer(farmerId: String): Result<List<AssetBatchOperation>>
}
