package com.rio.rostry.domain.farm.service

import com.rio.rostry.core.common.Result

/**
 * Domain interface for asset lifecycle documentation.
 *
 * Aggregates all data related to a single asset including activities,
 * vaccinations, growth records, mortality, and lifecycle events.
 */
interface AssetDocumentationService {

    /** Load complete documentation for a given asset. */
    suspend fun loadDocumentation(assetId: String): Result<Map<String, Any>>

    /** Generate a chronological timeline of all lifecycle events. */
    suspend fun generateTimeline(assetId: String): Result<List<Map<String, Any>>>

    /** Gather all media items associated with an asset. */
    suspend fun getAssetMedia(assetId: String): Result<List<Map<String, Any>>>
}
