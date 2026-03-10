package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for canceling an order.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface CancelOrderUseCase {
    /**
     * Cancel an order.
     * @param orderId The order ID to cancel
     * @return Result indicating success or error
     */
    suspend operator fun invoke(orderId: String): Result<Unit>
}
