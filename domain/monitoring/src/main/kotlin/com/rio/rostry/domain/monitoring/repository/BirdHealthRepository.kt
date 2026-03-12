package com.rio.rostry.domain.monitoring.repository

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for bird health tracking and monitoring.
 *
 * Provides health records, wellness scoring, and medical
 * event tracking for individual birds.
 */
interface BirdHealthRepository {

    fun observeBirdHealth(birdId: String): Flow<Map<String, Any>?>
    suspend fun getHealthSummary(birdId: String): Result<Map<String, Any>>
    suspend fun getHealthHistory(birdId: String): Result<List<Map<String, Any>>>
    suspend fun getMedicalEvents(birdId: String): Result<List<Map<String, Any>>>
    suspend fun calculateWellnessScore(birdId: String): Result<Float>
}
