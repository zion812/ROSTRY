package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for order management operations.
 *
 * Provides order lifecycle management including creation,
 * status tracking, and fulfilment operations.
 */
interface OrderManagementRepository {

    fun getActiveOrders(userId: String): Flow<List<Map<String, Any>>>
    suspend fun getOrderDetails(orderId: String): Result<Map<String, Any>>
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit>
    suspend fun getOrderHistory(userId: String, page: Int = 0, pageSize: Int = 20): Result<List<Map<String, Any>>>
    suspend fun getOrderStats(): Result<Map<String, Any>>
}
