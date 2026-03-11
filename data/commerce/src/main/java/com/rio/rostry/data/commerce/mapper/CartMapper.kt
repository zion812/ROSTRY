package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.CartItem
import com.rio.rostry.data.database.entity.CartItemEntity

fun CartItemEntity.toCartItem(): CartItem =
    CartItem(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        addedAt = addedAt
    )

fun CartItem.toEntity(): CartItemEntity =
    CartItemEntity(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        addedAt = addedAt
    )

fun List<CartItemEntity>.toCartItems(): List<CartItem> = map { it.toCartItem() }

fun List<CartItem>.toCartEntities(): List<CartItemEntity> = map { it.toEntity() }
