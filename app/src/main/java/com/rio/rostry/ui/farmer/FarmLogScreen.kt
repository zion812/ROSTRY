package com.rio.rostry.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi // For stickyHeader
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Farm Log Screen - Shows all farm activities with filtering.
 * Accessible from Home Dashboard and Profile screen.
 * Now supports clicking activities to navigate to their detail screens.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FarmLogScreen(
    onBack: () -> Unit,
    onNavigateRoute: (String) -> Unit = {},  // Default route navigation
    onBirdClick: ((String) -> Unit)? = null,
    onActivityClick: ((FarmActivityLogEntity) -> Unit)? = null,
    viewModel: FarmLogViewModel = hiltViewModel()
) {
    // Default activity click handler if not provided
    val handleActivityClick: (FarmActivityLogEntity) -> Unit = onActivityClick ?: { activity ->
        onNavigateRoute("farm_activity_detail/${activity.activityId}")
    }
    
    val uiState by viewModel.uiState.collectAsState()
    var showQuickLogSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Log", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickLogSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Quick Check")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Summary Card
            if (uiState.totalExpenses > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total Expenses",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "₹${String.format("%.2f", uiState.totalExpenses)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            Icons.Default.CurrencyRupee,
                            contentDescription = "Total expenses indicator",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedType == null,
                        onClick = { viewModel.setFilter(null) },
                        label = { Text("All") }
                    )
                }
                items(viewModel.getActivityTypes()) { type ->
                    FilterChip(
                        selected = uiState.selectedType == type,
                        onClick = { viewModel.setFilter(type) },
                        label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingIcon = {
                            Icon(
                                getIconForType(type),
                                contentDescription = "$type filter",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Log List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "No activity logs",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No activity logs yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Use Quick Log to record activities",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.groupedLogs.forEach { (header, logs) ->
                         stickyHeader {
                             FarmLogHeader(
                                 title = header,
                                 summary = uiState.dailySummaries[header]
                             )
                         }
                         
                        items(logs) { log ->
                            ActivityLogCard(
                                log = log,
                                onClick = {
                                    handleActivityClick(log)
                                        ?: log.productId?.let { onBirdClick?.invoke(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showQuickLogSheet) {
        QuickLogBottomSheet(
            products = uiState.activeProducts,
            onDismiss = { showQuickLogSheet = false },
            onLogSubmit = { productIds, type, value, notes ->
                viewModel.submitQuickLog(productIds, type, value, notes)
            }
        )
    }
}

@Composable
private fun ActivityLogCard(
    log: FarmActivityLogEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getColorForType(log.activityType)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getIconForType(log.activityType),
                    contentDescription = "${log.activityType} activity",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    log.activityType.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Show value based on type
                val valueText = when {
                    log.amountInr != null -> "₹${String.format("%.2f", log.amountInr)}"
                    log.quantity != null -> when (log.activityType) {
                        "FEED" -> "${log.quantity}kg"
                        "WEIGHT" -> "${log.quantity}g"
                        "MORTALITY" -> "${log.quantity.toInt()} birds"
                        else -> log.quantity.toString()
                    }
                    else -> log.description ?: ""
                }
                if (valueText.isNotBlank()) {
                    Text(
                        valueText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                log.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Text(
                            notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    FarmLogViewModel.formatDate(log.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Bird indicator
            if (log.productId != null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "View bird",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FarmLogHeader(
    title: String,
    summary: FarmLogViewModel.DailySummary?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (summary != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (summary.feedKg > 0) {
                        SummaryBadge(icon = Icons.Default.Restaurant, text = "${summary.feedKg}kg", color = Color(0xFF16A34A)) // Green
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (summary.expenseInr > 0) {
                        SummaryBadge(icon = Icons.Default.CurrencyRupee, text = "₹${summary.expenseInr.toInt()}", color = Color(0xFFEA580C)) // Orange
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                if (summary.mortalityCount > 0) {
                    SummaryBadge(icon = Icons.Default.Warning, text = "${summary.mortalityCount}", color = Color(0xFFDC2626)) // Red
                }
            }
        }
        
        // Cost Breakdown Row
        if (summary != null && summary.costBreakdown.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                summary.costBreakdown.forEach { (type, amount) ->
                    Text(
                        text = "${type.lowercase().replaceFirstChar { it.uppercase() }}: ₹${amount.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
    

}
}

@Composable
fun SummaryBadge(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(2.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
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

@Composable
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
