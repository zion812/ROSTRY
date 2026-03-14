package com.rio.rostry.data.farm.repository

import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Product
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.farm.repository.FarmAssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real farm asset repository implementation with database and Firestore sync.
 */
@Singleton
class FarmAssetRepositoryImpl @Inject constructor(
    private val farmAssetDao: com.rio.rostry.data.database.dao.FarmAssetDao,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao,
    private val firestore: com.google.firebase.firestore.FirebaseFirestore
) : FarmAssetRepository {

    override fun getAssetsByFarmer(farmerId: String): Flow<Result<List<FarmAsset>>> =
        farmAssetDao.getAssetsByFarmer(farmerId).map { entities ->
            Result.Success(entities.map { it.toDomainModel() })
        }

    override fun getAssetById(assetId: String): Flow<Result<FarmAsset?>> =
        farmAssetDao.getAssetById(assetId).map { entity ->
            Result.Success(entity?.toDomainModel())
        }

    override fun getAssetsByType(farmerId: String, type: String): Flow<Result<List<FarmAsset>>> =
        farmAssetDao.getAssetsByType(farmerId, type).map { entities ->
            Result.Success(entities.map { it.toDomainModel() })
        }

    override fun getShowcaseAssets(farmerId: String): Flow<Result<List<FarmAsset>>> =
        farmAssetDao.getShowcaseAssets(farmerId).map { entities ->
            Result.Success(entities.map { it.toDomainModel() })
        }

    override suspend fun addAsset(asset: FarmAsset): Result<String> = try {
        val entity = asset.toEntity()
        farmAssetDao.insertAsset(entity)
        Result.Success(entity.assetId)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateAsset(asset: FarmAsset): Result<Unit> = try {
        farmAssetDao.updateAsset(asset.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun deleteAsset(assetId: String): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.updateStatus(assetId, "DELETED", now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateQuantity(assetId: String, quantity: Double): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.updateQuantity(assetId, quantity, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateHealthStatus(assetId: String, status: String): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.updateHealthStatus(assetId, status, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun syncAssets(): Result<Unit> = try {
        val dirtyAssets = farmAssetDao.getDirtyAssets(limit = 100)
        dirtyAssets.forEach { asset ->
            firestore.collection("farm_assets")
                .document(asset.assetId)
                .set(asset)
                .await()
            farmAssetDao.clearDirty(asset.assetId)
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun markAsListed(assetId: String, listingId: String, listedAt: Long): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.markAsListed(assetId, listingId, listedAt, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun markAsDeListed(assetId: String): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.markAsDeListed(assetId, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun markAsSold(assetId: String, buyerId: String, price: Double): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.markAsSold(assetId, buyerId, price, now, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun graduateBatch(batchId: String, newAssets: List<FarmAsset>): Result<Unit> = try {
        newAssets.forEach { asset ->
            farmAssetDao.insertAsset(asset.toEntity())
        }
        val now = System.currentTimeMillis()
        farmAssetDao.updateStatus(batchId, "GRADUATED", now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun updateMetadataJson(assetId: String, metadataJson: String): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.updateMetadataJson(assetId, metadataJson, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun createSnapshotListing(
        assetId: String,
        price: Double,
        listingTitle: String?,
        listingDescription: String?
    ): Result<Product> = try {
        // 1. Fetch the farm asset
        val asset = farmAssetDao.findById(assetId)
            ?: return Result.Error(IllegalArgumentException("Asset not found: $assetId"))

        // 2. Check if already listed
        val existingListing = productDao.getListingForAssetSync(assetId)
        if (existingListing != null) {
            return Result.Error(IllegalStateException("Asset already has an active listing: ${existingListing.productId}"))
        }

        // 3. Create marketplace product from farm asset
        val now = System.currentTimeMillis()
        val productId = "prod_${java.util.UUID.randomUUID()}"
        
        val productEntity = com.rio.rostry.data.database.entity.ProductEntity(
            productId = productId,
            name = listingTitle ?: asset.name ?: "Bird for Sale",
            sellerId = asset.farmerId,
            category = asset.category ?: "POULTRY",
            price = price,
            quantity = asset.quantity,
            unit = "piece",
            currency = "INR",
            description = listingDescription ?: asset.description,
            imageUrls = asset.imageUrls ?: "",
            breed = asset.breed,
            gender = asset.gender,
            birthDate = asset.birthDate,
            ageWeeks = asset.ageWeeks,
            colorTag = asset.colorTag,
            birdCode = asset.birdCode,
            stage = asset.stage ?: com.rio.rostry.domain.model.LifecycleStage.ADULT,
            lifecycleStatus = "ACTIVE",
            lastStageTransitionAt = asset.lastStageTransitionAt,
            latitude = asset.latitude,
            longitude = asset.longitude,
            location = asset.location,
            familyTreeId = asset.familyTreeId,
            parentMaleId = asset.parentMaleId,
            parentFemaleId = asset.parentFemaleId,
            isTraceable = asset.familyTreeId != null,
            isVerified = false,
            verificationLevel = null,
            qrCodeUrl = asset.qrCodeUrl,
            metadata = asset.metadataJson,
            recordsLockedAt = null,
            autoLockAfterDays = 30,
            createdAt = now,
            updatedAt = now,
            sourceAssetId = assetId,
            status = "ACTIVE",
            isDeleted = false,
            dirty = true
        )

        // 4. Insert product into database
        productDao.insertProduct(productEntity)

        // 5. Link asset to listing
        farmAssetDao.linkActiveListing(assetId, productId, now)

        // 6. Sync to Firestore
        firestore.collection("products")
            .document(productId)
            .set(productEntity)
            .await()

        // 7. Convert to domain model and return
        Result.Success(productEntity.toDomainModel())
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun delistAsset(assetId: String): Result<Unit> = try {
        val now = System.currentTimeMillis()
        val listing = productDao.getListingForAssetSync(assetId)
        if (listing != null) {
            productDao.deleteProduct(listing.productId)
        }
        farmAssetDao.unlinkActiveListing(assetId, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun applyQuantityDelta(assetId: String, delta: Double): Result<Unit> = try {
        val now = System.currentTimeMillis()
        farmAssetDao.applyDelta(assetId, delta, now)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    // Extension functions for mapping
    private fun com.rio.rostry.data.database.entity.FarmAssetEntity.toDomainModel() = FarmAsset(
        id = assetId,
        farmerId = farmerId,
        assetType = when (assetType) {
            "BIRD" -> com.rio.rostry.core.model.AssetType.BIRD
            "BATCH" -> com.rio.rostry.core.model.AssetType.BATCH
            "EQUIPMENT" -> com.rio.rostry.core.model.AssetType.EQUIPMENT
            "BUILDING" -> com.rio.rostry.core.model.AssetType.BUILDING
            else -> com.rio.rostry.core.model.AssetType.BIRD
        },
        birthDate = birthDate,
        breed = breed,
        gender = gender?.let {
            when (it) {
                "MALE" -> com.rio.rostry.core.model.Gender.MALE
                "FEMALE" -> com.rio.rostry.core.model.Gender.FEMALE
                else -> com.rio.rostry.core.model.Gender.UNKNOWN
            }
        },
        healthStatus = when (healthStatus) {
            "SICK" -> com.rio.rostry.core.model.HealthStatus.SICK
            "QUARANTINED" -> com.rio.rostry.core.model.HealthStatus.QUARANTINED
            "DECEASED" -> com.rio.rostry.core.model.HealthStatus.DECEASED
            else -> com.rio.rostry.core.model.HealthStatus.HEALTHY
        },
        location = location,
        biologicalData = emptyMap(),
        lifecycleStage = stage?.name ?: "ADULT",
        parentMaleId = parentMaleId,
        parentFemaleId = parentFemaleId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun FarmAsset.toEntity() = com.rio.rostry.data.database.entity.FarmAssetEntity(
        assetId = id,
        farmerId = farmerId,
        assetType = assetType.name,
        birthDate = birthDate,
        breed = breed,
        gender = gender?.name,
        healthStatus = healthStatus.name,
        location = location,
        stage = com.rio.rostry.domain.model.LifecycleStage.valueOf(lifecycleStage),
        parentMaleId = parentMaleId,
        parentFemaleId = parentFemaleId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        status = "ACTIVE",
        isDeleted = false,
        dirty = true
    )

    private fun com.rio.rostry.data.database.entity.ProductEntity.toDomainModel() = Product(
        id = productId,
        name = name,
        sellerId = sellerId,
        category = category,
        price = price,
        quantity = quantity,
        unit = unit,
        currency = currency,
        description = description,
        imageUrls = imageUrls?.split(",") ?: emptyList(),
        breed = breed,
        gender = gender,
        birthDate = birthDate,
        ageWeeks = ageWeeks,
        colorTag = colorTag,
        birdCode = birdCode,
        stage = stage?.name ?: "ADULT",
        lifecycleStatus = lifecycleStatus ?: "ACTIVE",
        lastStageTransitionAt = lastStageTransitionAt,
        latitude = latitude,
        longitude = longitude,
        location = location,
        familyTreeId = familyTreeId,
        parentMaleId = parentMaleId,
        parentFemaleId = parentFemaleId,
        isTraceable = isTraceable,
        isVerified = isVerified,
        verificationLevel = verificationLevel,
        qrCodeUrl = qrCodeUrl,
        metadata = null,
        recordsLockedAt = recordsLockedAt,
        autoLockAfterDays = autoLockAfterDays,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
