package com.rio.rostry.domain.social.service

import com.rio.rostry.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Domain interface for community engagement features.
 *
 * Manages community interactions, event discovery, nearby farms,
 * and community content feeds.
 */
interface CommunityEngagementService {

    suspend fun getCommunityFeed(userId: String): Result<List<Map<String, Any>>>
    suspend fun getNearbyFarms(latitude: Double, longitude: Double, radiusKm: Double = 50.0): Result<List<Map<String, Any>>>
    suspend fun getUpcomingEvents(userId: String): Result<List<Map<String, Any>>>
    suspend fun getCommunityStats(userId: String): Result<Map<String, Any>>
}
