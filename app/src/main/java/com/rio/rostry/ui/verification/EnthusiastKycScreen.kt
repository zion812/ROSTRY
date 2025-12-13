package com.rio.rostry.ui.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.UpgradeType
import androidx.compose.runtime.LaunchedEffect

@Composable
fun EnthusiastKycScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.setUpgradeType(UpgradeType.FARMER_TO_ENTHUSIAST)
    }

    val levelState = remember { mutableStateOf("") }
    var selectedDocType by remember { mutableStateOf("AADHAAR") }
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            viewModel.uploadDocument(it.toString(), selectedDocType, viewModel.ui.value.upgradeType ?: UpgradeType.FARMER_TO_ENTHUSIAST)
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            viewModel.uploadImage(it.toString(), "SELFIE", viewModel.ui.value.upgradeType ?: UpgradeType.FARMER_TO_ENTHUSIAST)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Enthusiast KYC", style = MaterialTheme.typography.titleLarge)
        }
        
        item {
            Text("Enter KYC level (numeric) for placeholder")
            OutlinedTextField(
                value = levelState.value,
                onValueChange = { levelState.value = it },
                label = { Text("KYC Level") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }

        item {
            Text("Upload Documents (Required)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            Text("Accepted: PDF, JPG, PNG (max 5MB)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Required for account verification and compliance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select document type:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    listOf("AADHAAR","PAN","DL","PASSPORT","ADDRESS_PROOF").forEach { type ->
                        AssistChip(
                            onClick = { selectedDocType = type },
                            label = { Text(type) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedDocType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
                OutlinedButton(
                    onClick = { documentPicker.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Selected Document")
                }
                OutlinedButton(
                    onClick = { imagePicker.launch(arrayOf("image/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Selfie")
                }
            }
        }

        // Progress summary
        item {
            val required = 3
            val docTypes = ui.uploadedDocTypes.values.map { it.uppercase() }
            val hasIdDoc = docTypes.any { it in setOf("AADHAAR", "PAN", "DL", "PASSPORT") }
            val hasAddress = docTypes.any { it == "ADDRESS_PROOF" }
            val hasSelfie = ui.uploadedImages.isNotEmpty()
            val done = (if (hasIdDoc) 1 else 0) + (if (hasSelfie) 1 else 0) + (if (hasAddress) 1 else 0)
            Text("Progress: $done of $required required documents")
            LinearProgressIndicator(progress = done / required.toFloat(), modifier = Modifier.fillMaxWidth())
        }

        // Upload Progress
        if (ui.uploadProgress.isNotEmpty()) {
            item {
                Text("Uploading...", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            }
            items(ui.uploadProgress.toList()) { (path, progress) ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(path.substringAfterLast("/"), style = MaterialTheme.typography.bodySmall)
                        LinearProgressIndicator(progress = progress / 100f, modifier = Modifier.fillMaxWidth())
                        Text("$progress%", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Uploaded Documents (with basic badge by type)
        if (ui.uploadedDocuments.isNotEmpty()) {
            item {
                Text("Uploaded Documents (${ui.uploadedDocuments.size})", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            }
            items(ui.uploadedDocuments) { doc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(doc.substringAfterLast("/"), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.removeUploadedFile(doc, true) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }

        // Uploaded Images
        if (ui.uploadedImages.isNotEmpty()) {
            item {
                Text("Uploaded Images (${ui.uploadedImages.size})", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            }
            items(ui.uploadedImages) { img ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var imageLoadError by remember { mutableStateOf(false) }

                        if (!imageLoadError) {
                            AsyncImage(
                                model = img,
                                contentDescription = "Uploaded image",
                                modifier = Modifier.size(64.dp),
                                onError = { imageLoadError = true },
                                onSuccess = { imageLoadError = false }
                            )
                        } else {
                            // Fallback when image fails to load
                            Column(
                                modifier = Modifier.size(64.dp).clickable { imageLoadError = false }, // Retry when clicked
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.BrokenImage, contentDescription = "Image failed to load", modifier = Modifier.size(32.dp))
                                Text("Failed", style = MaterialTheme.typography.bodySmall, fontSize = 8.sp)
                            }
                        }
                        Text(img.substringAfterLast("/"), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                        IconButton(onClick = { viewModel.removeUploadedFile(img, false) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }

        // Upload Error
        ui.uploadError?.let { error ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Upload Error", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                        Text(error, style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { viewModel.clearUploadError() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        // Validation errors
        if (ui.validationErrors.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Please fix the following:", color = MaterialTheme.colorScheme.error)
                        ui.validationErrors.forEach { (field, msg) ->
                            Text("• ${field}: ${msg}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item {
            Button(
                onClick = {
                    val docTypes = ui.uploadedDocTypes.values.map { it.uppercase() }
                    val hasIdDoc = docTypes.any { it in setOf("AADHAAR", "PAN", "DL", "PASSPORT") }
                    val hasAddress = docTypes.any { it == "ADDRESS_PROOF" }
                    val hasSelfie = ui.uploadedImages.isNotEmpty()
                    if (hasIdDoc && hasAddress && hasSelfie) {
                        val level = levelState.value.toIntOrNull()
                        viewModel.submitEnthusiastKyc(level)
                        viewModel.submitKycWithDocuments(viewModel.ui.value.upgradeType ?: UpgradeType.FARMER_TO_ENTHUSIAST)
                    }
                },
                enabled = !ui.isSubmitting && run {
                    val types = ui.uploadedDocTypes.values.map { it.uppercase() }
                    val id = types.any { it in setOf("AADHAAR", "PAN", "DL", "PASSPORT") }
                    val addr = types.any { it == "ADDRESS_PROOF" }
                    val selfie = ui.uploadedImages.isNotEmpty()
                    id && addr && selfie
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                if (ui.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    Text("  Submitting...")
                } else {
                    Text("Submit KYC with Documents")
                }
            }
            if (ui.uploadedDocuments.isEmpty() || ui.uploadedImages.isEmpty()) {
                Text(
                    "Please upload at least one ID proof and one selfie",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        ui.error?.let { error ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            Text("Submission Failed", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                        }
                        Text(error, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                        Button(
                            onClick = { viewModel.retryKycSubmission() },
                            enabled = !ui.isSubmitting,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            if (ui.isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                                Text("  Retrying...")
                            } else {
                                Text("Retry Submission")
                            }
                        }
                    }
                }
            }
        }
        ui.message?.let {
            item {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // Success dialog
        if (ui.submissionSuccess) {
            item {
                AlertDialog(
                    onDismissRequest = onDone,
                    confirmButton = { Button(onClick = onDone) { Text("OK") } },
                    title = { Text("Submission received") },
                    text = { Text("Your KYC documents have been submitted for verification. You'll be notified within 2-3 business days.") }
                )
            }
        }
    }
}
