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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EnthusiastDashboardScreen(
    vm: EnthusiastDashboardViewModel = hiltViewModel(),
    onOpenReports: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
    onOpenPerformance: () -> Unit = {},
    onOpenFinancial: () -> Unit = {},
    onOpenTraceability: (String) -> Unit = {},
) {
    val d = vm.dashboard.collectAsState().value
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Enthusiast Dashboard", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Breeding Success Rate: ${"%.1f".format(d.breedingSuccessRate * 100)}%")
                Text("Transfers: ${d.transfers}")
                Text("Engagement Score (7d): ${"%.1f".format(d.engagementScore)}")
                if (d.suggestions.isNotEmpty()) {
                    Text("Suggestions:", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
                    d.suggestions.forEach { s ->
                        Text("• $s", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Button(onClick = onOpenReports, modifier = Modifier.padding(top = 12.dp)) { Text("Open Reports") }
                Button(onClick = onOpenFeed, modifier = Modifier.padding(top = 8.dp)) { Text("Open Feed") }
            }
        }

        // Interactive KPI mini-chart (tap bars to drill down)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("KPI Overview")
                val barData = listOf(
                    BarDatum(label = "Transfers", value = d.transfers.toFloat(), metaId = null),
                    BarDatum(label = "Breeding%", value = (d.breedingSuccessRate * 100.0).toFloat(), metaId = null),
                    BarDatum(label = "Engagement", value = d.engagementScore.toFloat(), metaId = null),
                )
                BarChartView(data = barData, onBarClick = { datum ->
                    when (datum.label) {
                        "Transfers", "Breeding%", "Engagement" -> onOpenPerformance()
                        else -> onOpenPerformance()
                    }
                })
            }
        }
    }
}
