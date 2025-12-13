package com.rio.rostry.ui.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.ui.components.LocationPicker
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.BuildConfig

@Composable
fun FarmerLocationVerificationScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsState()
    val placesClient = viewModel.placesClient
    
    // Status Logic
    when (uiState.user?.verificationStatus) {
        VerificationStatus.VERIFIED -> {
            VerifiedStateScreen(onDone)
            return
        }
        VerificationStatus.PENDING -> {
            PendingStateScreen(
                onDone = onDone, 
                uiState = uiState,
                onViewDetails = { /* Optional: Expand details logic if needed internally in screen */ }
            )
            return
        }
        else -> { } // Continue to Form
    }

    // Main Form Logic
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.showLocationPicker) {
            LocationPicker(
                placesClient = placesClient,
                onLocationSelected = { place -> viewModel.onPlaceSelected(place) },
                onCancel = {
                    if (uiState.selectedPlace != null) {
                        viewModel.changeLocation() // Close picker
                    } else {
                        onDone()
                    }
                }
            )
        } else {
            ConfirmationView(
                viewModel = viewModel,
                uiState = uiState,
                onDone = onDone
            )
        }

        // Success Dialog Overlay
        if (uiState.submissionSuccess) {
            AlertDialog(
                onDismissRequest = { onDone() },
                confirmButton = { Button(onClick = { onDone() }) { Text("Done") } },
                icon = { Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary) },
                title = { Text("Submission Received") },
                text = { Text("Your documents are under review. We will notify you once verification is complete.") }
            )
        }
    }
}

@Composable
private fun VerifiedStateScreen(onDone: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.CheckCircle, "Verified", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
            Text("Verification Complete", style = MaterialTheme.typography.headlineMedium)
            Text("Your farm location is verified.", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onDone) { Text("Done") }
        }
    }
}

@Composable
private fun PendingStateScreen(
    onDone: () -> Unit,
    uiState: VerificationViewModel.UiState,
    onViewDetails: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }

    if (showDetails) {
        // Read-only view of submitted data
         Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Submitted Details", style = MaterialTheme.typography.headlineSmall)
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Location Submitted", style = MaterialTheme.typography.titleMedium)
                    }
                    if (uiState.user?.farmLocationLat != null) {
                        Text("Coordinates: ${uiState.user?.farmLocationLat}, ${uiState.user?.farmLocationLng}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Text("Documents", style = MaterialTheme.typography.titleMedium)
            // Show uploaded images/docs (read-only)
             LazyColumn(
                 modifier = Modifier.height(200.dp),
                 verticalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                 items(uiState.uploadedImages) { url ->
                     UploadedItem(url = url, onDelete = {}, isImage = true, readOnly = true)
                 }
                 items(uiState.uploadedDocuments) { url ->
                     UploadedItem(url = url, onDelete = {}, isImage = false, readOnly = true)
                 }
             }

             Button(onClick = { showDetails = false }, modifier = Modifier.fillMaxWidth()) {
                 Text("Close Details")
             }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(Icons.Default.HourglassEmpty, "Pending", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                Text("Verification Pending", style = MaterialTheme.typography.headlineSmall)
                Text("Your documents are under review.", style = MaterialTheme.typography.bodyMedium)
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { showDetails = true }) { Text("View Details") }
                    Button(onClick = onDone) { Text("Done") }
                }
            }
        }
    }
}

@Composable
private fun ConfirmationView(
    viewModel: VerificationViewModel,
    uiState: VerificationViewModel.UiState,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val selectedPlace = uiState.selectedPlace
    val upgradeType = uiState.upgradeType

    // Track which type is being uploaded
    var currentUploadType by remember { mutableStateOf<String?>(null) }
    var currentUploadIsImage by remember { mutableStateOf(true) }

    val contentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { 
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            
            if (upgradeType != null && currentUploadType != null) {
                if (currentUploadIsImage) {
                    viewModel.uploadImage(it.toString(), currentUploadType!!, upgradeType)
                } else {
                    viewModel.uploadDocument(it.toString(), currentUploadType!!, upgradeType)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Header
        Column {
            Text("Verification Request", style = MaterialTheme.typography.headlineSmall)
            Text(upgradeType?.description ?: "Complete standard verification", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (upgradeType == null) {
             Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Error: Unable to determine verification type.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        // Status Banner (Rejected)
        if (uiState.user?.verificationStatus == VerificationStatus.REJECTED) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Verification Rejected", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                    uiState.user?.kycRejectionReason?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }

        // 1. Location Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Farm Location", style = MaterialTheme.typography.titleMedium)
                }
                
                Text(selectedPlace?.name ?: "Your Location", style = MaterialTheme.typography.titleLarge)
                Text(selectedPlace?.address ?: "Address not selected", style = MaterialTheme.typography.bodyMedium)
                
                OutlinedButton(
                    onClick = { viewModel.changeLocation() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Location")
                }
            }
        }

        // 2. Photos Section
        if (upgradeType != null && upgradeType.requiredImages.isNotEmpty()) {
            Text("Required Photos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    upgradeType.requiredImages.forEach { imageType ->
                        // Check if uploaded
                        val uploadedUrl = uiState.uploadedImages.find { url -> 
                            uiState.uploadedImageTypes[url] == imageType 
                        } // simplified matching based on map content from VM
                        
                        UploadRow(
                            label = formatLabel(imageType),
                            isUploaded = uploadedUrl != null,
                            uploadUrl = uploadedUrl,
                            onUpload = {
                                currentUploadType = imageType
                                currentUploadIsImage = true
                                contentLauncher.launch(arrayOf("image/*"))
                            },
                            onDelete = {
                                if (uploadedUrl != null) viewModel.removeUploadedFile(uploadedUrl, false)
                            },
                            isImage = true
                        )
                    }
                }
            }
        }

        // 3. Documents Section
        if (upgradeType != null && upgradeType.requiredDocuments.isNotEmpty()) {
            Text("Required Documents", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    upgradeType.requiredDocuments.forEach { docType ->
                        val uploadedUrl = uiState.uploadedDocuments.find { url ->
                             uiState.uploadedDocTypes[url] == docType
                        }
                        
                        UploadRow(
                            label = formatLabel(docType),
                            isUploaded = uploadedUrl != null,
                            uploadUrl = uploadedUrl,
                            onUpload = {
                                currentUploadType = docType
                                currentUploadIsImage = false
                                contentLauncher.launch(arrayOf("application/pdf", "image/*"))
                            },
                            onDelete = {
                                if (uploadedUrl != null) viewModel.removeUploadedFile(uploadedUrl, true)
                            },
                            isImage = false
                        )
                    }
                }
            }
        }
        
        // Progress Bar
        if (uiState.uploadProgress.isNotEmpty()) {
             LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
             Text("Uploading...", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.End))
        }

        // Errors
        if (uiState.uploadError != null) {
            Text(uiState.uploadError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        if (uiState.error != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                 Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                     Spacer(Modifier.width(8.dp))
                     Text(uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer)
                 }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = { 
                if (upgradeType != null) {
                    viewModel.submitKycWithDocuments(upgradeType) 
                }
            },
            enabled = !uiState.isSubmitting && upgradeType != null && isAllRequiredUploaded(upgradeType, uiState),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Submitting...")
            } else {
                Text("Submit Verification")
            }
        }
        
        Spacer(Modifier.height(32.dp))
    }
}

// Helper to check if all requirements met locally for button enable state
private fun isAllRequiredUploaded(type: UpgradeType, state: VerificationViewModel.UiState): Boolean {
    val imagesOk = type.requiredImages.all { req -> state.uploadedImages.any { state.uploadedImageTypes[it] == req } }
    val docsOk = type.requiredDocuments.all { req -> state.uploadedDocuments.any { state.uploadedDocTypes[it] == req } }
    return imagesOk && docsOk
}

private fun formatLabel(type: String): String {
    return type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
fun UploadRow(
    label: String,
    isUploaded: Boolean,
    uploadUrl: String?,
    onUpload: () -> Unit,
    onDelete: () -> Unit,
    isImage: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (isUploaded) {
                Text("✓ Uploaded", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            } else {
                Text("Required", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
        
        if (isUploaded && uploadUrl != null) {
            UploadedItem(url = uploadUrl, onDelete = onDelete, isImage = isImage)
        } else {
            OutlinedButton(onClick = onUpload, shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Upload")
            }
        }
    }
}

@Composable
fun UploadedItem(url: String, onDelete: () -> Unit, isImage: Boolean, readOnly: Boolean = false) {
    var imageLoadError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
        if (isImage) {
             if (!imageLoadError) {
                AsyncImage(
                    model = LocalContext.current.let {
                        coil.request.ImageRequest.Builder(it)
                            .data(url)
                            .crossfade(true)
                            .build()
                    },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    onError = { imageLoadError = true },
                    onSuccess = { imageLoadError = false }
                )
             } else {
                 Icon(Icons.Default.Image, "Image error", modifier = Modifier.align(Alignment.Center), tint = MaterialTheme.colorScheme.error)
             }
        } else {
            Icon(Icons.Default.Description, null, modifier = Modifier.align(Alignment.Center))
        }
        
        if (!readOnly) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd).size(20.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
    }
}
