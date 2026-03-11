package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Listing
import com.rio.rostry.core.model.ListingStatus
import com.rio.rostry.data.database.entity.MarketListingEntity

fun MarketListingEntity.toListing(): Listing =
    Listing(
        id = listingId,
        productId = inventoryId,
        sellerId = sellerId,
        sellerName = null, // Not in entity
        title = title,
        description = description,
        price = price,
        currency = currency,
        quantity = 1, // Not directly in entity as Int
        minimumOrderQuantity = minOrderQuantity.toInt(),
        category = category,
        images = imageUrls,
        status = when (status) {
            "DRAFT" -> ListingStatus.DRAFT
            "PUBLISHED" -> ListingStatus.ACTIVE
            "SUSPENDED" -> ListingStatus.PAUSED
            "SOLD_OUT" -> ListingStatus.SOLD_OUT
            else -> ListingStatus.ACTIVE
        },
        isPromoted = false,
        promotionEndsAt = null,
        views = viewsCount,
        favorites = 0,
        location = locationName,
        latitude = latitude,
        longitude = longitude,
        shippingOptions = emptyList(), // Complex mapping if needed
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        expiresAt = expiresAt
    )

fun Listing.toEntity(): MarketListingEntity =
    MarketListingEntity(
        listingId = id,
        sellerId = sellerId,
        inventoryId = productId,
        title = title,
        description = description,
        price = price,
        currency = currency,
        category = category,
        tags = tags,
        locationName = location,
        latitude = latitude,
        longitude = longitude,
        minOrderQuantity = minimumOrderQuantity.toDouble(),
        imageUrls = images,
        status = when (status) {
            ListingStatus.DRAFT -> "DRAFT"
            ListingStatus.ACTIVE -> "PUBLISHED"
            ListingStatus.PAUSED -> "SUSPENDED"
            ListingStatus.SOLD_OUT -> "SOLD_OUT"
            ListingStatus.EXPIRED -> "PUBLISHED" // Fallback
            ListingStatus.REMOVED -> "SUSPENDED" // Fallback
        },
        isActive = status == ListingStatus.ACTIVE,
        viewsCount = views,
        createdAt = createdAt,
        updatedAt = updatedAt,
        expiresAt = expiresAt
    )

fun List<MarketListingEntity>.toListings(): List<Listing> = map { it.toListing() }

fun List<Listing>.toListingEntities(): List<MarketListingEntity> = map { it.toEntity() }
