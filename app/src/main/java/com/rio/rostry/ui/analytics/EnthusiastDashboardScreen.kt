package com.rio.rostry.ui.analytics

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
) {
    val d = vm.dashboard.collectAsState().value
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Enthusiast Dashboard", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
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
    }
}
