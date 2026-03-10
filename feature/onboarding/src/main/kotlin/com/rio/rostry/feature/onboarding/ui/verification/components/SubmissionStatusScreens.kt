package com.rio.rostry.ui.verification.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.verification.VerificationViewModel

private const val SUPPORT_PHONE = "8106312656"

@Composable
fun VerifiedStateScreen(onDone: () -> Unit) {
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
fun PendingStateScreen(
    onDone: () -> Unit,
    uiState: VerificationViewModel.UiState,
    onViewDetails: () -> Unit = {}
) {
    val context = LocalContext.current
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, 
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(Icons.Default.HourglassEmpty, "Pending", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                Text("Verification Pending", style = MaterialTheme.typography.headlineSmall)
                Text("Your documents are under review.", style = MaterialTheme.typography.bodyMedium)
                
                // Contact Support Card for Verification Delays
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Verification delayed?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            "Contact us via WhatsApp or Call",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://wa.me/91$SUPPORT_PHONE?text=Hi, I need help with my farmer verification.")
                                    }
                                    context.startActivity(whatsappIntent)
                                }
                            ) {
                                Icon(Icons.Default.Message, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("WhatsApp")
                            }
                            
                            FilledTonalButton(
                                onClick = {
                                    val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$SUPPORT_PHONE")
                                    }
                                    context.startActivity(callIntent)
                                }
                            ) {
                                Icon(Icons.Default.Call, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Call")
                            }
                        }
                        
                        Text(
                            SUPPORT_PHONE,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { showDetails = true }) { Text("View Details") }
                    Button(onClick = onDone) { Text("Done") }
                }
            }
        }
    }
}
