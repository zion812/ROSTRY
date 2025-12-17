package com.rio.rostry.ui.verification

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.ui.verification.components.UploadRow

@Composable
fun EnthusiastKycScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.setUpgradeType(UpgradeType.FARMER_TO_ENTHUSIAST)
    }

    val levelState = remember { mutableStateOf("") }
    
    var currentUploadType by remember { mutableStateOf<String?>(null) }
    var currentUploadIsImage by remember { mutableStateOf(true) }

    val contentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            
            if (currentUploadType != null) {
                if (currentUploadIsImage) {
                     viewModel.uploadImage(it.toString(), currentUploadType!!, viewModel.ui.value.upgradeType ?: UpgradeType.FARMER_TO_ENTHUSIAST)
                } else {
                     viewModel.uploadDocument(it.toString(), currentUploadType!!, viewModel.ui.value.upgradeType ?: UpgradeType.FARMER_TO_ENTHUSIAST)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Enthusiast KYC", style = MaterialTheme.typography.titleLarge)
            Text("Upgrade to Enthusiast for advanced features", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        item {
            OutlinedTextField(
                value = levelState.value,
                onValueChange = { levelState.value = it },
                label = { Text("KYC Level / Experience (Years)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Required Images
        if (ui.requiredImages.isNotEmpty()) {
            item {
                 Text("Required Photos", style = MaterialTheme.typography.titleMedium)
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ui.requiredImages.forEach { imageType ->
                             val uploadedUrl = ui.uploadedImages.find { url -> 
                                ui.uploadedImageTypes[url] == imageType 
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

        // Required Documents
        if (ui.requiredDocuments.isNotEmpty()) {
            item {
                 Text("Required Documents", style = MaterialTheme.typography.titleMedium)
            }
            item {
                 Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ui.requiredDocuments.forEach { docType ->
                             val uploadedUrl = ui.uploadedDocuments.find { url -> 
                                ui.uploadedDocTypes[url] == docType 
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

        if (ui.uploadProgress.isNotEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Uploading...", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterEnd))
                }
            }
        }

        ui.uploadError?.let { error ->
            item {
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Button(onClick = { viewModel.clearUploadError() }) { Text("Retry") }
            }
        }

        if (ui.validationErrors.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        ui.validationErrors.forEach { (field, msg) ->
                            Text("• $field: $msg", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }
        }

        item {
            val isReady = isAllRequiredUploaded(ui)
            Button(
                onClick = {
                    val level = levelState.value.toIntOrNull()
                    val type = viewModel.ui.value.upgradeType ?: UpgradeType.FARMER_TO_ENTHUSIAST
                    viewModel.submitKycWithDocuments(type, level)
                },
                enabled = !ui.isSubmitting && isReady,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(50.dp)
            ) {
                if (ui.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Text("  Submitting...")
                } else {
                    Text("Submit KYC")
                }
            }
        }
        
        // Success dialog
        if (ui.submissionSuccess) {
            item {
                AlertDialog(
                    onDismissRequest = onDone,
                    confirmButton = { Button(onClick = onDone) { Text("OK") } },
                    title = { Text("Submission received") },
                    text = { Text("Your KYC documents have been submitted for verification.") }
                )
            }
        }
        
        item { Spacer(Modifier.height(32.dp)) }
    }
}

private fun isAllRequiredUploaded(state: VerificationViewModel.UiState): Boolean {
    val imagesOk = state.requiredImages.all { req -> state.uploadedImages.any { state.uploadedImageTypes[it] == req } }
    val docsOk = state.requiredDocuments.all { req -> state.uploadedDocuments.any { state.uploadedDocTypes[it] == req } }
    return imagesOk && docsOk
}

private fun formatLabel(type: String): String {
    return type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}
