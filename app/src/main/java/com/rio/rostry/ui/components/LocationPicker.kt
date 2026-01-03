package com.rio.rostry.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

private const val TAG = "LocationPicker"
private val DEFAULT_LOCATION = LatLng(20.5937, 78.9629)

@Composable
fun LocationPicker(
    modifier: Modifier = Modifier,
    placesClient: PlacesClient,
    onLocationSelected: (Place) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var searchQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<com.google.android.libraries.places.api.model.AutocompletePrediction>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    
    // The location currently under the center pin
    var centerLocationName by remember { mutableStateOf("Move map to select location") }
    var centerLocationAddress by remember { mutableStateOf("") }
    var currentAddressObject by remember { mutableStateOf<android.location.Address?>(null) }
    var isGeocoding by remember { mutableStateOf(false) }
    
    var geocodeJob by remember { mutableStateOf<Job?>(null) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, 4f)
    }

    // Permission launcher for current location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locateDeviceInternal(placesClient, context) { place ->
                place?.latLng?.let {
                    coroutineScope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }
                }
            }
        } else {
             coroutineScope.launch { snackbarHostState.showSnackbar("Location permission denied") }
        }
    }

    fun getPlacePredictions(query: String) {
        isSearching = true
        searchError = null
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                predictions = response.autocompletePredictions
                isSearching = false
                if (predictions.isEmpty()) {
                    Log.d(TAG, "No predictions found for query: $query")
                }
            }
            .addOnFailureListener { exception ->
                isSearching = false
                predictions = emptyList()
                val errorMessage = when {
                    exception.message?.contains("API_NOT_CONNECTED") == true -> 
                        "Places API not configured. Check API key."
                    exception.message?.contains("NETWORK") == true -> 
                        "Network error. Check internet connection."
                    exception.message?.contains("REQUEST_DENIED") == true ->
                        "API key invalid or Places API not enabled."
                    else -> "Search failed: ${exception.message}"
                }
                searchError = errorMessage
                Log.e(TAG, "Prediction failed: ${exception.message}", exception)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
    }

    fun fetchPlaceDetailsAndMove(placeId: String) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        coroutineScope.launch {
            try {
                val response = placesClient.fetchPlace(request).await()
                val place = response.place
                place.latLng?.let {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 17f))
                }
                searchQuery = place.address ?: ""
                centerLocationName = place.name ?: "Selected Location"
                centerLocationAddress = place.address ?: ""
                currentAddressObject = null 
                predictions = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Place not found", e)
                snackbarHostState.showSnackbar("Failed to fetch place details")
            }
        }
    }
    
    // Reverse Geocoding Logic
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            geocodeJob?.cancel()
            geocodeJob = launch {
                delay(500) // Debounce
                isGeocoding = true
                val center = cameraPositionState.position.target
                try {
                    val address = getAddressFromLocation(context, center)
                    currentAddressObject = address
                    centerLocationName = if (address.featureName != null && address.featureName != address.getAddressLine(0)) address.featureName else "Pinned Location"
                    centerLocationAddress = address.getAddressLine(0) ?: "Lat: ${center.latitude}, Lng: ${center.longitude}"
                } catch (e: Exception) {
                    currentAddressObject = null
                    centerLocationName = "Pinned Location"
                    centerLocationAddress = "${center.latitude}, ${center.longitude}"
                } finally {
                    isGeocoding = false
                }
            }
        } else {
            if (!isGeocoding) {
                 centerLocationName = "Locating..."
                 centerLocationAddress = "..."
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Google Map view
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
        )
        
        // Center Pin
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Center Pin",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
                .offset(y = (-24).dp) // Offset to make the bottom of the pin point effectively to center
        )

        // Search UI on top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(12.dp)
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.length > 2) {
                            getPlacePredictions(it)
                        } else {
                            predictions = emptyList()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search farm or address") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                predictions = emptyList()
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Loading indicator
            if (isSearching) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Searching...")
                    }
                }
            }

            // Error message
            if (searchError != null && !isSearching) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = searchError ?: "",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Autocomplete Predictions
            AnimatedVisibility(visible = predictions.isNotEmpty() && !isSearching) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .heightIn(max = 200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LazyColumn {
                        items(predictions) { prediction ->
                            Column(
                                modifier = Modifier
                                    .clickable { fetchPlaceDetailsAndMove(prediction.placeId) }
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = prediction.getPrimaryText(null).toString(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = prediction.getSecondaryText(null).toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // Bottom Details & Confirm
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(centerLocationName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(centerLocationAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                             val target = cameraPositionState.position.target
                             try {
                                 val addressObj = currentAddressObject
                                 val placeBuilder = Place.builder()
                                     .setLatLng(target)
                                     .setName(centerLocationName)
                                     .setAddress(centerLocationAddress)
                                     
                                 if (addressObj != null) {
                                     val components = mutableListOf<AddressComponent>()
                                     addressObj.locality?.let { components.add(AddressComponent.builder(it, listOf("locality")).build()) }
                                     addressObj.adminArea?.let { components.add(AddressComponent.builder(it, listOf("administrative_area_level_1")).build()) }
                                     addressObj.countryName?.let { components.add(AddressComponent.builder(it, listOf("country")).build()) }
                                     addressObj.postalCode?.let { components.add(AddressComponent.builder(it, listOf("postal_code")).build()) }
                                     // Add thoroughfare if available (Route)
                                     addressObj.thoroughfare?.let { components.add(AddressComponent.builder(it, listOf("route")).build()) }
                                     addressObj.subThoroughfare?.let { components.add(AddressComponent.builder(it, listOf("street_number")).build()) }
                                     
                                     placeBuilder.setAddressComponents(com.google.android.libraries.places.api.model.AddressComponents.newInstance(components))
                                 }
                                 
                                 onLocationSelected(placeBuilder.build())
                             } catch (e: Exception) {
                                 Log.e(TAG, "Error building place", e)
                                 // Fallback
                                 locateDeviceInternal(placesClient, context) {
                                     if (it != null) onLocationSelected(it)
                                     else onCancel() 
                                 }
                             }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm Location")
                    }
                }
            }
        }

        // My Location FAB
        FloatingActionButton(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locateDeviceInternal(placesClient, context) { place ->
                        place?.latLng?.let {
                            coroutineScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 17f))
                            }
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 180.dp), // Position above bottom card
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Default.MyLocation, "My Location")
        }
        
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@SuppressLint("MissingPermission")
private fun locateDeviceInternal(
    placesClient: PlacesClient,
    context: Context,
    onResult: (Place?) -> Unit
) {
    val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
    val request = FindCurrentPlaceRequest.newInstance(placeFields)
    
    placesClient.findCurrentPlace(request)
        .addOnSuccessListener { response ->
            val placeLikelihood = response.placeLikelihoods.maxByOrNull { it.likelihood }
            onResult(placeLikelihood?.place)
        }
        .addOnFailureListener {
            onResult(null)
        }
}

private suspend fun getAddressFromLocation(context: Context, latLng: LatLng): android.location.Address = withContext(Dispatchers.IO) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0]
        } else {
            android.location.Address(Locale.getDefault()).apply {
                latitude = latLng.latitude
                longitude = latLng.longitude
                featureName = "Unknown Location"
            }
        }
    } catch (e: Exception) {
        throw e
    }
}