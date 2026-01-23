package com.rio.rostry.ui.admin.mortality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.admin.MortalityRiskLevel
import com.rio.rostry.data.repository.admin.OutbreakAlert
import com.rio.rostry.data.repository.admin.RegionalMortality

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortalityDashboardScreen(
    viewModel: MortalityDashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mortality Watch") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading && state.regions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Alerts Section
                if (state.alerts.isNotEmpty()) {
                    item {
                        Text(
                            "Active Outbreak Alerts",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(state.alerts) { alert ->
                        AlertCard(alert)
                    }
                    item { Divider() }
                }

                // Heatmap Section
                item {
                    Text(
                        "Regional Mortality Heatmap",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (state.regions.isEmpty()) {
                     item { Text("No data available.") }
                }

                items(state.regions) { region ->
                    RegionCard(region)
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: OutbreakAlert) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "POTENTIAL OUTBREAK: ${alert.regionName}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "${alert.affectedFarmsCount} farms affected with high mortality.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun RegionCard(region: RegionalMortality) {
    val color = when (region.riskLevel) {
        MortalityRiskLevel.CRITICAL -> Color(0xFFFFEBEE) // Light Red
        MortalityRiskLevel.HIGH -> Color(0xFFFFF3E0)     // Light Orange
        MortalityRiskLevel.MODERATE -> Color(0xFFFFFDE7) // Light Yellow
        MortalityRiskLevel.LOW -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val indicatorColor = when (region.riskLevel) {
        MortalityRiskLevel.CRITICAL -> Color.Red
        MortalityRiskLevel.HIGH -> Color(0xFFFF9800)
        MortalityRiskLevel.MODERATE -> Color(0xFFFFEB3B)
        MortalityRiskLevel.LOW -> Color.Green
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color Indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(indicatorColor, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = region.regionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${region.reportedDeaths24h} deaths in last 24h",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${region.totalFarms} Farms",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
