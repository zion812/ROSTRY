package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.common.Result

/**
 * Domain interface for breed standard reference data.
 *
 * Provides access to breed standards, ideal weight/height ranges,
 * trait descriptions, and breed-specific guidelines.
 */
interface BreedStandardRepository {

    /** Get the breed standard for a specific breed. */
    suspend fun getBreedStandard(breedName: String): Result<Map<String, Any>>

    /** Get all available breed standards. */
    suspend fun getAllBreedStandards(): Result<List<Map<String, Any>>>

    /** Search breed standards by query. */
    suspend fun searchBreedStandards(query: String): Result<List<Map<String, Any>>>
}
