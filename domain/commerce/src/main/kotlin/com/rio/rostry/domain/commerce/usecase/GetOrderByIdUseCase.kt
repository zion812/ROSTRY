package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.Result

/**
 * Use case for getting an order by ID.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetOrderByIdUseCase {
    /**
     * Get an order by its ID.
     * @param orderId The order ID
     * @return Result containing the order or error
     */
    suspend operator fun invoke(orderId: String): Result<Order>
}
