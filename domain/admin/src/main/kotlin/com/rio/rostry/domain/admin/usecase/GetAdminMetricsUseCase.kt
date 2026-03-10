package com.rio.rostry.domain.admin.usecase

import com.rio.rostry.core.model.AdminMetrics
import com.rio.rostry.core.model.Result

/**
 * Use case for retrieving admin metrics.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain modules define interfaces without implementation details
 */
interface GetAdminMetricsUseCase {
    /**
     * Get admin metrics including user counts, activity stats, and system health.
     * @return Result containing admin metrics or error
     */
    suspend operator fun invoke(): Result<AdminMetrics>
}
