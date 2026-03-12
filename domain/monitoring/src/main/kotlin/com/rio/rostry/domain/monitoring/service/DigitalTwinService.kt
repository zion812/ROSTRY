package com.rio.rostry.domain.monitoring.service

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for the Digital Twin lifecycle service.
 *
 * Manages virtual representations of physical poultry assets including
 * creation, lifecycle tracking, growth analytics, and grading.
 */
interface DigitalTwinService {

    /** Create a digital twin from a product. */
    suspend fun createTwinFromProduct(productId: String): Result<String>

    /** Update lifecycle stage of a twin. */
    suspend fun updateLifecycle(twinId: String, stage: String): Result<Unit>

    /** Get growth timeline data for a twin. */
    suspend fun getGrowthTimeline(twinId: String): Result<List<Map<String, Any>>>

    /** Get weight analytics for a twin. */
    suspend fun getWeightAnalytics(twinId: String): Result<Map<String, Any>>

    /** Get morphological summary for a twin. */
    suspend fun getMorphSummary(twinId: String): Result<Map<String, Any>>

    /** Submit a manual grading entry. */
    suspend fun submitManualGrading(twinId: String, gradeData: Map<String, Any>): Result<Unit>

    /** Check if a product has an associated digital twin. */
    suspend fun hasTwin(productId: String): Boolean

    /** Get the twin ID for a product. */
    suspend fun getTwinId(productId: String): String?
}
