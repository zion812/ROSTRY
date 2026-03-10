package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.CartItem
import com.rio.rostry.data.database.entity.CartItemEntity

/**
 * Legacy/simple mapping helpers kept with distinct names to avoid extension conflicts.
 */
fun CartItemEntity.toSimpleCartItem(): CartItem {
    return CartItem(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        addedAt = addedAt
    )
}

fun CartItem.toSimpleEntity(): CartItemEntity {
    return CartItemEntity(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        addedAt = addedAt
    )
}
