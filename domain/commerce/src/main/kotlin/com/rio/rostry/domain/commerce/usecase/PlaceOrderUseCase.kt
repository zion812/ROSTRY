package com.rio.rostry.domain.commerce.usecase

import com.rio.rostry.core.model.Order
import com.rio.rostry.core.model.Result

/**
 * Use case for placing an order.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface PlaceOrderUseCase {
    /**
     * Place a new order.
     * @param request The order placement request
     * @return Result containing the created order or error
     */
    suspend operator fun invoke(request: PlaceOrderRequest): Result<Order>
}

/**
 * Request data for placing an order.
 */
data class PlaceOrderRequest(
    val listingId: String,
    val quantity: Int,
    val shippingAddress: String?,
    val paymentMethod: String?
)
