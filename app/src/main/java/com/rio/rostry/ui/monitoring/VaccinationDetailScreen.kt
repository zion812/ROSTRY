package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.MediaItem
import com.rio.rostry.ui.components.MediaThumbnailRow
import com.rio.rostry.ui.components.RecordMediaGallerySheet
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationDetailScreen(
    vaccinationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: VaccinationDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showNotesDialog by remember { mutableStateOf(false) }
    var notesInput by remember { mutableStateOf("") }
    var showMediaGallery by remember { mutableStateOf(false) }
    var galleryInitialIndex by remember { mutableIntStateOf(0) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "Error", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) { Text("Go Back") }
                    }
                }
            }
            state.vaccination != null -> {
                val vaccination = state.vaccination!!
                val product = state.product
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status Banner
                    val isAdministered = vaccination.administeredAt != null
                    Surface(
                        color = if (isAdministered) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                               else MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAdministered) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isAdministered) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAdministered) "Administered" else "Scheduled",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdministered) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (isAdministered) 
                                        "Given on ${dateFormatter.format(Date(vaccination.administeredAt!!))}"
                                    else 
                                        "Scheduled for ${dateFormatter.format(Date(vaccination.scheduledAt))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Vaccine Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = vaccination.vaccineType,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))
                            
                            // Details Grid
                            DetailRow(label = "Scheduled Date", value = dateFormatter.format(Date(vaccination.scheduledAt)))
                            
                            if (vaccination.administeredAt != null) {
                                DetailRow(label = "Administered Date", value = dateFormatter.format(Date(vaccination.administeredAt)))
                            }
                            
                            if (!vaccination.supplier.isNullOrBlank()) {
                                DetailRow(label = "Supplier", value = vaccination.supplier)
                            }
                            
                            if (!vaccination.batchCode.isNullOrBlank()) {
                                DetailRow(label = "Batch Code", value = vaccination.batchCode)
                            }
                            
                            if (vaccination.doseMl != null) {
                                DetailRow(label = "Dose", value = "${vaccination.doseMl} ml")
                            }
                            
                            if (vaccination.costInr != null) {
                                DetailRow(label = "Cost", value = "₹${vaccination.costInr}")
                            }
                        }
                    }

                    // Associated Bird/Batch Card
                    if (product != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToProduct(product.productId) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Pets,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = product.breed ?: "Bird",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "View",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Efficacy Notes Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Efficacy Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(onClick = { 
                                    notesInput = vaccination.efficacyNotes ?: ""
                                    showNotesDialog = true 
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Notes")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = vaccination.efficacyNotes ?: "No notes recorded",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (vaccination.efficacyNotes != null) 
                                    MaterialTheme.colorScheme.onSurface 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Photos Card
                    val mediaItems = vaccination.getMediaItems()
                    if (mediaItems.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Photos (${mediaItems.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(12.dp))
                                MediaThumbnailRow(
                                    urls = mediaItems.map { it.url },
                                    maxVisible = 4,
                                    onViewGallery = {
                                        galleryInitialIndex = 0
                                        showMediaGallery = true
                                    }
                                )
                            }
                        }
                    }

                    // Action Button
                    if (!isAdministered) {
                        Button(
                            onClick = { viewModel.markAsAdministered() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Mark as Administered")
                        }
                    }

                    // Metadata
                    Text(
                        text = "Created ${dateFormatter.format(Date(vaccination.createdAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }

    // Media Gallery Sheet
    if (showMediaGallery) {
        val vaccination = state.vaccination
        val mediaItems = vaccination?.getMediaItems() ?: emptyList()
        RecordMediaGallerySheet(
            mediaItems = mediaItems,
            initialIndex = galleryInitialIndex,
            onDismiss = { showMediaGallery = false },
            showActions = false
        )
    }

    // Notes Dialog
    if (showNotesDialog) {
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Efficacy Notes") },
            text = {
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateEfficacyNotes(notesInput)
                    showNotesDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
