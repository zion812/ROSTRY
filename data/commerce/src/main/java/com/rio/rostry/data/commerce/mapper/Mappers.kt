package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.core.model.MarketListingStatus
import com.rio.rostry.core.model.MarketListing

fun MarketListingEntity.toDomainModel(): MarketListing {
    return MarketListing(
        id = listingId,
        inventoryItemId = inventoryId,
        sellerId = sellerId,
        price = price,
        currency = currency,
        minimumOrderQuantity = minOrderQuantity.toInt(),
        title = title,
        description = description,
        images = imageUrls,
        status = when (status) {
            "ACTIVE", "PUBLISHED" -> MarketListingStatus.ACTIVE
            "SOLD_OUT" -> MarketListingStatus.SOLD
            "SUSPENDED" -> MarketListingStatus.SUSPENDED
            else -> MarketListingStatus.INACTIVE
        },
        shippingOptions = deliveryOptions,
        category = category,
        tags = tags,
        viewsCount = viewsCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun MarketListing.toEntity(): MarketListingEntity {
    return MarketListingEntity(
        listingId = id,
        sellerId = sellerId,
        inventoryId = inventoryItemId,
        title = title,
        description = description,
        price = price,
        currency = currency,
        minOrderQuantity = minimumOrderQuantity.toDouble(),
        category = category ?: "",
        tags = tags,
        deliveryOptions = shippingOptions,
        imageUrls = images,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

