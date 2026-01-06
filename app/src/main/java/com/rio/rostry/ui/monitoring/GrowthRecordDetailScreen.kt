package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun GrowthRecordDetailScreen(
    recordId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    viewModel: GrowthRecordDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showMediaGallery by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Growth Record") },
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
            state.record != null -> {
                val record = state.record!!
                val product = state.product
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Week Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Week ${record.week}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = dateFormatter.format(Date(record.createdAt)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Weight Metric
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            label = "Weight",
                            value = record.weightGrams?.let { "%.0f".format(it) } ?: "--",
                            unit = "grams",
                            icon = Icons.Default.Scale,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Height Metric
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            label = "Height",
                            value = record.heightCm?.let { "%.1f".format(it) } ?: "--",
                            unit = "cm",
                            icon = Icons.Default.Height,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    // Health Status
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Health Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("OK", "WATCH", "SICK").forEach { status ->
                                    FilterChip(
                                        selected = record.healthStatus == status,
                                        onClick = { viewModel.updateHealthStatus(status) },
                                        label = { Text(status) },
                                        leadingIcon = if (record.healthStatus == status) {
                                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = when (status) {
                                                "OK" -> Color(0xFF4CAF50)
                                                "WATCH" -> Color(0xFFFF9800)
                                                else -> MaterialTheme.colorScheme.error
                                            },
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Milestone
                    if (!record.milestone.isNullOrBlank()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Milestone",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = record.milestone,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Growth Chart (Simple Line)
                    if (state.allRecords.size > 1) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Growth Trend",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(12.dp))
                                SimpleGrowthChart(
                                    records = state.allRecords,
                                    currentWeek = record.week,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
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
                    // Photo Card
                    val mediaItems = record.getMediaItems()
                    if (mediaItems.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Photo",
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
                    
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }

    // Media Gallery Sheet
    if (showMediaGallery) {
        val record = state.record
        val mediaItems = record?.getMediaItems() ?: emptyList()
        if (mediaItems.isNotEmpty()) {
            RecordMediaGallerySheet(
                mediaItems = mediaItems,
                initialIndex = 0,
                onDismiss = { showMediaGallery = false },
                showActions = false
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, Modifier.size(24.dp), tint = color)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SimpleGrowthChart(
    records: List<com.rio.rostry.data.database.entity.GrowthRecordEntity>,
    currentWeek: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.tertiary
    
    Canvas(modifier = modifier) {
        val weights = records.mapNotNull { it.weightGrams }
        if (weights.isEmpty()) return@Canvas
        
        val maxWeight = weights.maxOrNull() ?: 1.0
        val minWeight = weights.minOrNull() ?: 0.0
        val range = (maxWeight - minWeight).coerceAtLeast(1.0)
        
        val path = Path()
        val padding = 20f
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2
        
        records.forEachIndexed { index, record ->
            val x = padding + (index.toFloat() / (records.size - 1).coerceAtLeast(1)) * chartWidth
            val weight = record.weightGrams ?: minWeight
            val y = padding + chartHeight - ((weight - minWeight) / range * chartHeight).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            // Draw point
            val pointColor = if (record.week == currentWeek) accentColor else primaryColor
            drawCircle(
                color = pointColor,
                radius = if (record.week == currentWeek) 8f else 4f,
                center = Offset(x, y)
            )
        }
        
        // Draw line
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 2f)
        )
    }
}
