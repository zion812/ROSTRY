package com.rio.rostry.marketplace.location

import com.rio.rostry.utils.ValidationUtils
import timber.log.Timber

/**
 * Location utilities for validation, delivery radius, and hub assignment.
 */
object LocationService {
    data class RadiusResult(val withinServiceArea: Boolean, val distanceKm: Double?)

    fun withinRadius(productLat: Double?, productLng: Double?, buyerLat: Double?, buyerLng: Double?, maxKm: Double = 50.0): RadiusResult {
        if (productLat == null || productLng == null || buyerLat == null || buyerLng == null) return RadiusResult(false, null)
        val dist = ValidationUtils.distanceKm(productLat, productLng, buyerLat, buyerLng)
        return RadiusResult(dist <= maxKm, dist)
    }

    // Placeholder: decide hub assignment based on lat/lng regions.
    fun assignHub(lat: Double?, lng: Double?): String? {
        lat ?: return null
        lng ?: return null
        return when {
            lat > 20 && lng > 75 -> "Hub-East"
            lat > 20 && lng <= 75 -> "Hub-North"
            lat <= 20 && lng > 75 -> "Hub-SouthEast"
            else -> "Hub-SouthWest"
        }
    }

    // --- Enhancement: Places integration points and fallbacks ---

    /**
     * Validate coordinates against Places metadata when available. Fallback returns true.
     */
    fun validateAgainstPlacesFallback(lat: Double?, lng: Double?, formattedAddress: String?): Boolean {
        Timber.d("validateAgainstPlacesFallback lat=%s, lng=%s, addr=%s", lat, lng, formattedAddress)
        return true
    }

    /**
     * Format address string when Places API is unavailable.
     */
    fun formatAddressFallback(lat: Double?, lng: Double?): String? =
        if (lat != null && lng != null) "${"%.5f".format(lat)}, ${"%.5f".format(lng)}" else null
}

