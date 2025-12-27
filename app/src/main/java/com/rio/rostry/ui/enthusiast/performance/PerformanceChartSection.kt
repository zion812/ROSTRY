package com.rio.rostry.ui.enthusiast.performance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

/**
 * Performance Chart Section - Shows weight growth over time.
 * Uses Vico library for beautiful line charts.
 */
@Composable
fun PerformanceChartSection(
    weightData: List<Pair<Int, Double>>, // (weekNumber, weightGrams)
    productName: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            // Collapsed summary
            if (!isExpanded && weightData.isNotEmpty()) {
                val latest = weightData.maxByOrNull { it.first }
                val first = weightData.minByOrNull { it.first }
                val gainGrams = ((latest?.second ?: 0.0) - (first?.second ?: 0.0)).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatMini(
                        label = "Current",
                        value = "${latest?.second?.toInt() ?: 0}g",
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatMini(
                        label = "Gain",
                        value = "+${gainGrams}g",
                        color = Color(0xFF4CAF50)
                    )
                    StatMini(
                        label = "Records",
                        value = "${weightData.size}",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Expanded chart
            if (isExpanded) {
                Spacer(Modifier.height(16.dp))

                if (weightData.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No weight records yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    WeightGrowthChart(
                        weightData = weightData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LegendItem(color = MaterialTheme.colorScheme.primary, label = "Weight (grams)")
                    }
                }
            }
        }
    }
}

@Composable
private fun WeightGrowthChart(
    weightData: List<Pair<Int, Double>>,
    modifier: Modifier = Modifier
) {
    val sortedData = weightData.sortedBy { it.first }

    val chartEntryModelProducer = remember(sortedData) {
        ChartEntryModelProducer(
            sortedData.mapIndexed { index, (week, weight) ->
                entryOf(index.toFloat(), weight.toFloat())
            }
        )
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        val index = value.toInt()
        if (index in sortedData.indices) {
            "W${sortedData[index].first}"
        } else {
            ""
        }
    }

    Chart(
        chart = lineChart(
            lines = listOf(
                LineChart.LineSpec(
                    lineColor = 0xFF6200EE.toInt(),
                    lineThicknessDp = 2f
                )
            )
        ),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
        modifier = modifier,
        chartScrollState = rememberChartScrollState()
    )
}

@Composable
private fun StatMini(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = RoundedCornerShape(3.dp),
            color = color
        ) {}
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
