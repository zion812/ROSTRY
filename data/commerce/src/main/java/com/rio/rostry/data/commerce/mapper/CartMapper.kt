package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.CartItem
import com.rio.rostry.data.database.entity.CartItemEntity

fun CartItemEntity.toCartItem(): CartItem =
    TODO("Temporary mapper stub during modularization")

fun CartItem.toEntity(): CartItemEntity =
    TODO("Temporary mapper stub during modularization")

fun List<CartItemEntity>.toCartItems(): List<CartItem> = map { it.toCartItem() }

fun List<CartItem>.toCartEntities(): List<CartItemEntity> = map { it.toEntity() }
