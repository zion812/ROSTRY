package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for farm-wide documentation aggregation.
 *
 * Aggregates data across all farm assets including vaccinations,
 * growth records, mortality, feed consumption, and financial summaries.
 */
interface FarmDocumentationService {

    /** Load complete farm documentation for the current user. */
    suspend fun loadFarmDocumentation(): Result<Map<String, Any>>

    /** Generate asset summaries for all farm assets. */
    suspend fun generateAssetSummaries(): Result<List<Map<String, Any>>>
}
