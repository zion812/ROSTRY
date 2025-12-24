package com.rio.rostry.ui.farmer.asset

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmAssetDetailScreen(
    assetId: String,
    viewModel: FarmAssetDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onCreateListing: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show messages
    LaunchedEffect(state.successMessage, state.error) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.asset?.name ?: "Asset Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.asset != null) {
                        IconButton(onClick = { viewModel.toggleShowcase() }) {
                            Icon(
                                if (state.asset?.isShowcase == true) Icons.Filled.Visibility 
                                else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Showcase"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (viewModel.canCreateListing()) {
                ExtendedFloatingActionButton(
                    onClick = onCreateListing,
                    icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    text = { Text("Create Listing") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.asset == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Asset not found", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) { Text("Go Back") }
                    }
                }
            }
            else -> {
                val asset = state.asset!!
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status and Type Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssetStatusChip(status = asset.status)
                        AssetTypeChip(type = asset.assetType)
                    }

                    // Main Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            
                            if (asset.description.isNotBlank()) {
                                Text(
                                    text = asset.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoItem(label = "Breed", value = asset.breed ?: "Unknown")
                                InfoItem(label = "Gender", value = asset.gender ?: "Unknown")
                            }
                        }
                    }

                    // Quantity and Metrics Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Quantity & Metrics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MetricBox(
                                    label = "Current",
                                    value = "${asset.quantity.toInt()}",
                                    unit = asset.unit
                                )
                                MetricBox(
                                    label = "Initial",
                                    value = "${asset.initialQuantity.toInt()}",
                                    unit = asset.unit
                                )
                                MetricBox(
                                    label = "Age",
                                    value = "${asset.ageWeeks ?: 0}",
                                    unit = "weeks"
                                )
                                if (asset.weightGrams != null) {
                                    MetricBox(
                                        label = "Weight",
                                        value = "${(asset.weightGrams / 1000).format(1)}",
                                        unit = "kg"
                                    )
                                }
                            }
                        }
                    }

                    // Health Status Card
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Health Status",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                HealthStatusChip(status = asset.healthStatus)
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            // Health update buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("HEALTHY", "SICK", "RECOVERING").forEach { status ->
                                    FilterChip(
                                        selected = asset.healthStatus == status,
                                        onClick = { viewModel.updateHealthStatus(status) },
                                        label = { Text(status.lowercase().replaceFirstChar { it.uppercase() }) },
                                        enabled = !state.isUpdating
                                    )
                                }
                            }
                        }
                    }

                    // Vaccination Info
                    if (asset.lastVaccinationDate != null || asset.nextVaccinationDate != null) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Vaccination",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(8.dp))
                                
                                asset.lastVaccinationDate?.let {
                                    InfoRow(
                                        icon = Icons.Default.Check,
                                        label = "Last Vaccination",
                                        value = formatDate(it)
                                    )
                                }
                                asset.nextVaccinationDate?.let {
                                    InfoRow(
                                        icon = Icons.Default.Schedule,
                                        label = "Next Due",
                                        value = formatDate(it),
                                        isWarning = it < System.currentTimeMillis()
                                    )
                                }
                            }
                        }
                    }

                    // Traceability Info
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Traceability",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            
                            InfoRow(
                                icon = Icons.Default.QrCode,
                                label = "Asset ID",
                                value = asset.assetId.take(12) + "..."
                            )
                            if (!asset.birdCode.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Tag,
                                    label = "Bird Code",
                                    value = asset.birdCode
                                )
                            }
                            if (!asset.origin.isNullOrBlank()) {
                                InfoRow(
                                    icon = Icons.Default.Place,
                                    label = "Origin",
                                    value = asset.origin.replace("_", " ").lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            }
                        }
                    }

                    // Showcase Toggle
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (asset.isShowcase) CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else CardDefaults.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Showcase on Profile",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Display this asset publicly on your farm profile",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = asset.isShowcase,
                                onCheckedChange = { viewModel.toggleShowcase() },
                                enabled = !state.isUpdating
                            )
                        }
                    }

                    // Creation Date
                    Text(
                        text = "Added ${formatDate(asset.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(Modifier.height(72.dp)) // Space for FAB
                }
            }
        }
        
        // Loading overlay when updating
        if (state.isUpdating) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun AssetStatusChip(status: String) {
    val (color, icon) = when (status.uppercase()) {
        "ACTIVE" -> MaterialTheme.colorScheme.primary to Icons.Default.CheckCircle
        "QUARANTINED" -> MaterialTheme.colorScheme.error to Icons.Default.Warning
        "ARCHIVED" -> MaterialTheme.colorScheme.outline to Icons.Default.Archive
        else -> MaterialTheme.colorScheme.secondary to Icons.Default.Info
    }
    
    AssistChip(
        onClick = { },
        label = { Text(status) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color,
            leadingIconContentColor = color
        )
    )
}

@Composable
private fun AssetTypeChip(type: String) {
    AssistChip(
        onClick = { },
        label = { Text(type) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
private fun HealthStatusChip(status: String) {
    val color = when (status.uppercase()) {
        "HEALTHY" -> Color(0xFF4CAF50)
        "SICK" -> Color(0xFFF44336)
        "RECOVERING" -> Color(0xFFFF9800)
        "INJURED" -> Color(0xFFE91E63)
        else -> MaterialTheme.colorScheme.outline
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MetricBox(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isWarning: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
