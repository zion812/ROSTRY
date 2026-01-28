package com.rio.rostry.ui.farmer.asset

import androidx.compose.foundation.BorderStroke
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
    onNavigateRoute: (String) -> Unit,  // Direct navigation for history
    onCreateListing: () -> Unit,
    onCreateAuction: () -> Unit,
    onViewHistory: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showTaggingDialog by remember { mutableStateOf(false) }
    
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
    
    if (showTaggingDialog) {
        val currentGroups = state.tagGroups.map { 
            TagGroupInput(
                id = it.id,
                count = it.count.toString(),
                gender = it.gender,
                tagColor = it.color,
                prefix = it.prefix,
                startNumber = it.startNumber.toString()
            ) 
        }
        
        BatchTaggingDialog(
            totalQuantity = state.asset?.quantity?.toInt() ?: 0,
            existingGroups = currentGroups,
            onDismiss = { showTaggingDialog = false },
            onConfirm = { inputs ->
                val groups = inputs.map { input ->
                    TagGroup(
                        id = input.id,
                        count = input.count.toIntOrNull() ?: 0,
                        gender = input.gender,
                        color = input.tagColor,
                        prefix = input.prefix,
                        startNumber = input.startNumber.toIntOrNull() ?: 1
                    )
                }
                viewModel.saveTagGroups(groups)
                showTaggingDialog = false
            }
        )
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

                    // Graduation Status Banner
                    if (asset.isEligibleForGraduation) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (asset.hasGraduationTags) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                            ),
                            border = BorderStroke(1.dp, if (asset.hasGraduationTags) Color(0xFF4CAF50) else Color(0xFFFFC107))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (asset.hasGraduationTags) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (asset.hasGraduationTags) Color(0xFF4CAF50) else Color(0xFFFFA000),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = if (asset.hasGraduationTags) "Ready for Graduation" else "Graduation Pending: Missing Tags",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (asset.hasGraduationTags) 
                                            "This batch will be automatically graduated soon." 
                                        else 
                                            "This batch is ready to graduate. Please add tag rules to proceed.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Main Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            
                             Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = asset.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                                
                                // Quick Actions
                                Row {
                                    if (asset.assetType == "BATCH" || asset.assetType == "FLOCK") {
                                        IconButton(
                                            onClick = { showTaggingDialog = true },
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        ) {
                                            Icon(Icons.Default.Label, contentDescription = "Tag Batch")
                                        }
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    
                                    IconButton(
                                        onClick = onCreateAuction,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    ) {
                                        Icon(Icons.Default.Gavel, contentDescription = "Auction")
                                    }
                                }
                            }

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
                            
                            // Batch Structure Breakdown
                            if (state.tagGroups.isNotEmpty()) {
                                Spacer(Modifier.height(16.dp))
                                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                Spacer(Modifier.height(16.dp))
                                
                                Text(
                                    text = "Batch Structure (Phase 2)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                                
                                state.tagGroups.forEach { group ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Label, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(16.dp),
                                            tint = getColor(group.color)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = group.label,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
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

                    // Performance Scorecard (For Batches)
                    if ((asset.assetType == "BATCH" || asset.assetType == "FLOCK") && state.performance != null) {
                        val perf = state.performance!!
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (perf.grade) {
                                    "A" -> Color(0xFFE8F5E9) // Light Green
                                    "B" -> Color(0xFFFFF3E0) // Light Orange
                                    else -> Color(0xFFFFEBEE) // Light Red
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Performance Grade",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Surface(
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        color = when (perf.grade) {
                                            "A" -> Color(0xFF4CAF50)
                                            "B" -> Color(0xFFFF9800)
                                            else -> Color(0xFFE91E63)
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = perf.grade,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    MetricBox(
                                        label = "FCR",
                                        value = perf.fcr.format(2),
                                        unit = ""
                                    )
                                    MetricBox(
                                        label = "Mortality",
                                        value = perf.mortalityRate.format(1),
                                        unit = "%"
                                    )
                                    MetricBox(
                                        label = "Feed Used",
                                        value = perf.totalFeedConsumed.format(0),
                                        unit = "kg"
                                    )
                                }
                                
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = when(perf.grade) {
                                        "A" -> "Excellent performance! Keep it up."
                                        "B" -> "Good. Watch feed efficiency."
                                        else -> "Needs attention. Mortality or Feed use is high."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
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
                    
                    // Recent History Section (Last 3 Events)
                    RecentHistorySection(
                        assetId = assetId,
                        viewModel = viewModel
                    )
                    
                    // Quick Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Record Activity Button
                        Button(
                            onClick = { 
                                onNavigateRoute(com.rio.rostry.ui.navigation.Routes.Builders.dailyLog(assetId))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Log Activity")
                        }
                    }

                    // View Full History Button - Always visible
                    OutlinedButton(
                        onClick = { 
                            // Navigate to bird history screen using Routes builder
                            onNavigateRoute(com.rio.rostry.ui.navigation.Routes.Builders.birdHistory(assetId))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Full History")
                    }

                    // View Pedigree Button
                    OutlinedButton(
                        onClick = { 
                            onNavigateRoute(com.rio.rostry.ui.navigation.Routes.EnthusiastNav.pedigree(assetId))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountTree, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Family Tree")
                    }

                    // View Show Records Button
                    OutlinedButton(
                        onClick = { 
                            onNavigateRoute(com.rio.rostry.ui.navigation.Routes.EnthusiastNav.showLog(assetId))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("View Show Records")
                    }

                    // Showcase Toggle & Share
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (asset.isShowcase) CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else CardDefaults.cardColors()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Showcase & Public Lookup",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Visible on profile & public ID search",
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
                            
                            Spacer(Modifier.height(16.dp))
                            
                            // Generate Showcase Card Button
                            Button(
                                onClick = { 
                                    onNavigateRoute(com.rio.rostry.ui.navigation.Routes.EnthusiastNav.showcaseCard(assetId))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color(0xFF3E2723)
                                )
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Generate Shareable Card")
                            }
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

/**
 * Recent History Section - Shows last 3 activity events for the asset.
 */
@Composable
private fun RecentHistorySection(
    assetId: String,
    viewModel: FarmAssetDetailViewModel
) {
    val recentEvents by viewModel.recentEvents.collectAsState()
    
    if (recentEvents.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                
                recentEvents.take(3).forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (event.type.uppercase()) {
                                "FEED" -> Icons.Default.Restaurant
                                "WEIGHT" -> Icons.Default.Scale
                                "VACCINATION" -> Icons.Default.Vaccines
                                "MORTALITY" -> Icons.Default.Warning
                                "HEALTH" -> Icons.Default.HealthAndSafety
                                else -> Icons.Default.Event
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.type.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when (event.type.uppercase()) {
                                    "FEED" -> event.quantity?.let { "${it}kg feed" } ?: (event.notes ?: "Feed recorded")
                                    "WEIGHT" -> event.quantity?.let { "${it.toInt()}g weight" } ?: (event.notes ?: "Weight recorded")
                                    else -> event.notes ?: formatDate(event.timestamp)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = formatDate(event.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Simple data class for recent events (used by RecentHistorySection)
 */
data class RecentActivityEvent(
    val type: String,
    val timestamp: Long,
    val notes: String? = null,
    val quantity: Double? = null
)

private fun getColor(name: String): Color {
    return when(name.lowercase()) {
        "red" -> Color(0xFFE57373)
        "blue" -> Color(0xFF64B5F6)
        "green" -> Color(0xFF81C784)
        "yellow" -> Color(0xFFFFF176)
        "black" -> Color.Black
        "white" -> Color.LightGray
        else -> Color.Gray
    }
}
