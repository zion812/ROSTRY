package com.rio.rostry.data.database.mapper

import com.rio.rostry.core.model.Payment
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.database.entity.statusEnum
import com.rio.rostry.domain.model.PaymentStatus

fun PaymentEntity.toDomain() = Payment(
    paymentId = paymentId,
    orderId = orderId,
    userId = userId,
    method = method,
    amount = amount,
    currency = currency,
    status = statusEnum(),
    providerRef = providerRef,
    upiUri = upiUri,
    idempotencyKey = idempotencyKey,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Payment.toEntity() = PaymentEntity(
    paymentId = paymentId,
    orderId = orderId,
    userId = userId,
    method = method,
    amount = amount,
    currency = currency,
    status = status.toStoredString(),
    providerRef = providerRef,
    upiUri = upiUri,
    idempotencyKey = idempotencyKey,
    createdAt = createdAt,
    updatedAt = updatedAt
)
