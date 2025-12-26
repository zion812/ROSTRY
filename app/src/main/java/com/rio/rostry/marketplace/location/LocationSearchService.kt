package com.rio.rostry.marketplace.location

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.rio.rostry.utils.location.PlacesUtils
import kotlinx.coroutines.tasks.await
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
            // Updated: Using real Places API
            val request = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()
            
            val response = places.findAutocompletePredictions(request).await()
            response.autocompletePredictions.map { prediction ->
                PlacesUtils.SimplePlace(
                    id = prediction.placeId,
                    name = prediction.getPrimaryText(null).toString(),
                    formattedAddress = prediction.getFullText(null).toString(), // Use full text for address context
                    lat = null, // Not available in autocomplete
                    lng = null, // Not available in autocomplete
                    types = prediction.placeTypes.map { com.google.android.libraries.places.api.model.Place.Type.valueOf(it.name) }
                )
            }
        }.onFailure { e ->
            Timber.w(e, "Places autocomplete failed; returning empty list")
            PlacesUtils.logAppCheckDebugHint()
        }.getOrDefault(emptyList())

    suspend fun getPlaceDetails(placeId: String): PlacesUtils.SimplePlace? =
        runCatching<PlacesUtils.SimplePlace?> {
            // Updated: Using real Places API with specific fields to save costs/latency
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES
            )
            val request = com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(placeId, placeFields)
                .build()
                
            val response = places.fetchPlace(request).await()
            PlacesUtils.toSimplePlace(response.place)
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
