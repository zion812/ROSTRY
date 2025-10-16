package com.rio.rostry.ui.verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rio.rostry.BuildConfig
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FarmerLocationVerificationScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val latState = remember { mutableStateOf("") }

    val lngState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val markerState = remember { mutableStateOf<Marker?>(null) }

    val farmPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.uploadImage(it.toString(), "FARM_PHOTO") }
    }

    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.uploadDocument(it.toString(), "LAND_OWNERSHIP") }
    }

    // Initialize Places once
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, BuildConfig.MAPS_API_KEY)
        }
    }
    
    // Permission launcher for fine location
    val locationPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            fetchCurrentLocation(
                onLocation = { lat, lng ->
                    latState.value = lat
                    lngState.value = lng
                },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                context = context
            )
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                try {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    val latLng = place.latLng
                    if (latLng != null) {
                        latState.value = latLng.latitude.toString()
                        lngState.value = latLng.longitude.toString()
                    }
                    addressState.value = place.address ?: place.name.orEmpty()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error retrieving place: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No place data returned.", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (result.resultCode == android.app.Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Place selection cancelled.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to pick location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Farmer Location Verification")
        Text("Enter approximate farm location (lat/lng)")
        Text("Tap on the map to set location", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = addressState.value,
            onValueChange = { addressState.value = it },
            label = { Text("Search or address (optional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
                autocompleteLauncher.launch(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Pick location on Google")
        }
        Button(
            onClick = {
                val status = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                if (status == PermissionChecker.PERMISSION_GRANTED) {
                    fetchCurrentLocation(
                        onLocation = { lat, lng ->
                            latState.value = lat
                            lngState.value = lng
                        },
                        onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                        context = context
                    )
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Use current location")
        }
        OutlinedButton(onClick = {
            latState.value = ""
            lngState.value = ""
            markerState.value?.remove()
            markerState.value = null
        }, modifier = Modifier.padding(top = 4.dp)) { Text("Clear location") }
        // Embedded Google Map
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(top = 8.dp),
            factory = { ctx ->
                MapsInitializer.initialize(ctx)
                MapView(ctx).apply {
                    onCreate(null)
                    getMapAsync { map ->
                        configureMap(map, latState.value, lngState.value, markerState) { newLat, newLng ->
                            latState.value = newLat
                            lngState.value = newLng
                        }
                    }
                    onResume()
                }
            },
            update = { mapView ->
                mapView.getMapAsync { map ->
                    updateMapMarker(map, latState.value, lngState.value, markerState)
                }
            }
        )
        OutlinedTextField(value = latState.value, onValueChange = { latState.value = it }, label = { Text("Latitude") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = lngState.value, onValueChange = { lngState.value = it }, label = { Text("Longitude") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        
        Text("Upload Farm Verification Documents", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Text("Upload clear photos of your farm and poultry", style = MaterialTheme.typography.bodySmall)
        Text("Accepted formats: JPG, PNG, PDF (max 5MB per file)", style = MaterialTheme.typography.bodySmall)
        Text("Land ownership documents help speed up verification", style = MaterialTheme.typography.bodySmall)
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            OutlinedButton(
                onClick = { farmPhotoPicker.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Upload Farm Photo")
            }
            OutlinedButton(
                onClick = { documentPicker.launch("*/*") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Upload Land Proof")
            }
        }
        
        // Upload Progress
        ui.uploadProgress.forEach { (path, progress) ->
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(path.substringAfterLast("/"), style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator(progress = progress / 100f, modifier = Modifier.fillMaxWidth())
                    Text("$progress%")
                }
            }
        }
        
        // Uploaded Images with Thumbnails
        if (ui.uploadedImages.isNotEmpty()) {
            Text("Uploaded Farm Photos (${ui.uploadedImages.size})", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                items(ui.uploadedImages) { img ->
                    Card {
                        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = img,
                                contentDescription = "Farm photo",
                                modifier = Modifier.size(80.dp)
                            )
                            IconButton(onClick = { viewModel.removeUploadedFile(img, false) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }

        // EXIF Warnings
        if (ui.exifWarnings.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Photo location warnings", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
                    ui.exifWarnings.forEach { w ->
                        Text("• $w", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Tip: Take photos at your farm location to speed up verification.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Uploaded Documents
        if (ui.uploadedDocuments.isNotEmpty()) {
            Text("Uploaded Documents (${ui.uploadedDocuments.size})", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            ui.uploadedDocuments.forEach { doc ->
                Card(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Row(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(doc.substringAfterLast('/'), modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.removeUploadedFile(doc, true) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }

        // Upload Error
        ui.uploadError?.let { error ->
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Upload Error", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                    Text(error)
                    Button(onClick = { viewModel.clearUploadError() }) {
                        Text("Retry")
                    }
                }
            }
        }

        // Validation errors
        if (ui.validationErrors.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Please fix the following:", color = MaterialTheme.colorScheme.error)
                    ui.validationErrors.forEach { (field, msg) ->
                        Text("• $field: $msg", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Button(
            onClick = {
                val lat = latState.value.toDoubleOrNull()
                val lng = lngState.value.toDoubleOrNull()
                if (lat != null && lng != null && ui.uploadedImages.isNotEmpty()) {
                    viewModel.submitKycWithDocuments(passedLat = lat, passedLng = lng)
                }
            },
            enabled = !ui.isSubmitting && latState.value.toDoubleOrNull() != null && lngState.value.toDoubleOrNull() != null && ui.uploadedImages.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            if (ui.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                Text("  Submitting...")
            } else {
                Text("Submit Location & Documents")
            }
        }
        if (latState.value.toDoubleOrNull() == null || lngState.value.toDoubleOrNull() == null || ui.uploadedImages.isEmpty()) {
            Text(
                "Please set location and upload at least one farm photo for verification",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        ui.error?.let { Text("Error: ${'$'}it", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.error) }
        ui.message?.let { Text(it, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.primary) }

        // Success dialog & navigation
        if (ui.submissionSuccess) {
            AlertDialog(
                onDismissRequest = { onDone() },
                confirmButton = {
                    Button(onClick = { onDone() }) { Text("OK") }
                },
                title = { Text("Submission received") },
                text = { Text("Your verification documents have been submitted. We'll review them within 24-48 hours.") }
            )
        }
    }
}


@SuppressLint("MissingPermission")
private fun fetchCurrentLocation(
    onLocation: (lat: String, lng: String) -> Unit,
    onError: (String) -> Unit,
    context: android.content.Context
) {
    try {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onLocation(loc.latitude.toString(), loc.longitude.toString())
                } else {
                    client.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                        .addOnSuccessListener { cur ->
                            if (cur != null) onLocation(cur.latitude.toString(), cur.longitude.toString())
                            else onError("Unable to get current location")
                        }
                        .addOnFailureListener { onError(it.message ?: "Location error") }
                }
            }
            .addOnFailureListener { onError(it.message ?: "Location error") }
    } catch (t: Throwable) {
        onError(t.message ?: "Location error")
    }
}

private fun configureMap(
    map: GoogleMap,
    lat: String,
    lng: String,
    markerState: androidx.compose.runtime.MutableState<Marker?>,
    onPick: (lat: String, lng: String) -> Unit
) {
    map.uiSettings.isZoomControlsEnabled = true
    map.uiSettings.isMyLocationButtonEnabled = true
    try {
        map.isMyLocationEnabled = true
    } catch (_: SecurityException) { /* ignore if not granted */ }

    map.setOnMapClickListener { ll ->
        onPick(ll.latitude.toString(), ll.longitude.toString())
        updateMapMarker(map, ll.latitude.toString(), ll.longitude.toString(), markerState)
    }

    updateMapMarker(map, lat, lng, markerState)
}

private fun updateMapMarker(
    map: GoogleMap,
    lat: String,
    lng: String,
    markerState: androidx.compose.runtime.MutableState<Marker?>
) {
    val dLat = lat.toDoubleOrNull()
    val dLng = lng.toDoubleOrNull()
    if (dLat != null && dLng != null) {
        val pos = LatLng(dLat, dLng)
        markerState.value?.remove()
        markerState.value = map.addMarker(MarkerOptions().position(pos).title("Selected location"))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14f))
    }
}

