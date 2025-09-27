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
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun FarmerDashboardScreen(
    vm: FarmerDashboardViewModel = hiltViewModel(),
    onOpenReports: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
) {
    val d = vm.dashboard.collectAsState().value
    val sp = LocalSpacing.current
    Column(Modifier.fillMaxSize().padding(sp.lg)) {
        Text("Farmer Dashboard", style = MaterialTheme.typography.titleLarge)
        Card(Modifier.fillMaxWidth().padding(top = sp.sm)) {
            Column(Modifier.fillMaxWidth().padding(sp.sm)) {
                Text("Revenue: ${"%.2f".format(d.revenue)}")
                Text("Orders: ${d.orders}")
                Text("Product Views: ${d.productViews}")
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
    }
}
