package com.rio.rostry.ui.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.geometry.Offset

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
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(16.dp)) {
            Text("Farmer Dashboard", style = MaterialTheme.typography.titleMedium)
            Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Text("Revenue: ${"%.2f".format(d.revenue)}")
                    Text("Orders: ${d.orders}")
                    Text("Product Views: ${d.productViews}")
                    Text("Engagement Score (7d): ${"%.1f".format(d.engagementScore)}")
                    if (d.suggestions.isNotEmpty()) {
                        Text("Suggestions:", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
                        d.suggestions.forEach { s ->
                            Text("• $s", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Button(onClick = onOpenReports, modifier = Modifier.padding(top = 12.dp)) { Text("Open Reports") }
                    Button(onClick = onOpenFeed, modifier = Modifier.padding(top = 8.dp)) { Text("Open Feed") }
                    Button(onClick = {
                        // Build KPI map and export
                        val kpis = mapOf(
                            "Orders" to d.orders,
                            "Product Views" to d.productViews,
                            "Engagement Score (7d)" to d.engagementScore
                        )
                        val units = mapOf(
                            "Orders" to "count",
                            "Product Views" to "views",
                            "Engagement Score (7d)" to "score"
                        )
                        val res = com.rio.rostry.utils.export.CsvExporter.exportKpis(
                            context,
                            kpis,
                            fileName = "farmer_kpis.csv",
                            dateRange = null,
                            units = units
                        )
                        when (res) {
                            is com.rio.rostry.utils.Resource.Success -> {
                                res.data?.let { com.rio.rostry.utils.export.CsvExporter.showExportNotification(context, it) }
                                scope.launch { snackbarHostState.showSnackbar("Report exported") }
                            }
                            is com.rio.rostry.utils.Resource.Error -> scope.launch { snackbarHostState.showSnackbar(res.message ?: "Export failed") }
                            else -> {}
                        }
                    }, modifier = Modifier.padding(top = 8.dp)) { Text("Export CSV") }

                    Button(onClick = {
                        val kpis = mapOf(
                            "Orders" to d.orders,
                            "Product Views" to d.productViews,
                            "Engagement Score (7d)" to d.engagementScore
                        )
                        val units = mapOf(
                            "Orders" to "count",
                            "Product Views" to "views",
                            "Engagement Score (7d)" to "score"
                        )
                        val res = com.rio.rostry.utils.export.CsvExporter.exportKpis(
                            context,
                            kpis,
                            fileName = "farmer_kpis.csv",
                            dateRange = null,
                            units = units
                        )
                        if (res is com.rio.rostry.utils.Resource.Success) {
                            res.data?.let { uri ->
                                val intent = com.rio.rostry.utils.export.CsvExporter.shareCsv(context, uri)
                                context.startActivity(intent)
                            }
                        }
                    }, modifier = Modifier.padding(top = 8.dp)) { Text("Share CSV") }
                }
            }

            // Trend charts
            if (last4.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Trends (last ${last4.size} weeks)", style = MaterialTheme.typography.titleMedium)

                        // Revenue trend
                        Text("Revenue (INR)")
                        MiniLineChart(values = last4.map { it.revenueInr })

                        Spacer(Modifier.height(8.dp))
                        // Mortality rate trend
                        Text("Mortality Rate")
                        MiniLineChart(values = last4.map { it.mortalityRate.coerceIn(0.0, 1.0) }, color = Color(0xFFD32F2F))

                        Spacer(Modifier.height(8.dp))
                        // Hatch success trend
                        Text("Hatch Success Rate")
                        MiniLineChart(values = last4.map { it.hatchSuccessRate.coerceIn(0.0, 1.0) }, color = Color(0xFF388E3C))
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniLineChart(values: List<Double>, color: Color = Color(0xFF1976D2)) {
    if (values.size < 2) return
    val maxV = (values.maxOrNull() ?: 0.0)
    val minV = values.minOrNull() ?: 0.0
    val range = (maxV - minV).takeIf { it > 0 } ?: 1.0
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(vertical = 4.dp)) {
        val stepX = if (values.size > 1) size.width / (values.size - 1) else size.width
        var prev: Offset? = null
        values.forEachIndexed { idx, v ->
            val x = idx * stepX
            val yRatio = ((v - minV) / range).toFloat()
            val y = size.height * (1f - yRatio)
            val cur = Offset(x, y)
            prev?.let { p ->
                drawLine(color, p, cur, strokeWidth = 4f)
            }
            prev = cur
        }
    }
}
