package com.rio.rostry.ui.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.rio.rostry.R
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.FarmLocation
import com.rio.rostry.ui.components.LocationPicker
import com.rio.rostry.ui.verification.components.PendingStateScreen
import com.rio.rostry.ui.verification.components.UploadRow
import com.rio.rostry.ui.verification.components.VerifiedStateScreen

@Composable
fun FarmerLocationVerificationScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsState()
    val placesClient = viewModel.placesClient
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.user?.verificationStatus) {
            VerificationStatus.VERIFIED -> {
                VerifiedStateScreen(onDone)
            }
            VerificationStatus.PENDING -> {
                PendingStateScreen(
                    onDone = onDone, 
                    uiState = uiState
                )
            }
            else -> { 
                if (uiState.showLocationPicker) {
                    LocationPicker(
                        placesClient = placesClient,
                        onLocationSelected = { place -> viewModel.onPlaceSelected(place) },
                        onCancel = {
                            if (uiState.selectedPlace != null) {
                                viewModel.cancelLocationChange()
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
            }
        }

        if (uiState.submissionSuccess) {
            AlertDialog(
                onDismissRequest = { onDone() },
                confirmButton = { Button(onClick = { onDone() }) { Text(stringResource(R.string.done)) } },
                icon = { Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary) },
                title = { Text(stringResource(R.string.submission_received)) },
                text = { Text(stringResource(R.string.submission_received_msg)) }
            )
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

    var currentUploadType by rememberSaveable { mutableStateOf<String?>(null) }
    var currentUploadIsImage by rememberSaveable { mutableStateOf(true) }

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
        
        Column {
            Text(stringResource(R.string.verification_request), style = MaterialTheme.typography.headlineSmall)
            Text(upgradeType?.description ?: stringResource(R.string.complete_standard_verification), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (upgradeType == null) {
             Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(stringResource(R.string.error_unable_to_determine_type), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        if (uiState.user?.verificationStatus == VerificationStatus.REJECTED) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(stringResource(R.string.verification_rejected), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
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
                    Text(stringResource(R.string.farm_location), style = MaterialTheme.typography.titleMedium)
                }
                
                Text(selectedPlace?.name ?: stringResource(R.string.your_location), style = MaterialTheme.typography.titleLarge)
                Text(selectedPlace?.address ?: stringResource(R.string.address_not_selected), style = MaterialTheme.typography.bodyMedium)
                
                OutlinedButton(
                    onClick = { viewModel.changeLocation() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.change_location))
                }
            }
        }

        if (uiState.exifWarnings.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.photo_location_warning), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                    Spacer(Modifier.height(4.dp))
                    uiState.exifWarnings.forEach { warning ->
                        Text("• $warning", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }
        }

        // 2. Photos Section
        if (uiState.requiredImages.isNotEmpty()) {
            Text(stringResource(R.string.required_photos), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    uiState.requiredImages.forEach { imageType ->
                        key(imageType) {
                            val uploadedUrl = uiState.uploadedImages.find { url -> 
                                uiState.uploadedImageTypes[url] == imageType 
                            }
                        
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
        }

        // 3. Documents Section
        if (uiState.requiredDocuments.isNotEmpty()) {
            Text(stringResource(R.string.required_documents), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    uiState.requiredDocuments.forEach { docType ->
                        key(docType) {
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
        }
        
        if (uiState.uploadProgress.isNotEmpty()) {
             LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
             Text(stringResource(R.string.uploading_progress), style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.End))
        }

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

        if (upgradeType != null) {
            val missingCount = countMissingRequirements(uiState)
            if (missingCount > 0) {
                Text(
                    text = stringResource(R.string.required_items_remaining, missingCount), 
                    style = MaterialTheme.typography.labelMedium, 
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        Button(
            onClick = { 
                if (upgradeType != null) {
                    viewModel.submitKycWithDocuments(upgradeType) 
                }
            },
            enabled = !uiState.isSubmitting && upgradeType != null && isAllRequiredUploaded(uiState),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.submitting))
            } else {
                Text(stringResource(R.string.submit_verification))
            }
        }
        
        Spacer(Modifier.height(32.dp))
    }
}

private fun isAllRequiredUploaded(state: VerificationViewModel.UiState): Boolean {
    val imagesOk = state.requiredImages.all { req -> state.uploadedImages.any { state.uploadedImageTypes[it] == req } }
    val docsOk = state.requiredDocuments.all { req -> state.uploadedDocuments.any { state.uploadedDocTypes[it] == req } }
    return imagesOk && docsOk
}

private fun countMissingRequirements(state: VerificationViewModel.UiState): Int {
    val missingImages = state.requiredImages.count { req -> state.uploadedImages.none { state.uploadedImageTypes[it] == req } }
    val missingDocs = state.requiredDocuments.count { req -> state.uploadedDocuments.none { state.uploadedDocTypes[it] == req } }
    return missingImages + missingDocs
}

private fun formatLabel(type: String): String {
    return when (type) {
        // Photo types
        "SELFIE_WITH_CHICKEN" -> "Selfie with your chicken"
        "FARM_PHOTO" -> "Photo of your farm"
        "CHICKENS_PHOTO" -> "Photo of your chickens"
        "PRIZE_WINNING_STOCK" -> "Prize-winning bird photo"
        "FACILITY_PHOTO" -> "Breeding facility photo"
        
        // Document types
        "GOVT_ID" -> "Government ID (Aadhar/Voter ID/Job Card)"
        "BREEDING_RECORD" -> "Breeding record/certificate"
        "VET_CERTIFICATE" -> "Veterinary certificate"
        
        // Fallback: convert SNAKE_CASE to Title Case
        else -> type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }
}



