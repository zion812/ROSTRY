package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.model.Transaction
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for transaction management.
 * 
 * Handles financial transactions for orders including payment tracking,
 * revenue reporting, and transaction history.
 */
interface TransactionRepository {
    
    /**
     * Stream all transactions for a specific user.
     */
    fun streamTransactionsByUser(userId: String): Flow<List<Transaction>>
    
    /**
     * Stream all transactions for a specific order.
     */
    fun streamTransactionsByOrder(orderId: String): Flow<List<Transaction>>
    
    /**
     * Stream all transactions in the system.
     */
    fun streamAllTransactions(): Flow<List<Transaction>>
    
    /**
     * Stream total revenue across all transactions.
     */
    fun streamTotalRevenue(): Flow<Double?>
    
    /**
     * Record a new transaction for an order.
     */
    suspend fun recordTransaction(
        orderId: String,
        amount: Double,
        status: String,
        paymentMethod: String,
        gatewayRef: String? = null,
        notes: String? = null
    ): Result<Unit>
}
