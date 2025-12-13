package com.rio.rostry.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "LocationPicker"

// A default location (India) to center the map initially
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
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, 4f)
    }

    // Permission launcher for current location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, fetch location
            getCurrentLocation(placesClient, context) { place ->
                selectedPlace = place
                place?.latLng?.let {
                    coroutineScope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }
                }
                searchQuery = place?.address ?: ""
            }
        } else {
             coroutineScope.launch { snackbarHostState.showSnackbar("Location permission denied") }
        }
    }

    fun getPlacePredictions(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                predictions = response.autocompletePredictions
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Prediction failed", exception)
            }
    }

    fun fetchPlaceDetails(placeId: String) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        coroutineScope.launch {
            try {
                val response = placesClient.fetchPlace(request).await()
                selectedPlace = response.place
                response.place.latLng?.let {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                }
                searchQuery = response.place.address ?: ""
                predictions = emptyList() // Clear predictions after selection
            } catch (e: Exception) {
                Log.e(TAG, "Place not found", e)
                snackbarHostState.showSnackbar("Failed to fetch place details")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Google Map view
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            selectedPlace?.latLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = selectedPlace?.name,
                    snippet = selectedPlace?.address
                )
            }
        }

        // Search UI on top of the map
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(12.dp)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.length > 2) {
                        getPlacePredictions(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search for your farm or address") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            predictions = emptyList()
                            selectedPlace = null
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            // Autocomplete Predictions
            if (predictions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    items(predictions) { prediction ->
                        Column(
                            modifier = Modifier
                                .clickable { fetchPlaceDetails(prediction.placeId) }
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
                        Divider()
                    }
                }
            }
        }

        // Current Location Button
        FloatingActionButton(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    isLocating = true
                    getCurrentLocation(placesClient, context) { place ->
                         isLocating = false
                         if (place != null) {
                             selectedPlace = place
                             place.latLng?.let {
                                 coroutineScope.launch {
                                     cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
                                 }
                             }
                             searchQuery = place.address ?: ""
                         } else {
                             coroutineScope.launch { snackbarHostState.showSnackbar("Could not detect current location") }
                         }
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            if (isLocating) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
            } else {
                Icon(Icons.Default.MyLocation, contentDescription = "Use Current Location")
            }
        }

        // Confirmation Button
        if (selectedPlace != null) {
            FloatingActionButton(
                onClick = { selectedPlace?.let { onLocationSelected(it) } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirm Location")
            }
        }
        
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    placesClient: PlacesClient,
    context: Context,
    onResult: (Place?) -> Unit
) {
    val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
    val request = FindCurrentPlaceRequest.newInstance(placeFields)
    
    placesClient.findCurrentPlace(request)
        .addOnSuccessListener { response ->
            // Use the most likely place
            val placeLikelihood = response.placeLikelihoods.maxByOrNull { it.likelihood }
            onResult(placeLikelihood?.place)
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "FindCurrentPlace failed", exception)
            onResult(null)
        }
}