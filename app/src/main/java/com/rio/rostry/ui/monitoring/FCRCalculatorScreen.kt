package com.rio.rostry.ui.monitoring

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.FCRRating
import com.rio.rostry.data.repository.FCRRating as Rating
import com.rio.rostry.domain.service.HistoricalFCRPoint
import com.rio.rostry.domain.service.InsightPriority
import com.rio.rostry.domain.service.InsightType
import com.rio.rostry.ui.monitoring.vm.FCRCalculatorViewModel

/**
 * FCR Calculator Screen for calculating and displaying Feed Conversion Ratio.
 * Enhanced with Historical Trends and Benchmarking.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FCRCalculatorScreen(
    assetId: String,
    viewModel: FCRCalculatorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val fcrAnalysis by viewModel.fcrAnalysis.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val historicalFCR by viewModel.historicalFCR.collectAsState()
    val benchmarkData by viewModel.benchmarkData.collectAsState()
    val availableBreeds by viewModel.availableBreeds.collectAsState()
    val selectedBreed by viewModel.selectedBreed.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Dropdown state
    var breedExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FCR Calculator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Selector
            Text(
                text = "Analysis Period",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.periodOptions.forEach { days ->
                    FilterChip(
                        selected = selectedPeriod == days,
                        onClick = { viewModel.selectPeriod(days) },
                        label = { 
                            Text(
                                when (days) {
                                    7 -> "7 Days"
                                    14 -> "2 Weeks"
                                    30 -> "1 Month"
                                    90 -> "3 Months"
                                    else -> "$days Days"
                                }
                            )
                        },
                        leadingIcon = if (selectedPeriod == days) {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Loading State
            AnimatedVisibility(visible = isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error State
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // FCR Result
            AnimatedVisibility(
                visible = !isLoading && fcrAnalysis != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                fcrAnalysis?.let { analysis ->
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // FCR Gauge Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Feed Conversion Ratio",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // FCR Value with colored background
                                val ratingColor = when (analysis.rating) {
                                    FCRRating.EXCELLENT -> Color(0xFF4CAF50) // Green
                                    FCRRating.GOOD -> Color(0xFF8BC34A) // Light Green
                                    FCRRating.AVERAGE -> Color(0xFFFFC107) // Yellow
                                    FCRRating.POOR -> Color(0xFFF44336) // Red
                                    FCRRating.UNKNOWN -> Color.Gray
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(ratingColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = String.format("%.2f", analysis.fcr),
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "FCR",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Rating Badge
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = ratingColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = analysis.ratingDescription,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = ratingColor
                                    )
                                }
                            }
                        }
                        
                        // Benchmark Selection
                        if (availableBreeds.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Compare vs Benchmark",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = breedExpanded,
                                        onExpandedChange = { breedExpanded = !breedExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedBreed,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded) },
                                            modifier = Modifier.menuAnchor().fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = breedExpanded,
                                            onDismissRequest = { breedExpanded = false }
                                        ) {
                                            availableBreeds.forEach { breed ->
                                                DropdownMenuItem(
                                                    text = { Text(breed) },
                                                    onClick = {
                                                        viewModel.selectBreed(breed)
                                                        breedExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Trend Chart
                        if (historicalFCR.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        text = "FCR Trend vs Benchmark",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    FCRTrendChart(
                                        actual = historicalFCR,
                                        benchmark = benchmarkData,
                                        modifier = Modifier.fillMaxWidth().height(220.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LegendItem(color = MaterialTheme.colorScheme.primary, label = "Your Flock")
                                        LegendItem(color = Color.Gray, label = "$selectedBreed Standard", isDashed = true)
                                    }
                                }
                            }
                        }
                        
                        // Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Feed",
                                value = String.format("%.1f kg", analysis.totalFeedKg),
                                icon = Icons.Default.Dining
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Weight Gain",
                                value = String.format("%.2f kg", analysis.weightGainKg),
                                icon = Icons.Default.TrendingUp
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Initial Weight",
                                value = String.format("%.2f kg", analysis.initialWeightKg),
                                icon = Icons.Default.Scale
                            )
                            MetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Final Weight",
                                value = String.format("%.2f kg", analysis.finalWeightKg),
                                icon = Icons.Default.Scale
                            )
                        }
                        
                        // Insights
                        val insights = viewModel.getInsights()
                        if (insights.isNotEmpty()) {
                            Text(
                                text = "Smart Insights",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            insights.forEach { insight ->
                                val (bgColor, iconTint) = when (insight.type) {
                                    InsightType.FEED_EFFICIENCY -> {
                                        if (insight.priority == InsightPriority.HIGH || insight.priority == InsightPriority.CRITICAL)
                                            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
                                        else 
                                            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
                                    }
                                    InsightType.COST_SAVINGS -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                    InsightType.MORTALITY_ALERT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
                                    InsightType.GROWTH_TREND -> Color(0xFFFFF3E0) to Color(0xFFFF9800)
                                    else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                                }
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        val icon = when (insight.type) {
                                            InsightType.FEED_EFFICIENCY -> Icons.Default.TrendingUp
                                            InsightType.COST_SAVINGS -> Icons.Default.AttachMoney
                                            InsightType.MORTALITY_ALERT -> Icons.Default.Warning
                                            InsightType.GROWTH_TREND -> Icons.Default.ShowChart
                                            else -> Icons.Default.Info
                                        }
                                        Icon(icon, contentDescription = null, tint = iconTint)
                                        Column {
                                            Text(
                                                text = insight.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = insight.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (insight.actionLabel != null) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = insight.actionLabel!!,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = iconTint,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Empty State
            if (!isLoading && fcrAnalysis == null && error == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select a period to calculate FCR",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, isDashed: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp, 2.dp)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FCRTrendChart(
    actual: List<HistoricalFCRPoint>,
    benchmark: List<HistoricalFCRPoint>,
    modifier: Modifier = Modifier
) {
    if (actual.isEmpty()) return
    
    val weeks = actual.map { it.week }
    val maxWeek = weeks.maxOrNull() ?: 0
    val minWeek = weeks.minOrNull() ?: 0
    
    // Y-Axis range: simple logic to include both datasets
    val maxActual = actual.maxOf { it.fcr }
    val maxBench = if (benchmark.isNotEmpty()) benchmark.maxOf { it.fcr } else 0f
    val maxY = maxOf(maxActual, maxBench) * 1.1f // 10% buffer
    
    Canvas(modifier = modifier.padding(16.dp)) {
        val width = size.width
        val height = size.height
        
        // Draw Axis Lines
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = 2f
        )
        
        fun getX(week: Int): Float {
             val range = (maxWeek - minWeek).coerceAtLeast(1)
             return ((week - minWeek).toFloat() / range) * width
        }
        
        fun getY(fcr: Float): Float {
            return height - (fcr / maxY) * height
        }
        
        // Draw Benchmark (Dashed line ideally, but standard line for now with gray)
        if (benchmark.isNotEmpty()) {
            val validBenchmarks = benchmark.filter { it.week >= minWeek && it.week <= maxWeek }
            if (validBenchmarks.size > 1) {
                val path = Path()
                validBenchmarks.forEachIndexed { index, point ->
                    val x = getX(point.week)
                    val y = getY(point.fcr)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path, 
                    color = Color.Gray.copy(alpha = 0.5f), 
                    style = Stroke(width = 3f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
            }
        }
        
        // Draw Actual
        val path = Path()
        actual.sortedBy { it.week }.forEachIndexed { index, point ->
            val x = getX(point.week)
            val y = getY(point.fcr)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            
            // Draw points
            drawCircle(color = Color(0xFF2196F3), radius = 4f, center = Offset(x, y))
        }
        
        drawPath(
            path = path,
            color = Color(0xFF2196F3),
            style = Stroke(width = 4f)
        )
        
        // Labels (Simple start/end)
        /* 
           Ideally draw text labels, but NativeCanvas text drawing in compose requires text measurer 
           or native paint. Keeping it simple visual only for MVP 
        */
    }
}
