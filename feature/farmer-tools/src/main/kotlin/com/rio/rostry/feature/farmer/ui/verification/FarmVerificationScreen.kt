package com.rio.rostry.ui.farmer.verification

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.ui.components.LocationPicker
import com.google.android.libraries.places.api.net.PlacesClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmVerificationScreen(
    onNavigateUp: () -> Unit,
    placesClient: PlacesClient,
    viewModel: FarmVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showLocationPicker by remember { mutableStateOf(false) }

    if (showLocationPicker) {
        LocationPicker(
            placesClient = placesClient,
            onLocationSelected = { place ->
                place.latLng?.let { latLng ->
                    viewModel.onLocationSelected(latLng.latitude, latLng.longitude, null)
                }
                showLocationPicker = false
            },
            onCancel = { showLocationPicker = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Verification") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusBanner(uiState.currentStatus, uiState.rejectionReason)

            if (uiState.currentStatus == VerificationStatus.UNVERIFIED || uiState.currentStatus == VerificationStatus.REJECTED) {
                Text("Farm Address", style = MaterialTheme.typography.titleMedium)
                
                OutlinedTextField(
                    value = uiState.address["line1"] ?: "",
                    onValueChange = { viewModel.onAddressChanged("line1", it) },
                    label = { Text("Address Line 1") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.address["line2"] ?: "",
                    onValueChange = { viewModel.onAddressChanged("line2", it) },
                    label = { Text("Address Line 2 (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.address["city"] ?: "",
                        onValueChange = { viewModel.onAddressChanged("city", it) },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.address["postalCode"] ?: "",
                        onValueChange = { viewModel.onAddressChanged("postalCode", it) },
                        label = { Text("Postal Code") },
                        modifier = Modifier.weight(1f)
                    )
                }
                 Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.address["state"] ?: "",
                        onValueChange = { viewModel.onAddressChanged("state", it) },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.address["country"] ?: "",
                        onValueChange = { viewModel.onAddressChanged("country", it) },
                        label = { Text("Country") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text("Farm Location", style = MaterialTheme.typography.titleMedium)
                
                OutlinedCard(
                    onClick = { showLocationPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null)
                        Spacer(Modifier.width(8.dp))
                        Column {
                             if (uiState.location["lat"] != null) {
                                Text("Location Selected")
                                Text("Lat: ${uiState.location["lat"]}, Lng: ${uiState.location["lng"]}", style = MaterialTheme.typography.bodySmall)
                             } else {
                                Text("Tap to select farm location on map")
                             }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Proof Documents", style = MaterialTheme.typography.titleMedium)
                Text("Upload ownership proof, utility bill, or land records.", style = MaterialTheme.typography.bodySmall)

                val docLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    uri?.let { viewModel.onDocumentAdded(it) }
                }

                uiState.documentUris.forEach { uri ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(uri.takeLast(20), modifier = Modifier.weight(1f), maxLines = 1)
                        IconButton(onClick = { viewModel.onDocumentRemoved(uri) }) {
                            Icon(Icons.Default.Delete, "Remove")
                        }
                    }
                }
                
                Button(onClick = { docLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Add Document")
                }

                if (uiState.error != null) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { viewModel.submitVerification() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Submit Verification")
                    }
                }
            } else if (uiState.currentStatus == VerificationStatus.PENDING) {
                Text("Your verification is under review. This usually takes 24-48 hours.")
            } else if (uiState.currentStatus == VerificationStatus.VERIFIED) {
                Text("Congratulations! Your farm is verified.", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun StatusBanner(status: VerificationStatus, reason: String?) {
    val (color, text) = when(status) {
        VerificationStatus.UNVERIFIED -> MaterialTheme.colorScheme.surfaceVariant to "Not Verified"
        VerificationStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer to "Verification Pending"
        VerificationStatus.PENDING_UPGRADE -> MaterialTheme.colorScheme.tertiaryContainer to "Upgrade in Progress"
        VerificationStatus.VERIFIED -> MaterialTheme.colorScheme.tertiaryContainer to "Verified"
        VerificationStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to "Verification Rejected"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (status == VerificationStatus.REJECTED && reason != null) {
                Spacer(Modifier.height(4.dp))
                Text("Reason: $reason")
            }
        }
    }
}
