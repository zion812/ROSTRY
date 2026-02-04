package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun streamTransactionsByUser(userId: String): Flow<List<TransactionEntity>>
    fun streamTransactionsByOrder(orderId: String): Flow<List<TransactionEntity>>
    fun streamAllTransactions(): Flow<List<TransactionEntity>>
    fun streamTotalRevenue(): Flow<Double?>
    
    suspend fun recordTransaction(
        orderId: String,
        amount: Double,
        status: String,
        paymentMethod: String,
        gatewayRef: String? = null,
        notes: String? = null
    )
}
