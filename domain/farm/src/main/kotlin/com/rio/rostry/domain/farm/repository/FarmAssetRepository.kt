package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for farm asset management.
 */
interface FarmAssetRepository {
    
    /**
     * Gets all assets for a farmer.
     */
    fun getAssetsByFarmer(farmerId: String): Flow<Result<List<FarmAsset>>>
    
    /**
     * Gets a specific asset by ID.
     */
    fun getAssetById(assetId: String): Flow<Result<FarmAsset?>>
    
    /**
     * Gets assets filtered by type.
     */
    fun getAssetsByType(farmerId: String, type: String): Flow<Result<List<FarmAsset>>>
    
    /**
     * Gets showcase assets for a farmer.
     */
    fun getShowcaseAssets(farmerId: String): Flow<Result<List<FarmAsset>>>
    
    /**
     * Adds a new asset.
     */
    suspend fun addAsset(asset: FarmAsset): Result<String>
    
    /**
     * Updates an existing asset.
     */
    suspend fun updateAsset(asset: FarmAsset): Result<Unit>
    
    /**
     * Deletes an asset.
     */
    suspend fun deleteAsset(assetId: String): Result<Unit>
    
    /**
     * Updates asset quantity.
     */
    suspend fun updateQuantity(assetId: String, quantity: Double): Result<Unit>
    
    /**
     * Updates asset health status.
     */
    suspend fun updateHealthStatus(assetId: String, status: String): Result<Unit>
    
    /**
     * Syncs assets with remote storage.
     */
    suspend fun syncAssets(): Result<Unit>
    
    // Marketplace Lifecycle
    
    /**
     * Marks an asset as listed on marketplace.
     */
    suspend fun markAsListed(assetId: String, listingId: String, listedAt: Long): Result<Unit>
    
    /**
     * Marks an asset as delisted from marketplace.
     */
    suspend fun markAsDeListed(assetId: String): Result<Unit>
    
    /**
     * Marks an asset as sold.
     */
    suspend fun markAsSold(assetId: String, buyerId: String, price: Double): Result<Unit>

    // Batch Lifecycle (Phase 3)
    
    /**
     * Graduates a batch into individual assets.
     */
    suspend fun graduateBatch(batchId: String, newAssets: List<FarmAsset>): Result<Unit>

    // Bird Studio - Appearance Customization
    
    /**
     * Updates asset metadata JSON.
     */
    suspend fun updateMetadataJson(assetId: String, metadataJson: String): Result<Unit>

    // Immutable Snapshot Pattern
    
    /**
     * Creates a snapshot listing for an asset.
     */
    suspend fun createSnapshotListing(
        assetId: String,
        price: Double = 0.0,
        listingTitle: String? = null,
        listingDescription: String? = null
    ): Result<Product>
    
    /**
     * Delists an asset from marketplace.
     */
    suspend fun delistAsset(assetId: String): Result<Unit>
    
    /**
     * Applies a quantity delta to an asset.
     */
    suspend fun applyQuantityDelta(assetId: String, delta: Double): Result<Unit>
}

