package com.rio.rostry.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import com.rio.rostry.ui.enthusiast.components.HeroStatCard
import com.rio.rostry.ui.enthusiast.components.SparklineChart
import com.rio.rostry.ui.enthusiast.components.AchievementBanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EnthusiastDashboardScreen(
    vm: EnthusiastDashboardViewModel = hiltViewModel(),
    onOpenReports: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
    onOpenPerformance: () -> Unit = {},
    onOpenFinancial: () -> Unit = {},
    onOpenTraceability: (String) -> Unit = {},
    onOpenEggCollection: () -> Unit = {},
    onKpiTap: (String) -> Unit = {}
) {
    val d = vm.dashboard.collectAsState().value
    val flockHealthScore by vm.flockHealthScore.collectAsState()
    val flockHealthTrend by vm.flockHealthTrend.collectAsState()
    val sparklineData by vm.sparklineData.collectAsState()
    val achievements by vm.achievements.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Animation state for staggered card reveal
    var visibleItems by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        for (i in 1..6) {
            delay(100L)
            visibleItems = i
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Achievement Banner at top (Comment 2)
        if (achievements.isNotEmpty()) {
            item {
                val firstAchievement = achievements.first()
                AnimatedVisibility(
                    visible = visibleItems >= 1,
                    enter = fadeIn() + slideInVertically { -it }
                ) {
                    // Convert local Achievement to component Achievement
                    val componentAchievement = com.rio.rostry.ui.enthusiast.components.Achievement(
                        id = firstAchievement.id,
                        title = firstAchievement.title,
                        description = firstAchievement.description,
                        icon = firstAchievement.emoji
                    )
                    AchievementBanner(
                        achievement = componentAchievement,
                        onDismiss = { /* Could track dismissed */ }
                    )
                }
            }
        }
        
        // Hero Stat Card - Flock Health (Comment 2)
        item {
            AnimatedVisibility(
                visible = visibleItems >= 2,
                enter = fadeIn() + slideInVertically { -it / 2 }
            ) {
                HeroStatCard(
                    title = "YOUR FLOCK HEALTH",
                    value = flockHealthScore,
                    maxValue = 100f,
                    unit = "%",
                    previousValue = if (flockHealthTrend != 0f) flockHealthScore - flockHealthTrend else null,
                    contextText = when {
                        flockHealthTrend > 0 -> "↑ Improving"
                        flockHealthTrend < 0 -> "↓ Declining"
                        else -> "→ Stable"
                    },
                    onClick = { onKpiTap("health") }
                )
            }
        }
        
        item {
            Text(
                "Performance Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        // KPI Cards with Sparklines (Comment 2)
        item {
            AnimatedVisibility(
                visible = visibleItems >= 3,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                KpiCardWithSparkline(
                    label = "Breeding Success",
                    value = "${"%.0f".format(d.breedingSuccessRate * 100)}%",
                    sparklineData = sparklineData["breeding"] ?: emptyList(),
                    onClick = { onKpiTap("breeding") }
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = visibleItems >= 4,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                KpiCardWithSparkline(
                    label = "Transfers",
                    value = "${d.transfers}",
                    sparklineData = sparklineData["transfers"] ?: emptyList(),
                    onClick = { onKpiTap("transfers") }
                )
            }
        }
        
        item {
            AnimatedVisibility(
                visible = visibleItems >= 5,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                KpiCardWithSparkline(
                    label = "Engagement (7d)",
                    value = "${"%.1f".format(d.engagementScore)}",
                    sparklineData = sparklineData["birds"] ?: emptyList(),
                    onClick = { onKpiTap("engagement") }
                )
            }
        }

        // Action Buttons
        item {
            AnimatedVisibility(
                visible = visibleItems >= 6,
                enter = fadeIn()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenReports,
                            modifier = Modifier.weight(1f)
                        ) { Text("Reports") }
                        
                        Button(
                            onClick = onOpenEggCollection,
                            modifier = Modifier.weight(1f)
                        ) { Text("Log Eggs") }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val kpis = mapOf(
                                    "Breeding Success Rate (%)" to (d.breedingSuccessRate * 100.0),
                                    "Transfers" to d.transfers,
                                    "Engagement Score (7d)" to d.engagementScore
                                )
                                val units = mapOf(
                                    "Breeding Success Rate (%)" to "%",
                                    "Transfers" to "count",
                                    "Engagement Score (7d)" to "score"
                                )
                                val res = com.rio.rostry.utils.export.CsvExporter.exportKpis(
                                    context,
                                    kpis,
                                    fileName = "enthusiast_kpis.csv",
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
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Export") }
                        
                        Button(
                            onClick = onOpenFeed,
                            modifier = Modifier.weight(1f)
                        ) { Text("Feed") }
                    }
                }
            }
        }
        
        // Suggestions
        if (d.suggestions.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "💡 Suggestions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        d.suggestions.forEach { s ->
                            Text("• $s", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

/**
 * KPI Card with inline sparkline chart.
 */
@Composable
private fun KpiCardWithSparkline(
    label: String,
    value: String,
    sparklineData: List<Float>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (sparklineData.isNotEmpty()) {
                SparklineChart(
                    dataPoints = sparklineData,
                    modifier = Modifier
                        .width(80.dp)
                        .height(40.dp)
                )
            }
        }
    }
}

