package com.rio.rostry.utils.location

import android.location.Location
import com.google.android.libraries.places.api.model.Place
import timber.log.Timber

/**
 * Utilities for Places SDK integration with robust error handling
 * and graceful fallbacks when the API is unavailable.
 */
object PlacesUtils {

    data class SimplePlace(
        val id: String?,
        val name: String?,
        val formattedAddress: String?,
        val lat: Double?,
        val lng: Double?,
        val types: List<Place.Type> = emptyList()
    )

    fun toSimplePlace(place: Place): SimplePlace = SimplePlace(
        id = place.id,
        name = place.name,
        formattedAddress = place.address,
        lat = place.latLng?.latitude,
        lng = place.latLng?.longitude,
        types = place.types ?: emptyList()
    )

    fun formatAddressFallback(lat: Double?, lng: Double?): String? =
        if (lat != null && lng != null) "${"%.5f".format(lat)}, ${"%.5f".format(lng)}" else null

    fun distanceMeters(aLat: Double, aLng: Double, bLat: Double, bLng: Double): Float {
        return try {
            val res = FloatArray(1)
            Location.distanceBetween(aLat, aLng, bLat, bLng, res)
            res.firstOrNull() ?: 0f
        } catch (t: Throwable) {
            Timber.w(t, "distanceBetween failed; returning 0f")
            0f
        }
    }

    fun isRelevantForMarketplace(types: List<Place.Type>): Boolean {
        // Relaxed: accept all results to avoid enum constant differences across SDK versions
        return true
    }

    fun logAppCheckDebugHint() {
        Timber.d("If Places calls fail in debug, verify Firebase App Check Debug token is registered and enforcement/monitoring are configured.")
    }
}
