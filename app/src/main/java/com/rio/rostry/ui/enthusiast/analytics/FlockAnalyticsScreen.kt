package com.rio.rostry.ui.enthusiast.analytics

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.service.BreedingROIService
import com.rio.rostry.domain.service.FlockProductivityService

private val GoldAccent = Color(0xFFFFC107)
private val PurplePrimary = Color(0xFF673AB7)
private val ProfitGreen = Color(0xFF4CAF50)
private val LossRed = Color(0xFFF44336)
private val InfoBlue = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockAnalyticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBirdProfile: (String) -> Unit = {},
    viewModel: FlockAnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Overview", "Leaderboard", "Breeds")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flock Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurplePrimary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PurplePrimary)
                        Spacer(Modifier.height(16.dp))
                        Text("Analyzing your flock…", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "Error", color = LossRed)
                }
            }
            uiState.summary != null -> {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    // Tabs
                    TabRow(
                        selectedTabIndex = uiState.activeTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = PurplePrimary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = uiState.activeTab == index,
                                onClick = { viewModel.selectTab(index) },
                                text = { Text(title) }
                            )
                        }
                    }

                    when (uiState.activeTab) {
                        0 -> OverviewTab(uiState.summary!!, uiState.selectedBirdROI, viewModel)
                        1 -> LeaderboardTab(uiState.summary!!, onNavigateToBirdProfile, viewModel)
                        2 -> BreedsTab(uiState.summary!!)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(
    summary: FlockProductivityService.FlockSummary,
    selectedROI: BreedingROIService.BirdROI?,
    viewModel: FlockAnalyticsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // KPI Row
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiCard("Birds", "${summary.totalBirds}", Icons.Filled.Pets, InfoBlue, Modifier.weight(1f))
                KpiCard("BVI Avg", "${(summary.avgBvi * 100).toInt()}", Icons.Filled.Science, PurplePrimary, Modifier.weight(1f))
                KpiCard("Chicks", "${summary.totalOffspring}", Icons.Filled.ChildCare, GoldAccent, Modifier.weight(1f))
            }
        }

        // Financial Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Financial Overview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    FinancialRow("Total Revenue", summary.totalRevenue, ProfitGreen)
                    FinancialRow("Total Cost", summary.totalCost, LossRed)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    FinancialRow("Net Profit", summary.netProfit,
                        if (summary.netProfit >= 0) ProfitGreen else LossRed,
                        bold = true
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Overall ROI", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${String.format("%.1f", summary.overallROI)}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (summary.overallROI >= 0) ProfitGreen else LossRed
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Avg Cost/Chick", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "₹${String.format("%.0f", summary.avgCostPerChick)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Flock Composition
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Flock Composition", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("♂", fontSize = 28.sp, color = Color(0xFF00BCD4))
                            Text("${summary.totalMales}", fontWeight = FontWeight.Bold)
                            Text("Males", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("♀", fontSize = 28.sp, color = Color(0xFFE91E63))
                            Text("${summary.totalFemales}", fontWeight = FontWeight.Bold)
                            Text("Females", style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐣", fontSize = 28.sp)
                            Text("${summary.totalOffspring}", fontWeight = FontWeight.Bold)
                            Text("Offspring", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Top Performers
        if (summary.topPerformers.isNotEmpty()) {
            item {
                Text("🏅 Top Performers", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            items(summary.topPerformers) { performer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(performer.bird.name ?: "Unknown", fontWeight = FontWeight.SemiBold)
                            Text(performer.category, style = MaterialTheme.typography.bodySmall, color = PurplePrimary)
                        }
                        Text(performer.value, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTab(
    summary: FlockProductivityService.FlockSummary,
    onNavigateToBirdProfile: (String) -> Unit,
    viewModel: FlockAnalyticsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(summary.birdLeaderboard) { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBirdProfile(entry.bird.productId) }
                    .animateContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = when (entry.rank) {
                        1 -> Color(0xFFFFF9C4) // Gold tint
                        2 -> Color(0xFFE0E0E0) // Silver tint
                        3 -> Color(0xFFD7CCC8) // Bronze tint
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(if (entry.rank <= 3) 4.dp else 1.dp)
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (entry.rank) {
                                    1 -> GoldAccent
                                    2 -> Color(0xFFB0BEC5)
                                    3 -> Color(0xFF8D6E63)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "#${entry.rank}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                entry.bird.name ?: "Unknown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(entry.badge, fontSize = 12.sp)
                        }
                        Text(
                            "${entry.bird.breed ?: "?"} • ${entry.offspringCount} chicks • ${entry.showWins} wins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Productivity score
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${entry.productivityScore.toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = productivityColor(entry.productivityScore)
                        )
                        Text(
                            "Score",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        if (summary.birdLeaderboard.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No birds to rank yet", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun BreedsTab(summary: FlockProductivityService.FlockSummary) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(summary.breedBreakdown) { breed ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(breed.breed, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${breed.count} birds", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Spacer(Modifier.height(8.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatChip("BVI", "${(breed.avgBvi * 100).toInt()}", PurplePrimary)
                        StatChip("ROI", "${String.format("%.0f", breed.avgROI)}%",
                            if (breed.avgROI >= 0) ProfitGreen else LossRed)
                        StatChip("Chicks", "${breed.totalOffspring}", GoldAccent)
                        StatChip("₹/Chick", String.format("%.0f", breed.avgCostPerChick), InfoBlue)
                    }

                    breed.topBird?.let { name ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "⭐ Top: $name",
                            style = MaterialTheme.typography.bodySmall,
                            color = PurplePrimary
                        )
                    }
                }
            }
        }

        if (summary.breedBreakdown.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No breed data available", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ─── Helper Composables ─────────────────────────────────────────────────

@Composable
private fun KpiCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun FinancialRow(label: String, amount: Double, color: Color, bold: Boolean = false) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(
            "₹${String.format("%,.0f", amount)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

@Composable
private fun productivityColor(score: Float): Color = when {
    score >= 70f -> ProfitGreen
    score >= 50f -> Color(0xFF8BC34A)
    score >= 30f -> GoldAccent
    else -> Color(0xFFFF9800)
}
