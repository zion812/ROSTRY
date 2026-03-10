package com.rio.rostry.data.commerce.mapper

import com.rio.rostry.core.model.Transaction
import com.rio.rostry.data.database.entity.TransactionEntity

/**
 * Maps TransactionEntity to Transaction domain model.
 */
fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.transactionId,
        orderId = this.orderId,
        userId = this.userId,
        amount = this.amount,
        currency = this.currency,
        status = this.status,
        paymentMethod = this.paymentMethod,
        gatewayReference = this.gatewayReference,
        timestamp = this.timestamp,
        notes = this.notes
    )
}

/**
 * Maps Transaction domain model to TransactionEntity.
 */
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        transactionId = this.transactionId,
        orderId = this.orderId,
        userId = this.userId,
        amount = this.amount,
        currency = this.currency,
        status = this.status,
        paymentMethod = this.paymentMethod,
        gatewayReference = this.gatewayReference,
        timestamp = this.timestamp,
        notes = this.notes
    )
}
