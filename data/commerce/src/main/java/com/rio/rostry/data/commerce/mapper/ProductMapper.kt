package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Product
import com.rio.rostry.data.database.entity.ProductEntity

fun ProductEntity.toProduct(): Product =
    Product(
        id = productId,
        name = name,
        sellerId = sellerId,
        category = category,
        price = price,
        quantity = quantity,
        unit = unit,
        currency = "USD", // Not explicitly in entity
        description = description,
        imageUrls = imageUrls,
        breed = breed,
        gender = gender,
        birthDate = birthDate,
        ageWeeks = ageWeeks,
        colorTag = colorTag,
        birdCode = birdCode,
        stage = stage?.name ?: "UNKNOWN",
        lifecycleStatus = lifecycleStatus ?: "unknown",
        lastStageTransitionAt = lastStageTransitionAt,
        latitude = latitude,
        longitude = longitude,
        location = location,
        familyTreeId = familyTreeId,
        parentMaleId = parentMaleId,
        parentFemaleId = parentFemaleId,
        isTraceable = !familyTreeId.isNullOrBlank(),
        isVerified = false, // Not in entity
        verificationLevel = null, // Not in entity
        qrCodeUrl = qrCodeUrl,
        metadata = null, // JSON parsing needed if required
        recordsLockedAt = recordsLockedAt,
        autoLockAfterDays = autoLockAfterDays,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun Product.toEntity(): ProductEntity =
    ProductEntity(
        productId = id,
        sellerId = sellerId,
        name = name,
        description = description ?: "",
        category = category,
        price = price,
        quantity = quantity,
        unit = unit,
        location = location ?: "",
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrls,
        birthDate = birthDate,
        gender = gender,
        breed = breed,
        birdCode = birdCode,
        colorTag = colorTag,
        familyTreeId = familyTreeId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lifecycleStatus = lifecycleStatus,
        parentMaleId = parentMaleId,
        parentFemaleId = parentFemaleId,
        ageWeeks = ageWeeks,
        lastStageTransitionAt = lastStageTransitionAt,
        qrCodeUrl = qrCodeUrl,
        recordsLockedAt = recordsLockedAt,
        autoLockAfterDays = autoLockAfterDays
    )

fun List<ProductEntity>.toProducts(): List<Product> = map { it.toProduct() }

fun List<Product>.toEntities(): List<ProductEntity> = map { it.toEntity() }
