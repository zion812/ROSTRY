package com.rio.rostry.ui.analytics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

/**
 * Enhanced Farmer Dashboard with:
 * - Hero stat card with animated counter
 * - Staggered animated KPI cards
 * - Sparkline trend charts
 * - Offline-first Room data
 * - Export functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerDashboardScreen(
    vm: FarmerDashboardViewModel = hiltViewModel(),
    onOpenReports: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
) {
    val d = vm.dashboard.collectAsState().value
    val last4 = vm.lastFour.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Animation states
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text("Farm Analytics", fontWeight = FontWeight.Bold) 
                },
                actions = {
                    IconButton(onClick = {
                        // Export CSV
                        val kpis = mapOf(
                            "Revenue" to d.revenue,
                            "Orders" to d.orders.toDouble(),
                            "Product Views" to d.productViews.toDouble(),
                            "Engagement Score" to d.engagementScore
                        )
                        val units = mapOf(
                            "Revenue" to "INR",
                            "Orders" to "count",
                            "Product Views" to "views",
                            "Engagement Score" to "score"
                        )
                        val res = com.rio.rostry.utils.export.CsvExporter.exportKpis(
                            context, kpis, fileName = "farmer_kpis.csv", dateRange = null, units = units
                        )
                        when (res) {
                            is com.rio.rostry.utils.Resource.Success -> {
                                res.data?.let { com.rio.rostry.utils.export.CsvExporter.showExportNotification(context, it) }
                                scope.launch { snackbarHostState.showSnackbar("Report exported") }
                            }
                            is com.rio.rostry.utils.Resource.Error -> scope.launch { snackbarHostState.showSnackbar(res.message ?: "Export failed") }
                            else -> {}
                        }
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                    IconButton(onClick = {
                        val kpis = mapOf(
                            "Revenue" to d.revenue,
                            "Orders" to d.orders.toDouble(),
                            "Product Views" to d.productViews.toDouble(),
                            "Engagement Score" to d.engagementScore
                        )
                        val units = mapOf("Revenue" to "INR", "Orders" to "count", "Product Views" to "views", "Engagement Score" to "score")
                        val res = com.rio.rostry.utils.export.CsvExporter.exportKpis(context, kpis, fileName = "farmer_kpis.csv", dateRange = null, units = units)
                        if (res is com.rio.rostry.utils.Resource.Success) {
                            res.data?.let { uri ->
                                val intent = com.rio.rostry.utils.export.CsvExporter.shareCsv(context, uri)
                                context.startActivity(intent)
                            }
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Revenue Card
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically { -40 }
                ) {
                    HeroStatCard(
                        title = "Total Revenue",
                        value = d.revenue,
                        subtitle = "This week",
                        icon = Icons.Default.TrendingUp,
                        trend = if (last4.size >= 2) {
                            val prev = last4.getOrNull(1)?.revenueInr ?: 0.0
                            val curr = last4.getOrNull(0)?.revenueInr ?: d.revenue
                            if (prev > 0) ((curr - prev) / prev * 100).toInt() else 0
                        } else null
                    )
                }
            }
            
            // KPI Cards Row
            item {
                Text(
                    "Key Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    val kpis = listOf(
                        KpiData("Orders", d.orders.toDouble(), Icons.Default.ShoppingCart, Color(0xFF2196F3)),
                        KpiData("Views", d.productViews.toDouble(), Icons.Default.Visibility, Color(0xFF9C27B0)),
                        KpiData("Engagement", d.engagementScore, Icons.Default.FavoriteBorder, Color(0xFFE91E63), suffix = "pts")
                    )
                    items(kpis) { kpi ->
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(300, delayMillis = kpis.indexOf(kpi) * 100)) +
                                    scaleIn(animationSpec = tween(300, delayMillis = kpis.indexOf(kpi) * 100))
                        ) {
                            KpiCard(
                                title = kpi.title,
                                value = kpi.value,
                                icon = kpi.icon,
                                color = kpi.color,
                                suffix = kpi.suffix
                            )
                        }
                    }
                }
            }
            
            // Suggestions
            if (d.suggestions.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(400, delayMillis = 300))
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFFC107))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Insights", fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(8.dp))
                                d.suggestions.forEach { s ->
                                    Text("• $s", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
            
            // Trend Charts Section
            if (last4.isNotEmpty()) {
                item {
                    Text(
                        "Weekly Trends",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 400))
                    ) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                TrendChartRow(
                                    label = "Revenue",
                                    values = last4.map { it.revenueInr },
                                    color = Color(0xFF4CAF50),
                                    format = { "₹${NumberFormat.getNumberInstance().format(it.toInt())}" }
                                )
                                
                                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                                
                                TrendChartRow(
                                    label = "Mortality Rate",
                                    values = last4.map { it.mortalityRate.coerceIn(0.0, 1.0) * 100 },
                                    color = Color(0xFFD32F2F),
                                    format = { "${it.toInt()}%" }
                                )
                                
                                HorizontalDivider(Modifier.padding(vertical = 12.dp))
                                
                                TrendChartRow(
                                    label = "Hatch Success",
                                    values = last4.map { it.hatchSuccessRate.coerceIn(0.0, 1.0) * 100 },
                                    color = Color(0xFF2196F3),
                                    format = { "${it.toInt()}%" }
                                )
                            }
                        }
                    }
                }
            }
            
            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenReports,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Assessment, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reports")
                    }
                    OutlinedButton(
                        onClick = onOpenFeed,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Feed, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Feed")
                    }
                }
            }
            
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

private data class KpiData(
    val title: String,
    val value: Double,
    val icon: ImageVector,
    val color: Color,
    val suffix: String = ""
)

@Composable
private fun HeroStatCard(
    title: String,
    value: Double,
    subtitle: String,
    icon: ImageVector,
    trend: Int? = null
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "revenue"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            title,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "₹${NumberFormat.getNumberInstance().format(animatedValue.toInt())}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp).size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                trend?.let {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (it >= 0) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFD32F2F).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (it >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = if (it >= 0) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${if (it >= 0) "+" else ""}$it% vs last week",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: Double,
    icon: ImageVector,
    color: Color,
    suffix: String = ""
) {
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                "${value.toInt()}$suffix",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrendChartRow(
    label: String,
    values: List<Double>,
    color: Color,
    format: (Double) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.4f)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                if (values.isNotEmpty()) format(values.first()) else "--",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        Box(modifier = Modifier.weight(0.6f).height(50.dp)) {
            SparklineChart(values = values.reversed(), color = color)
        }
    }
}

@Composable
private fun SparklineChart(values: List<Double>, color: Color) {
    if (values.size < 2) return
    val maxV = values.maxOrNull() ?: 0.0
    val minV = values.minOrNull() ?: 0.0
    val range = (maxV - minV).takeIf { it > 0 } ?: 1.0
    
    Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        val stepX = size.width / (values.size - 1)
        var prev: Offset? = null
        values.forEachIndexed { idx, v ->
            val x = idx * stepX
            val yRatio = ((v - minV) / range).toFloat()
            val y = size.height * (1f - yRatio)
            val cur = Offset(x, y)
            prev?.let { p ->
                drawLine(color, p, cur, strokeWidth = 3f, cap = StrokeCap.Round)
            }
            prev = cur
        }
        // Draw dots at each point
        values.forEachIndexed { idx, v ->
            val x = idx * stepX
            val yRatio = ((v - minV) / range).toFloat()
            val y = size.height * (1f - yRatio)
            drawCircle(color, radius = 4f, center = Offset(x, y))
        }
    }
}
