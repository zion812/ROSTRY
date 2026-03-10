package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for coin/credit management.
 * 
 * Handles virtual currency transactions including purchases, usage, and refunds.
 */
interface CoinRepository {
    /**
     * Observes the coin balance for a user.
     * 
     * @param userId The user ID
     * @return Flow emitting the current balance
     */
    fun observeBalance(userId: String): Flow<Int>
    
    /**
     * Gets the current coin balance for a user.
     * 
     * @param userId The user ID
     * @return The current balance
     */
    suspend fun getBalance(userId: String): Int
    
    /**
     * Records a coin purchase transaction.
     * 
     * @param userId The user ID
     * @param coins Number of coins purchased (must be > 0)
     * @param paymentRef Optional payment reference
     * @return Result indicating success or failure
     */
    suspend fun purchaseCoins(
        userId: String,
        coins: Int,
        paymentRef: String? = null
    ): Result<Unit>
    
    /**
     * Records a coin usage transaction.
     * 
     * @param userId The user ID
     * @param coins Number of coins to use (must be > 0)
     * @param refId Optional reference ID for the transaction
     * @param notes Optional notes about the usage
     * @return Result indicating success or failure
     */
    suspend fun useCoins(
        userId: String,
        coins: Int,
        refId: String?,
        notes: String? = null
    ): Result<Unit>
    
    /**
     * Records a coin refund transaction.
     * 
     * @param userId The user ID
     * @param coins Number of coins to refund (must be > 0)
     * @param refId Optional reference ID for the transaction
     * @param notes Optional notes about the refund
     * @return Result indicating success or failure
     */
    suspend fun refundCoins(
        userId: String,
        coins: Int,
        refId: String?,
        notes: String? = null
    ): Result<Unit>
}

