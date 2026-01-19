package com.rio.rostry.ui.farmer.log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmActivityDetailScreen(
    activityId: String,
    onBack: () -> Unit,
    viewModel: FarmActivityDetailViewModel = hiltViewModel()
) {
    val activityLog by viewModel.activityLog.collectAsState()
    
    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit */ }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = { viewModel.deleteActivity(onSuccess = onBack) }) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        val log = activityLog
        if (log == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Icon and Type
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(getColorForType(log.activityType)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getIconForType(log.activityType),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = log.activityType.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatDate(log.createdAt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                // Value/Amount Section
                if (log.amountInr != null || log.quantity != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            if (log.amountInr != null) {
                                DetailItem(
                                    label = "Cost",
                                    value = "₹${String.format("%.2f", log.amountInr)}",
                                    icon = Icons.Default.CurrencyRupee
                                )
                            }
                            if (log.quantity != null) {
                                DetailItem(
                                    label = "Quantity",
                                    value = "${log.quantity} ${getUnitForType(log.activityType)}",
                                    icon = Icons.Default.Scale
                                )
                            }
                        }
                    }
                }

                // Details Section
                LogDetailRow(icon = Icons.Default.Category, label = "Category", value = log.category ?: "General")
                
                if (log.description != null) {
                    LogDetailRow(icon = Icons.Default.Description, label = "Description", value = log.description)
                }
                
                if (log.notes != null) {
                    LogDetailRow(icon = Icons.Default.Notes, label = "Notes", value = log.notes)
                }

                // Related Asset
                if (log.productId != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Pets, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Linked to Asset ID: ${log.productId}", // In real app, fetch name
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LogDetailRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun getUnitForType(type: String): String = when (type) {
    "FEED" -> "kg"
    "WEIGHT" -> "g"
    "MORTALITY" -> "birds"
    else -> ""
}

private fun getIconForType(type: String): ImageVector = when (type) {
    "MORTALITY" -> Icons.Default.Warning
    "FEED" -> Icons.Default.Restaurant
    "EXPENSE" -> Icons.Default.CurrencyRupee
    "WEIGHT" -> Icons.Default.FitnessCenter
    "VACCINATION" -> Icons.Default.Vaccines
    "DEWORMING" -> Icons.Default.Medication
    "MEDICATION" -> Icons.Default.LocalHospital
    "SANITATION" -> Icons.Default.CleaningServices
    "MAINTENANCE" -> Icons.Default.Build
    else -> Icons.Default.Event
}

private fun getColorForType(type: String): Color = when (type) {
    "MORTALITY" -> Color(0xFFDC2626) // Red
    "FEED" -> Color(0xFF16A34A) // Green
    "EXPENSE" -> Color(0xFFEA580C) // Orange
    "WEIGHT" -> Color(0xFF2563EB) // Blue
    "VACCINATION" -> Color(0xFF7C3AED) // Purple
    "DEWORMING" -> Color(0xFF0891B2) // Cyan
    "MEDICATION" -> Color(0xFFC026D3) // Fuchsia
    "SANITATION" -> Color(0xFF059669) // Emerald
    "MAINTENANCE" -> Color(0xFF4B5563) // Gray
    else -> Color(0xFF6B7280)
}
