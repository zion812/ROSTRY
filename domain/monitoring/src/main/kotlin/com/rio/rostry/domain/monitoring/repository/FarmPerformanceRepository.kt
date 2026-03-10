package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.model.LifecycleEvent
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for farm performance and lifecycle tracking.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain interfaces have zero Android dependencies
 */
interface FarmPerformanceRepository {
    /**
     * Observe lifecycle events for a specific product.
     */
    fun observeLifecycle(productId: String): Flow<List<LifecycleEvent>>
}
