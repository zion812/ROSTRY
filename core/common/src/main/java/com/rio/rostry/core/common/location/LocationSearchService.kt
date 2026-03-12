package com.rio.rostry.core.common.location

/**
 * Domain interface for location search operations.
 *
 * Provides Places-powered autocomplete and place detail lookups
 * with graceful fallback when Places API is unavailable.
 */
interface LocationSearchService {

    /** Autocomplete search for places. */
    suspend fun autocomplete(query: String): List<Map<String, Any>>

    /** Get details for a specific place. */
    suspend fun getPlaceDetails(placeId: String): Map<String, Any>?

    /** Check if a point is within a radius of a center point. */
    fun withinRadiusWithFallback(
        lat: Double?, lng: Double?,
        centerLat: Double, centerLng: Double,
        radiusMeters: Double
    ): Boolean
}
