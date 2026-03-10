package com.rio.rostry.data.database.mapper

import com.rio.rostry.core.model.Product
import com.rio.rostry.data.database.entity.ProductEntity

fun ProductEntity.toDomain() = Product(
    id = productId,
    name = name,
    sellerId = sellerId,
    category = category,
    price = price,
    quantity = quantity,
    unit = unit,
    description = description,
    imageUrls = imageUrls,
    breed = breed,
    gender = gender,
    birthDate = birthDate,
    ageWeeks = ageWeeks,
    colorTag = colorTag,
    birdCode = birdCode,
    stage = stage?.toString() ?: "unknown",
    lifecycleStatus = lifecycleStatus ?: "unknown",
    lastStageTransitionAt = lastStageTransitionAt,
    latitude = latitude,
    longitude = longitude,
    location = location,
    familyTreeId = familyTreeId,
    parentMaleId = parentMaleId,
    parentFemaleId = parentFemaleId,
    isTraceable = familyTreeId != null,
    isVerified = true, // Simplified
    verificationLevel = "Basic", // Simplified
    qrCodeUrl = qrCodeUrl,
    metadata = emptyMap(), // Simplified
    recordsLockedAt = recordsLockedAt,
    autoLockAfterDays = autoLockAfterDays,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Product.toEntity() = ProductEntity(
    productId = id,
    name = name,
    sellerId = sellerId,
    category = category,
    price = price,
    quantity = quantity,
    unit = unit,
    description = description ?: "",
    imageUrls = imageUrls,
    breed = breed,
    gender = gender,
    birthDate = birthDate,
    ageWeeks = ageWeeks,
    colorTag = colorTag,
    birdCode = birdCode,
    lifecycleStatus = lifecycleStatus,
    lastStageTransitionAt = lastStageTransitionAt,
    latitude = latitude,
    longitude = longitude,
    location = location ?: "",
    familyTreeId = familyTreeId,
    parentMaleId = parentMaleId,
    parentFemaleId = parentFemaleId,
    qrCodeUrl = qrCodeUrl,
    recordsLockedAt = recordsLockedAt,
    autoLockAfterDays = autoLockAfterDays,
    createdAt = createdAt,
    updatedAt = updatedAt
)
