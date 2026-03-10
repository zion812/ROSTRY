package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.OrderItem
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderItemEntity

fun OrderEntity.toOrder(items: List<OrderItemEntity>): Order =
    TODO("Temporary mapper stub during modularization")

fun OrderItemEntity.toOrderItem(): OrderItem =
    TODO("Temporary mapper stub during modularization")

fun Order.toEntity(): OrderEntity =
    TODO("Temporary mapper stub during modularization")

fun OrderItem.toEntity(): OrderItemEntity =
    TODO("Temporary mapper stub during modularization")
