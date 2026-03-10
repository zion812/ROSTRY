package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.Result

/**
 * Use case for updating order status.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface UpdateOrderStatusUseCase {
    /**
     * Update the status of an order.
     * @param orderId The order ID to update
     * @param status The new status
     * @return Result indicating success or error
     */
    suspend operator fun invoke(orderId: String, status: String): Result<Unit>
}
