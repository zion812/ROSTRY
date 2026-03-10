package com.rio.rostry.ui.farmer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf

/**
 * A visually rich card that displays a sparkline trend chart alongside the metric.
 * Part of the "Farmer Role Enhancement" to provide visual analytics.
 */
@Composable
fun TrendFetcherCard(
    title: String,
    count: String,
    trendData: List<Float>,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp) // Same height as FetcherCardItem
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Left Side: Icon and Metric
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = count,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Right Side: Sparkline
            if (trendData.isNotEmpty() && trendData.size > 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                ) {
                    SimpleSparkline(data = trendData, color = color)
                }
            } else {
                 // Fallback if no trend data or single point
                 Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SimpleSparkline(data: List<Float>, color: Color) {
    val entries = remember(data) {
        data.mapIndexed { index, value -> FloatEntry(index.toFloat(), value) }
    }
    
    val chartEntryModel = remember(entries) { entryModelOf(entries) }
    
    // Create a simplified line chart style for the sparkline
    val chart = lineChart(
        lines = listOf(
            LineChart.LineSpec(
                lineColor = color.toArgb(),
                lineThicknessDp = 3f,
                lineBackgroundShader = null, // No fill for cleaner look
                pointSizeDp = 0f // No points
            )
        )
    )

    Chart(
        chart = chart,
        model = chartEntryModel,
        modifier = Modifier.fillMaxSize(),
        isZoomEnabled = false,
        // Hide axes for sparkline look
        startAxis = null,
        bottomAxis = null,
        marker = null,
    )
}
