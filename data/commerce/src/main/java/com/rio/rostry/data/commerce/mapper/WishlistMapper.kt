package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Wishlist
import com.rio.rostry.data.database.entity.WishlistEntity

/**
 * Converts WishlistEntity to domain model.
 */
fun WishlistEntity.toWishlist(): Wishlist {
    return Wishlist(
        userId = userId,
        productId = productId,
        addedAt = addedAt
    )
}

/**
 * Converts domain model to WishlistEntity.
 */
fun Wishlist.toEntity(): WishlistEntity {
    return WishlistEntity(
        userId = userId,
        productId = productId,
        addedAt = addedAt
    )
}
