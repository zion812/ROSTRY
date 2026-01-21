package com.rio.rostry.ui.enthusiast.hatchability

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.analytics.EnthusiastDashboardViewModel

/**
 * Hatchability Tracker Dashboard - Shows overall hatchability metrics and pair summaries.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HatchabilityTrackerScreen(
    viewModel: EnthusiastDashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPairClick: (String) -> Unit
) {
    val dashboard by viewModel.dashboard.collectAsState()
    val pairs by viewModel.breedingPairStats.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hatchability Tracker") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Overall Rate",
                        value = "${(dashboard.breedingSuccessRate * 100).toInt()}%",
                        icon = Icons.Filled.EggAlt
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Trend",
                        value = "${if (viewModel.flockHealthTrend.collectAsState().value >= 0) "+" else ""}${viewModel.flockHealthTrend.collectAsState().value.toInt()}%",
                        icon = Icons.Filled.TrendingUp
                    )
                }
            }
            
            item {
                Text(
                    "Active Breeding Pairs (${pairs.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            if (pairs.isEmpty()) {
                item {
                    Text(
                        "No active breeding pairs found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(pairs) { pair ->
                    Card(
                        onClick = { onPairClick(pair.pairId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(pair.pairName, style = MaterialTheme.typography.titleSmall)
                                Text("${pair.eggsCollected} eggs collected", style = MaterialTheme.typography.bodySmall)
                            }
                            // Show hatch rate or "No Data"
                            val rateText = if (pair.eggsCollected > 0) "${(pair.hatchRate * 100).toInt()}%" else "--"
                            Text(rateText, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
            }
            
            item {
                OutlinedButton(
                    onClick = { /* Navigate to egg collection */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log New Collection")
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
