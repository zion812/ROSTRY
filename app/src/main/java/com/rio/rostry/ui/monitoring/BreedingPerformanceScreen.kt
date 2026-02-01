package com.rio.rostry.ui.monitoring

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.monitoring.vm.BreedingPerformanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingPerformanceScreen(
    onBack: () -> Unit,
    viewModel: BreedingPerformanceViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val pairs by viewModel.pairDetails.collectAsState()
    val weeklyData by viewModel.weeklyTrends.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Pairs", "Trends")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Performance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, label ->
                        FilterChip(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
            }
            
            when (selectedTab) {
                0 -> {
                    // Overview Section
                    item {
                        // Summary Cards Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                icon = Icons.Default.Favorite,
                                value = stats.activePairs.toString(),
                                label = "Active Pairs",
                                color = Color(0xFFE91E63),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                icon = Icons.Default.Egg,
                                value = stats.totalEggsCollected.toString(),
                                label = "Total Eggs",
                                color = Color(0xFFFF9800),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    item {
                        // Hatch Rate Card with Gauge
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Average Hatch Rate",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(16.dp))
                                
                                // Circular gauge
                                HatchRateGauge(
                                    rate = stats.averageHatchRate.toFloat(),
                                    modifier = Modifier.size(120.dp)
                                )
                                
                                Spacer(Modifier.height(8.dp))
                                
                                val rateQuality = when {
                                    stats.averageHatchRate >= 0.85 -> "Excellent" to Color(0xFF4CAF50)
                                    stats.averageHatchRate >= 0.70 -> "Good" to Color(0xFF8BC34A)
                                    stats.averageHatchRate >= 0.50 -> "Average" to Color(0xFFFF9800)
                                    else -> "Needs Improvement" to Color(0xFFF44336)
                                }
                                
                                Text(
                                    rateQuality.first,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = rateQuality.second,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Top Performer
                    if (stats.topPerformingPairName != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    
                                    Spacer(Modifier.width(16.dp))
                                    
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            "Top Performer",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            stats.topPerformingPairName!!,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Text(
                                        String.format("%.0f%%", stats.topPerformingPairRate * 100),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700)
                                    )
                                }
                            }
                        }
                    }
                }
                
                1 -> {
                    // Pairs Comparison
                    if (pairs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No breeding pairs found")
                            }
                        }
                    } else {
                        items(pairs) { pair ->
                            PairComparisonCard(pair)
                        }
                    }
                }
                
                2 -> {
                    // Trends Graph
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.TrendingUp,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Weekly Hatch Rate Trend",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(Modifier.height(24.dp))
                                
                                if (weeklyData.isNotEmpty()) {
                                    TrendLineChart(
                                        data = weeklyData,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    )
                                    
                                    Spacer(Modifier.height(8.dp))
                                    
                                    // Legend
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        weeklyData.takeLast(4).forEachIndexed { index, data ->
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    "Week ${index + 1}",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                                Text(
                                                    String.format("%.0f%%", data.hatchRate * 100),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Not enough data for trends")
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Timeline,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Weekly Egg Production",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(Modifier.height(16.dp))
                                
                                weeklyData.takeLast(4).forEachIndexed { index, data ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Week ${index + 1}",
                                            modifier = Modifier.width(60.dp),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        LinearProgressIndicator(
                                            progress = { (data.eggsCollected / 50f).coerceIn(0f, 1f) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "${data.eggsCollected}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
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
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HatchRateGauge(
    rate: Float,
    modifier: Modifier = Modifier
) {
    val animatedRate by animateFloatAsState(
        targetValue = rate,
        animationSpec = tween(1000),
        label = "gauge"
    )
    
    val gaugeColor = when {
        rate >= 0.85f -> Color(0xFF4CAF50)
        rate >= 0.70f -> Color(0xFF8BC34A)
        rate >= 0.50f -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val startAngle = 135f
            val sweepAngle = 270f
            
            // Background arc
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Progress arc
            drawArc(
                color = gaugeColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedRate,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Text(
            String.format("%.0f%%", animatedRate * 100),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PairComparisonCard(pair: BreedingPairDetail) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    pair.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${pair.eggsCollected} eggs collected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    String.format("%.0f%%", pair.hatchRate * 100),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        pair.hatchRate >= 0.80f -> Color(0xFF4CAF50)
                        pair.hatchRate >= 0.60f -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
                Text(
                    "Hatch Rate",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun TrendLineChart(
    data: List<WeeklyTrendData>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val maxRate = 1f
        val padding = 16.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2
        
        val points = data.mapIndexed { index, item ->
            val x = padding + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * chartWidth
            val y = padding + (1 - item.hatchRate.toFloat() / maxRate) * chartHeight
            Offset(x, y)
        }
        
        // Draw grid lines
        for (i in 0..4) {
            val y = padding + (i / 4f) * chartHeight
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(padding, y),
                end = Offset(size.width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Draw line
        if (points.size >= 2) {
            val path = Path()
            path.moveTo(points.first().x, points.first().y)
            
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
            
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Draw points
        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = point
            )
        }
    }
}

// Data classes for enhanced ViewModel
data class BreedingPairDetail(
    val pairId: String,
    val name: String,
    val eggsCollected: Int,
    val hatchRate: Double,
    val status: String
)

data class WeeklyTrendData(
    val weekNumber: Int,
    val hatchRate: Double,
    val eggsCollected: Int
)
