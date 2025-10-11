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

@Composable
fun FarmerDashboardScreen(
    vm: FarmerDashboardViewModel = hiltViewModel(),
    onOpenReports: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
) {
    val d = vm.dashboard.collectAsState().value
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
                        val res = com.rio.rostry.utils.export.CsvExporter.exportKpis(
                            context,
                            kpis,
                            fileName = "farmer_kpis.csv",
                            dateRange = null
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
                }
            }
        }
    }
}
