package com.rio.rostry.domain.farm.repository

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for farm profile management.
 *
 * Manages farm profiles including creation, updates, trust score computation,
 * public timeline access, and farm discovery/search.
 */
interface FarmProfileRepository {

    /** Observe the farm profile for a user. */
    fun observeProfile(userId: String): Flow<Map<String, Any>?>

    /** Get the farm profile snapshot. */
    suspend fun getProfile(userId: String): Result<Map<String, Any>>

    /** Create or update a farm profile. */
    suspend fun createOrUpdateProfile(userId: String, data: Map<String, Any>): Result<Unit>

    /** Recalculate trust score for a farm. */
    suspend fun recalculateTrustScore(userId: String): Result<Float>

    /** Observe the public farm timeline. */
    fun observePublicTimeline(): Flow<List<Map<String, Any>>>

    /** Get top-ranked farms. */
    suspend fun getTopFarms(limit: Int = 10): Result<List<Map<String, Any>>>

    /** Search for farms by query. */
    suspend fun searchFarms(query: String): Result<List<Map<String, Any>>>
}
