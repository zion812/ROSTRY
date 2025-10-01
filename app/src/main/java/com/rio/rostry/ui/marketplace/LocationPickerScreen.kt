package com.rio.rostry.ui.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rio.rostry.marketplace.location.LocationSearchService
import com.rio.rostry.marketplace.location.LocationService
import com.rio.rostry.utils.location.PlacesUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LocationPickerScreen(
    locationSearchService: LocationSearchService,
    locationService: LocationService = LocationService,
    deliveryCenterLat: Double? = null,
    deliveryCenterLng: Double? = null,
    deliveryRadiusMeters: Double = 50000.0,
    onLocationPicked: (lat: Double, lng: Double, address: String?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<PlacesUtils.SimplePlace>>(emptyList()) }
    var selected by remember { mutableStateOf<PlacesUtils.SimplePlace?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Location Picker", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                debounceJob?.cancel()
                debounceJob = scope.launch {
                    delay(250)
                    runCatching { locationSearchService.autocomplete(it) }
                        .onSuccess { suggestions = it }
                        .onFailure { error = it.message }
                }
            },
            label = { Text("Search address or place") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        if (suggestions.isNotEmpty()) {
            suggestions.take(5).forEach { p ->
                ElevatedCard(
                    onClick = {
                        p.id?.let { id ->
                            scope.launch {
                                runCatching { locationSearchService.getPlaceDetails(id) }
                                    .onSuccess { selected = it }
                                    .onFailure { error = it.message }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(p.name ?: p.formattedAddress ?: "Unknown")
                        if (p.formattedAddress != null) Text(p.formattedAddress!!, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (selected != null) {
            Spacer(Modifier.height(12.dp))
            val s = selected!!
            Text("Selected: ${s.name ?: s.formattedAddress}")
            val lat = s.lat
            val lng = s.lng
            if (lat != null && lng != null) {
                val within = if (deliveryCenterLat != null && deliveryCenterLng != null) {
                    locationService.withinRadius(lat, lng, deliveryCenterLat, deliveryCenterLng, deliveryRadiusMeters / 1000.0).withinServiceArea
                } else null
                if (within != null) {
                    Text(if (within) "Within delivery radius" else "Outside delivery radius")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onLocationPicked(lat, lng, s.formattedAddress) }) { Text("Use This Location") }
            }
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text("Error: ${error}", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Text("Note: Map preview not shown in this build. Add Google Maps Compose for visual map.", style = MaterialTheme.typography.bodySmall)
    }
}
