package com.rio.rostry.data.farm.repository

import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Product
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.farm.repository.FarmAssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe farm asset repository stub used during modular migration.
 */
@Singleton
class FarmAssetRepositoryImpl @Inject constructor() : FarmAssetRepository {

    override fun getAssetsByFarmer(farmerId: String): Flow<Result<List<FarmAsset>>> =
        flowOf(Result.Success(emptyList()))

    override fun getAssetById(assetId: String): Flow<Result<FarmAsset?>> =
        flowOf(Result.Success(null))

    override fun getAssetsByType(farmerId: String, type: String): Flow<Result<List<FarmAsset>>> =
        flowOf(Result.Success(emptyList()))

    override fun getShowcaseAssets(farmerId: String): Flow<Result<List<FarmAsset>>> =
        flowOf(Result.Success(emptyList()))

    override suspend fun addAsset(asset: FarmAsset): Result<String> =
        Result.Success(java.util.UUID.randomUUID().toString())

    override suspend fun updateAsset(asset: FarmAsset): Result<Unit> = Result.Success(Unit)

    override suspend fun deleteAsset(assetId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun updateQuantity(assetId: String, quantity: Double): Result<Unit> = Result.Success(Unit)

    override suspend fun updateHealthStatus(assetId: String, status: String): Result<Unit> = Result.Success(Unit)

    override suspend fun syncAssets(): Result<Unit> = Result.Success(Unit)

    override suspend fun markAsListed(assetId: String, listingId: String, listedAt: Long): Result<Unit> =
        Result.Success(Unit)

    override suspend fun markAsDeListed(assetId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun markAsSold(assetId: String, buyerId: String, price: Double): Result<Unit> =
        Result.Success(Unit)

    override suspend fun graduateBatch(batchId: String, newAssets: List<FarmAsset>): Result<Unit> =
        Result.Success(Unit)

    override suspend fun updateMetadataJson(assetId: String, metadataJson: String): Result<Unit> =
        Result.Success(Unit)

    override suspend fun createSnapshotListing(
        assetId: String,
        price: Double,
        listingTitle: String?,
        listingDescription: String?
    ): Result<Product> = Result.Error(UnsupportedOperationException("Snapshot listing not available in stub"))

    override suspend fun delistAsset(assetId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun applyQuantityDelta(assetId: String, delta: Double): Result<Unit> = Result.Success(Unit)
}

