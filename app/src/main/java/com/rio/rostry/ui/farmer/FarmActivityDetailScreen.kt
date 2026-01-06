package com.rio.rostry.ui.farmer

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
fun FarmActivityDetailScreen(
    activityId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: FarmActivityDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    var showNotesDialog by remember { mutableStateOf(false) }
    var showMediaGallery by remember { mutableStateOf(false) }
    var notesInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Details") },
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
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "Error")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) { Text("Go Back") }
                    }
                }
            }
            state.activity != null -> {
                val activity = state.activity!!
                val product = state.product
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Activity Type Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = getActivityColor(activity.activityType).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                getActivityIcon(activity.activityType),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = getActivityColor(activity.activityType)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = activity.activityType.replace("_", " "),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = dateFormatter.format(Date(activity.createdAt)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Details Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(12.dp))
                            
                            activity.category?.let {
                                DetailRow("Category", it)
                            }
                            
                            activity.amountInr?.let {
                                DetailRow("Amount", "₹%.2f".format(it))
                            }
                            
                            activity.quantity?.let {
                                val unit = when (activity.activityType) {
                                    "FEED" -> "kg"
                                    "WEIGHT" -> "g"
                                    else -> "units"
                                }
                                DetailRow("Quantity", "%.1f $unit".format(it))
                            }
                            
                            activity.description?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // Notes Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(onClick = { 
                                    notesInput = activity.notes ?: ""
                                    showNotesDialog = true 
                                }) {
                                    Icon(Icons.Default.Edit, "Edit Notes")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = activity.notes ?: "No notes recorded",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (activity.notes != null) 
                                    MaterialTheme.colorScheme.onSurface 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Photos
                    val mediaItems = activity.getMediaItems()
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
                                    onViewGallery = { showMediaGallery = true }
                                )
                            }
                        }
                    }

                    // Associated Bird Card
                    if (product != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToProduct(product.productId) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Pets, null, Modifier.size(40.dp), MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                                    Text(product.breed ?: "Bird", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }

    // Media Gallery Sheet
    if (showMediaGallery) {
        val activity = state.activity
        val mediaItems = activity?.getMediaItems() ?: emptyList()
        RecordMediaGallerySheet(
            mediaItems = mediaItems,
            initialIndex = 0,
            onDismiss = { showMediaGallery = false },
            showActions = false
        )
    }

    // Notes Dialog
    if (showNotesDialog) {
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Notes") },
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
                    viewModel.updateNotes(notesInput)
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
private fun getActivityIcon(type: String) = when (type.uppercase()) {
    "EXPENSE" -> Icons.Default.AttachMoney
    "SANITATION" -> Icons.Default.CleaningServices
    "MAINTENANCE" -> Icons.Default.Build
    "MEDICATION" -> Icons.Default.Medication
    "MORTALITY" -> Icons.Default.HeartBroken
    "FEED" -> Icons.Default.SetMeal
    "WEIGHT" -> Icons.Default.Scale
    "VACCINATION" -> Icons.Default.Vaccines
    "DEWORMING" -> Icons.Default.Science
    else -> Icons.Default.ListAlt
}

@Composable
private fun getActivityColor(type: String): Color = when (type.uppercase()) {
    "EXPENSE" -> Color(0xFF2196F3)
    "SANITATION" -> Color(0xFF4CAF50)
    "MAINTENANCE" -> Color(0xFF9C27B0)
    "MEDICATION" -> Color(0xFFE91E63)
    "MORTALITY" -> Color(0xFFF44336)
    "FEED" -> Color(0xFFFF9800)
    "WEIGHT" -> Color(0xFF607D8B)
    "VACCINATION" -> Color(0xFF009688)
    "DEWORMING" -> Color(0xFF795548)
    else -> Color(0xFF607D8B)
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
