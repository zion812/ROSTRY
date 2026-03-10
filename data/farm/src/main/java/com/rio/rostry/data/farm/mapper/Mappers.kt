package com.rio.rostry.data.farm.mapper

import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.InventoryItemEntity
import com.rio.rostry.core.model.AssetType
import com.rio.rostry.core.model.FarmAsset
import com.rio.rostry.core.model.Gender
import com.rio.rostry.core.model.HealthStatus
import com.rio.rostry.core.model.InventoryItem
import com.rio.rostry.core.model.QualityGrade

fun FarmAssetEntity.toDomainModel(): FarmAsset {
    return FarmAsset(
        id = assetId,
        farmerId = farmerId,
        assetType = when (assetType) {
            "BATCH" -> AssetType.BATCH
            "EQUIPMENT" -> AssetType.EQUIPMENT
            "BUILDING" -> AssetType.BUILDING
            else -> AssetType.BIRD
        },
        birthDate = birthDate,
        breed = breed,
        gender = when (gender) {
            "MALE" -> Gender.MALE
            "FEMALE" -> Gender.FEMALE
            else -> Gender.UNKNOWN
        },
        healthStatus = when (healthStatus) {
            "SICK" -> HealthStatus.SICK
            "QUARANTINED" -> HealthStatus.QUARANTINED
            "DECEASED" -> HealthStatus.DECEASED
            else -> HealthStatus.HEALTHY
        },
        location = locationName,
        lifecycleStage = lifecycleSubStage ?: "ADULT",
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun FarmAsset.toEntity(): FarmAssetEntity {
    return FarmAssetEntity(
        assetId = id,
        farmerId = farmerId,
        assetType = assetType.name,
        birthDate = birthDate,
        breed = breed,
        gender = gender?.name,
        healthStatus = healthStatus.name,
        locationName = location,
        lifecycleSubStage = lifecycleStage,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun InventoryItemEntity.toDomainModel(): InventoryItem {
    return InventoryItem(
        id = inventoryId,
        farmAssetId = sourceAssetId ?: "",
        farmerId = farmerId,
        quantity = quantityAvailable,
        unit = unit,
        harvestDate = producedAt,
        storageLocation = null, // Not in entity yet
        qualityGrade = when (qualityGrade) {
            "PREMIUM" -> QualityGrade.PREMIUM
            "ECONOMY" -> QualityGrade.ECONOMY
            else -> QualityGrade.STANDARD
        },
        expiryDate = expiresAt,
        availableQuantity = quantityAvailable,
        reservedQuantity = quantityReserved,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun InventoryItem.toEntity(): InventoryItemEntity {
    return InventoryItemEntity(
        inventoryId = id,
        farmerId = farmerId,
        sourceAssetId = farmAssetId,
        name = "", // Needs to be passed or derived
        category = "", // Needs to be passed or derived
        quantityAvailable = quantity.toDouble(),
        quantityReserved = reservedQuantity.toDouble(),
        unit = unit,
        producedAt = harvestDate,
        expiresAt = expiryDate,
        qualityGrade = qualityGrade.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// ProductTracking mappers
fun com.rio.rostry.data.database.entity.ProductTrackingEntity.toDomainModel(): com.rio.rostry.core.model.ProductTracking {
    return com.rio.rostry.core.model.ProductTracking(
        trackingId = trackingId,
        productId = productId,
        ownerId = ownerId,
        status = status,
        metadataJson = metadataJson,
        timestamp = timestamp,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun com.rio.rostry.core.model.ProductTracking.toEntity(): com.rio.rostry.data.database.entity.ProductTrackingEntity {
    return com.rio.rostry.data.database.entity.ProductTrackingEntity(
        trackingId = trackingId,
        productId = productId,
        ownerId = ownerId,
        status = status,
        metadataJson = metadataJson,
        timestamp = timestamp,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = false,
        deletedAt = null,
        dirty = true
    )
}
