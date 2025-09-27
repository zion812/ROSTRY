package com.rio.rostry.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun EnthusiastDashboardScreen(
    vm: EnthusiastDashboardViewModel = hiltViewModel(),
    onOpenReports: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
    onOpenPerformance: () -> Unit = {},
    onOpenPerformanceDetail: (String) -> Unit = {},
    onOpenFinancial: () -> Unit = {},
    onOpenHealthIncidents: (String) -> Unit = {},
    onOpenTraceability: (String) -> Unit = {},
) {
    val d = vm.dashboard.collectAsState().value
    val sp = LocalSpacing.current
    Column(Modifier.fillMaxSize().padding(sp.lg), verticalArrangement = Arrangement.spacedBy(sp.sm)) {
        Text("Enthusiast Dashboard", style = MaterialTheme.typography.titleLarge)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Breeding Success Rate: ${"%.1f".format(d.breedingSuccessRate * 100)}%")
                Text("Transfers: ${d.transfers}")
                Text("Engagement Score (7d): ${"%.1f".format(d.engagementScore)}")
                if (d.suggestions.isNotEmpty()) {
                    Text("Suggestions:", modifier = Modifier.padding(top = sp.xs), style = MaterialTheme.typography.bodyMedium)
                    d.suggestions.forEach { s ->
                        Text("• $s", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Button(onClick = onOpenReports, modifier = Modifier.padding(top = sp.sm)) { Text("Open Reports") }
                Button(onClick = onOpenFeed, modifier = Modifier.padding(top = sp.xs)) { Text("Open Feed") }
            }
        }

        // Interactive KPI mini-chart (tap bars to drill down to PerformanceAnalytics with a metric key)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("KPI Overview")
                val barData = listOf(
                    BarDatum(label = "Transfers", value = d.transfers.toFloat(), metaId = null),
                    BarDatum(label = "Breeding%", value = (d.breedingSuccessRate * 100.0).toFloat(), metaId = null),
                    BarDatum(label = "Engagement", value = d.engagementScore.toFloat(), metaId = null),
                )
                BarChartView(data = barData, onBarClick = { datum ->
                    onOpenPerformanceDetail(datum.label)
                })
            }
        }

        // Health status distribution (tap slices to open filtered incidents)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Health Status Distribution")
                val slices = listOf(
                    PieSlice(label = "Healthy", value = 0f, color = androidx.compose.ui.graphics.Color(0xFF2E7D32)),
                    PieSlice(label = "Watch", value = 0f, color = androidx.compose.ui.graphics.Color(0xFFF9A825)),
                    PieSlice(label = "Critical", value = 0f, color = androidx.compose.ui.graphics.Color(0xFFC62828)),
                )
                PieChartView(slices = slices, onSliceClick = { slice ->
                    onOpenHealthIncidents(slice.label)
                })
            }
        }

        // Recent trend with metaId drilldown (tap points with metaId to open traceability)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Recent Trend")
                val linePoints = listOf(
                    LinePoint(xLabel = "W-2", y = (d.engagementScore * 0.8).toFloat(), metaId = null),
                    LinePoint(xLabel = "W-1", y = (d.engagementScore * 0.9).toFloat(), metaId = null),
                    LinePoint(xLabel = "Now", y = d.engagementScore.toFloat(), metaId = null),
                )
                LineChartView(points = linePoints, onPointClick = { pt ->
                    pt.metaId?.let { onOpenTraceability(it) } ?: onOpenPerformance()
                })
            }
        }
    }
}
