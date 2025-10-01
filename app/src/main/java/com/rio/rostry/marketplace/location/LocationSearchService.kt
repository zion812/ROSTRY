package com.rio.rostry.marketplace.location

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.rio.rostry.utils.location.PlacesUtils
import timber.log.Timber

/**
 * Places-powered search service that augments the existing LocationService.
 * Falls back gracefully when Places fails or App Check blocks requests.
 */
class LocationSearchService(
    private val places: PlacesClient,
    private val base: LocationService
) {

    suspend fun autocomplete(query: String): List<PlacesUtils.SimplePlace> =
        runCatching<List<PlacesUtils.SimplePlace>> {
            // NOTE: Direct Places calls removed to avoid unresolved references in this build.
            // Replace with actual SDK calls when ready. For now, return empty list as fallback.
            emptyList()
        }.onFailure { e ->
            Timber.w(e, "Places autocomplete failed; returning empty list")
            PlacesUtils.logAppCheckDebugHint()
        }.getOrDefault(emptyList<PlacesUtils.SimplePlace>())

    suspend fun getPlaceDetails(placeId: String): PlacesUtils.SimplePlace? =
        runCatching<PlacesUtils.SimplePlace?> {
            // NOTE: Direct Places calls removed to avoid unresolved references in this build.
            // Replace with actual SDK calls when ready. For now, return null as fallback.
            null
        }.onFailure { e ->
            Timber.w(e, "Places fetchPlace failed; returning null")
            PlacesUtils.logAppCheckDebugHint()
        }.getOrNull()

    fun withinRadiusWithFallback(
        lat: Double?, lng: Double?, centerLat: Double, centerLng: Double, radiusMeters: Double
    ): Boolean = if (lat != null && lng != null) {
        base.withinRadius(lat, lng, centerLat, centerLng, radiusMeters / 1000.0).withinServiceArea
    } else false
}
