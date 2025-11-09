package com.rio.rostry.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddressManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAddress by remember { mutableStateOf<AddressManagementViewModel.AddressItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Address Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Address")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text("Loading addresses...", modifier = Modifier.padding(top = 16.dp))
            }
        } else if (uiState.addresses.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No addresses saved", style = MaterialTheme.typography.titleMedium)
                Text("Add your first delivery address", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Address")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.addresses) { address ->
                    AddressCard(
                        address = address,
                        onEdit = { editingAddress = address },
                        onDelete = { viewModel.deleteAddress(address.id) },
                        onSetDefault = { viewModel.setDefaultAddress(address.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddressFormDialog(
            address = null,
            onDismiss = { showAddDialog = false },
            onSave = { addr ->
                viewModel.addAddress(addr)
                showAddDialog = false
            }
        )
    }

    editingAddress?.let { addr ->
        AddressFormDialog(
            address = addr,
            onDismiss = { editingAddress = null },
            onSave = { updatedAddr ->
                viewModel.updateAddress(updatedAddr)
                editingAddress = null
            }
        )
    }
}

@Composable
private fun AddressCard(
    address: AddressManagementViewModel.AddressItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (address.isDefault) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (address.isDefault) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Default",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Default Address",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    Text(
                        "${address.street}, ${address.city}, ${address.state} - ${address.pincode}",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (address.landmark.isNotBlank()) {
                        Text(
                            "Landmark: ${address.landmark}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "Used in ${address.usageCount} orders",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (address.verificationStatus.isNotBlank()) {
                        Text(
                            "Status: ${address.verificationStatus}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (address.verificationStatus == "Verified") Color.Green else MaterialTheme.colorScheme.error
                        )
                    }
                }
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
            if (!address.isDefault) {
                OutlinedButton(
                    onClick = onSetDefault,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Set as Default")
                }
            }
        }
    }
}

@Composable
private fun AddressFormDialog(
    address: AddressManagementViewModel.AddressItem?,
    onDismiss: () -> Unit,
    onSave: (AddressManagementViewModel.AddressItem) -> Unit
) {
    var street by remember { mutableStateOf(address?.street ?: "") }
    var city by remember { mutableStateOf(address?.city ?: "") }
    var state by remember { mutableStateOf(address?.state ?: "") }
    var pincode by remember { mutableStateOf(address?.pincode ?: "") }
    var landmark by remember { mutableStateOf(address?.landmark ?: "") }
    var lat by remember { mutableStateOf(address?.lat?.toString() ?: "") }
    var lng by remember { mutableStateOf(address?.lng?.toString() ?: "") }
    var searchAddress by remember { mutableStateOf("") }
    var showMap by remember { mutableStateOf(false) }
    val markerState = remember { mutableStateOf<Marker?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            fetchCurrentLocation(
                onLocation = { latVal, lngVal ->
                    lat = latVal
                    lng = lngVal
                },
                onError = { msg ->
                    scope.launch {
                        // Show error, but since no snackbar here, perhaps Toast
                    }
                },
                context = context
            )
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
                        lat = latLng.latitude.toString()
                        lng = latLng.longitude.toString()
                    }
                    searchAddress = place.address ?: place.name.orEmpty()
                    // Parse address into fields if possible
                    // For simplicity, leave manual entry
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, BuildConfig.MAPS_API_KEY)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    // Validation
                    val pincodeRegex = Regex("^\\d{6}$")
                    if (street.isBlank() || city.isBlank() || state.isBlank() || !pincode.matches(pincodeRegex)) {
                        // Show error
                        return@Button
                    }
                    val addr = AddressManagementViewModel.AddressItem(
                        id = address?.id ?: "",
                        street = street,
                        city = city,
                        state = state,
                        pincode = pincode,
                        landmark = landmark,
                        lat = lat.toDoubleOrNull(),
                        lng = lng.toDoubleOrNull(),
                        isDefault = address?.isDefault ?: false,
                        usageCount = address?.usageCount ?: 0,
                        verificationStatus = address?.verificationStatus ?: ""
                    )
                    onSave(addr)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(if (address == null) "Add Address" else "Edit Address") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchAddress,
                    onValueChange = { searchAddress = it },
                    label = { Text("Search Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
                        autocompleteLauncher.launch(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick from Google Places")
                }
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = street.isBlank()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        isError = city.isBlank()
                    )
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f),
                        isError = state.isBlank()
                    )
                }
                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !pincode.matches(Regex("^\\d{6}$"))
                )
                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = { Text("Landmark (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Location Coordinates (Optional)", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lat,
                        onValueChange = { lat = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lng,
                        onValueChange = { lng = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        val status = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        if (status == PermissionChecker.PERMISSION_GRANTED) {
                            fetchCurrentLocation(
                                onLocation = { latVal, lngVal ->
                                    lat = latVal
                                    lng = lngVal
                                },
                                onError = { /* handle */ },
                                context = context
                            )
                        } else {
                            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Use Current Location")
                }
                if (showMap) {
                    // Simple map view for picking location
                    androidx.compose.foundation.layout.Box(modifier = Modifier.height(200.dp)) {
                        androidx.compose.ui.viewinterop.AndroidView(
                            factory = { ctx ->
                                MapsInitializer.initialize(ctx)
                                MapView(ctx).apply {
                                    onCreate(null)
                                    getMapAsync { map ->
                                        configureMap(map, lat, lng, markerState) { newLat, newLng ->
                                            lat = newLat
                                            lng = newLng
                                        }
                                    }
                                    onResume()
                                }
                            },
                            update = { mapView ->
                                mapView.getMapAsync { map ->
                                    updateMapMarker(map, lat, lng, markerState)
                                }
                            }
                        )
                    }
                }
                OutlinedButton(onClick = { showMap = !showMap }) {
                    Text(if (showMap) "Hide Map" else "Show Map for Location")
                }
            }
        }
    )
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