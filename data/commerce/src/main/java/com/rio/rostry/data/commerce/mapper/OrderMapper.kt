package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.OrderItem
import com.rio.rostry.core.model.OrderStatus as DomainStatus
import com.rio.rostry.core.model.PaymentStatus as DomainPaymentStatus
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.OrderItemEntity

fun OrderEntity.toOrder(items: List<OrderItemEntity>): Order =
    Order(
        id = orderId,
        buyerId = buyerId ?: "",
        buyerName = null,
        sellerId = sellerId,
        sellerName = null,
        items = items.map { it.toOrderItem() },
        status = when (status) {
            "PLACED" -> DomainStatus.CONFIRMED
            "PROCESSING" -> DomainStatus.PROCESSING
            "DELIVERED" -> DomainStatus.DELIVERED
            "CANCELLED" -> DomainStatus.CANCELLED
            "REFUNDED" -> DomainStatus.REFUNDED
            else -> DomainStatus.PENDING
        },
        subtotal = totalAmount,
        shippingCost = 0.0,
        tax = 0.0,
        total = totalAmount,
        currency = "USD",
        shippingAddress = null,
        billingAddress = null,
        paymentMethod = paymentMethod,
        paymentStatus = when (paymentStatus.lowercase()) {
            "success" -> DomainPaymentStatus.CAPTURED
            "failed" -> DomainPaymentStatus.FAILED
            "refunded" -> DomainPaymentStatus.REFUNDED
            else -> DomainPaymentStatus.PENDING
        },
        notes = notes,
        trackingNumber = null,
        estimatedDelivery = expectedDeliveryDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = actualDeliveryDate
    )

fun OrderItemEntity.toOrderItem(): OrderItem =
    OrderItem(
        id = "${orderId}_${productId}",
        orderId = orderId,
        productId = productId,
        productName = "", // Not in entity
        productImageUrl = null,
        price = priceAtPurchase,
        quantity = quantity.toInt(),
        subtotal = priceAtPurchase * quantity
    )

fun Order.toEntity(): OrderEntity =
    OrderEntity(
        orderId = id,
        buyerId = buyerId,
        sellerId = sellerId,
        totalAmount = total,
        status = when (status) {
            DomainStatus.CONFIRMED -> "PLACED"
            DomainStatus.PROCESSING -> "PROCESSING"
            DomainStatus.DELIVERED -> "DELIVERED"
            DomainStatus.CANCELLED -> "CANCELLED"
            DomainStatus.REFUNDED -> "REFUNDED"
            else -> "PENDING_PAYMENT"
        },
        paymentMethod = paymentMethod,
        paymentStatus = when (paymentStatus) {
            DomainPaymentStatus.CAPTURED -> "success"
            DomainPaymentStatus.FAILED -> "failed"
            DomainPaymentStatus.REFUNDED -> "refunded"
            else -> "pending"
        },
        orderDate = createdAt,
        expectedDeliveryDate = estimatedDelivery,
        actualDeliveryDate = completedAt,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun OrderItem.toEntity(): OrderItemEntity =
    OrderItemEntity(
        orderId = orderId,
        productId = productId,
        quantity = quantity.toDouble(),
        priceAtPurchase = price,
        unitAtPurchase = "unit"
    )
