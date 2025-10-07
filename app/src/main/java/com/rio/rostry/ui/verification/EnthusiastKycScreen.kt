package com.rio.rostry.ui.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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

@Composable
fun EnthusiastKycScreen(
    onDone: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val levelState = remember { mutableStateOf("") }
    var selectedDocType by remember { mutableStateOf("AADHAAR") }
    val snackbarHostState = remember { SnackbarHostState() }

    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.uploadDocument(it.toString(), selectedDocType) }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.uploadImage(it.toString(), "SELFIE") }
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
                    onClick = { documentPicker.launch("*/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Selected Document")
                }
                OutlinedButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Upload Selfie")
                }
            }
        }

        // Progress summary
        item {
            val required = 3
            val done = (if (ui.uploadedDocuments.any { it.contains("AADHAAR") || it.contains("PAN") || it.contains("DL") || it.contains("PASSPORT") }) 1 else 0) +
                (if (ui.uploadedImages.isNotEmpty()) 1 else 0) +
                (if (ui.uploadedDocuments.any { it.contains("ADDRESS_PROOF") }) 1 else 0)
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
                        AsyncImage(
                            model = img,
                            contentDescription = "Uploaded image",
                            modifier = Modifier.size(64.dp)
                        )
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
                    if (ui.uploadedDocuments.isNotEmpty() && ui.uploadedImages.isNotEmpty()) {
                        val level = levelState.value.toIntOrNull()
                        viewModel.submitEnthusiastKyc(level)
                        viewModel.submitKycWithDocuments()
                    }
                },
                enabled = !ui.isSubmitting && ui.uploadedDocuments.isNotEmpty() && ui.uploadedImages.isNotEmpty(),
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

        ui.error?.let {
            item {
                Text("Error: $it", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
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
