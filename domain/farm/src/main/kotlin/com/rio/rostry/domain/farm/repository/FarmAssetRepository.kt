package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for farm asset operations.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface FarmAssetRepository {
    /**
     * Get all farm assets for a farmer.
     * @param farmerId The farmer ID
     * @return Flow of farm assets
     */
    fun getAssetsByFarmer(farmerId: String): Flow<List<FarmAsset>>

    /**
     * Get asset by ID.
     * @param assetId The asset ID
     * @return Result containing the asset or error
     */
    suspend fun getAssetById(assetId: String): Result<FarmAsset>

    /**
     * Create a new farm asset.
     * @param asset The asset to create
     * @return Result containing the created asset or error
     */
    suspend fun createAsset(asset: FarmAsset): Result<FarmAsset>

    /**
     * Update a farm asset.
     * @param asset The updated asset
     * @return Result indicating success or error
     */
    suspend fun updateAsset(asset: FarmAsset): Result<Unit>

    /**
     * Delete a farm asset.
     * @param assetId The asset ID to delete
     * @return Result indicating success or error
     */
    suspend fun deleteAsset(assetId: String): Result<Unit>

    /**
     * Get assets by lifecycle stage.
     * @param farmerId The farmer ID
     * @param stage The lifecycle stage
     * @return Flow of matching assets
     */
    fun getAssetsByLifecycleStage(farmerId: String, stage: String): Flow<List<FarmAsset>>

    /**
     * Transition asset to harvested state.
     * @param assetId The asset ID
     * @return Result indicating success or error
     */
    suspend fun harvestAsset(assetId: String): Result<Unit>
}
