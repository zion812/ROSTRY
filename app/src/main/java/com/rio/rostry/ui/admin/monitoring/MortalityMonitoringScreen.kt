package com.rio.rostry.ui.admin.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.admin.MortalityRiskLevel
import com.rio.rostry.data.repository.admin.OutbreakAlert
import com.rio.rostry.data.repository.admin.RegionalMortality
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortalityMonitoringScreen(
    viewModel: MortalityMonitoringViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mortality Monitoring") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
            // Alert Summary
            item {
                Text(
                    "Risk Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RiskCard(
                        modifier = Modifier.weight(1f),
                        title = "Outbreaks",
                        count = state.activeOutbreaks,
                        color = Color(0xFFD32F2F),
                        icon = Icons.Default.Warning
                    )
                    RiskCard(
                        modifier = Modifier.weight(1f),
                        title = "Critical",
                        count = state.criticalRegions,
                        color = Color(0xFFFF5722),
                        icon = Icons.Default.Error
                    )
                    RiskCard(
                        modifier = Modifier.weight(1f),
                        title = "High Risk",
                        count = state.highRiskRegions,
                        color = Color(0xFFFF9800),
                        icon = Icons.Default.TrendingUp
                    )
                }
            }

            // Outbreak Alerts Section
            if (state.outbreakAlerts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "⚠️ Active Outbreak Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }

                items(state.outbreakAlerts) { alert ->
                    OutbreakAlertCard(alert = alert)
                }
            }

            // Regional Stats
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Regional Mortality Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.regionalStats.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No regional data available")
                        }
                    }
                }
            } else {
                items(state.regionalStats.sortedByDescending { it.riskLevel }) { stat ->
                    RegionalStatCard(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun RiskCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
private fun OutbreakAlertCard(alert: OutbreakAlert) {
    val color = when (alert.severity) {
        MortalityRiskLevel.CRITICAL -> Color(0xFFD32F2F)
        MortalityRiskLevel.HIGH -> Color(0xFFFF5722)
        MortalityRiskLevel.MODERATE -> Color(0xFFFF9800)
        MortalityRiskLevel.LOW -> Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        alert.regionName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = color, shape = MaterialTheme.shapes.small) {
                        Text(
                            alert.severity.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
                Text(alert.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${alert.affectedFarmsCount} farms affected • Detected ${SimpleDateFormat("MMM dd HH:mm", Locale.getDefault()).format(Date(alert.detectedAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RegionalStatCard(stat: RegionalMortality) {
    val color = when (stat.riskLevel) {
        MortalityRiskLevel.CRITICAL -> Color(0xFFD32F2F)
        MortalityRiskLevel.HIGH -> Color(0xFFFF5722)
        MortalityRiskLevel.MODERATE -> Color(0xFFFF9800)
        MortalityRiskLevel.LOW -> Color(0xFF4CAF50)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Risk Indicator
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${String.format("%.1f", stat.averageMortalityRate)}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stat.regionName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
                        Text(
                            stat.riskLevel.name,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color
                        )
                    }
                }
                Text(
                    "${stat.totalFarms} farms • ${stat.reportedDeaths24h} deaths (24h)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
