package com.rio.rostry.ui.monitoring

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FCRRating
import com.rio.rostry.data.repository.ProfitabilityRating
import com.rio.rostry.ui.monitoring.vm.FarmPerformanceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmPerformanceScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: FarmPerformanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Performance") },
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
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Period Selector
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.periodOptions) { days ->
                            FilterChip(
                                selected = selectedPeriod == days,
                                onClick = { viewModel.selectPeriod(days) },
                                label = { Text("${days}D") }
                            )
                        }
                    }
                }
                
                // Overview Cards
                item {
                    Text(
                        "Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Total Birds",
                            value = state.totalBirds.toString(),
                            icon = Icons.Default.Grain,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Batches",
                            value = state.activeBatches.toString(),
                            icon = Icons.Default.Assessment,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Feed Cost",
                            value = currencyFormat.format(state.totalFeedCost),
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Avg FCR",
                            value = if (state.averageFCR > 0) String.format("%.2f", state.averageFCR) else "--",
                            icon = Icons.Default.Assessment,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // FCR Details (if asset selected)
                state.fcrAnalysis?.let { fcr ->
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "FCR Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        FCRCard(fcr = fcr)
                    }
                }
                
                // Cost Breakdown (if asset selected)
                state.costAnalysis?.let { cost ->
                    item {
                        Text(
                            "Cost Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        CostBreakdownCard(cost = cost, currencyFormat = currencyFormat)
                    }
                }
                
                // Asset List
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your Assets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (state.assets.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No assets found")
                                Text(
                                    "Add your first batch to see performance metrics",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(state.assets) { asset ->
                        AssetPerformanceCard(
                            asset = asset,
                            isSelected = state.selectedAssetId == asset.assetId,
                            onClick = { asset.assetId?.let { viewModel.selectAsset(it) } }
                        )
                    }
                }
                
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun FCRCard(fcr: com.rio.rostry.data.repository.FCRAnalysis) {
    val rating = fcr.rating
    val ratingColor = when (rating) {
        FCRRating.EXCELLENT -> Color(0xFF2E7D32)
        FCRRating.GOOD -> Color(0xFF4CAF50)
        FCRRating.AVERAGE -> Color(0xFFFFC107)
        FCRRating.POOR -> Color(0xFFD32F2F)
        FCRRating.UNKNOWN -> Color.Gray
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Feed Conversion Ratio",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        fcr.ratingDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        String.format("%.2f", fcr.fcr),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ratingColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (fcr.percentageVsBenchmark > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (fcr.percentageVsBenchmark > 0) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "${String.format("%.0f", kotlin.math.abs(fcr.percentageVsBenchmark))}% vs benchmark",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // FCR Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val progress = (1 - (fcr.fcr / 4.0)).coerceIn(0.0, 1.0).toFloat()
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(1000),
                    label = "fcr_progress"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ratingColor)
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Feed Used", "${String.format("%.1f", fcr.totalFeedKg)} kg")
                StatItem("Weight Gain", "${String.format("%.2f", fcr.weightGainKg)} kg")
                StatItem("Benchmark", String.format("%.1f", fcr.industryBenchmark))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CostBreakdownCard(
    cost: com.rio.rostry.data.repository.CostPerBirdAnalysis,
    currencyFormat: NumberFormat
) {
    val profitColor = when (cost.profitabilityRating) {
        ProfitabilityRating.PROFITABLE -> Color(0xFF2E7D32)
        ProfitabilityRating.BREAK_EVEN -> Color(0xFFFFC107)
        ProfitabilityRating.LOSS -> Color(0xFFD32F2F)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cost/Bird", style = MaterialTheme.typography.titleSmall)
                Text(
                    currencyFormat.format(cost.costPerBird),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            CostRow("Feed", cost.totalFeedCost, cost.totalCost, currencyFormat)
            CostRow("Vaccines", cost.totalVaccineCost, cost.totalCost, currencyFormat)
            CostRow("Medication", cost.totalMedicationCost, cost.totalCost, currencyFormat)
            CostRow("Mortality Impact", cost.totalMortalityImpact, cost.totalCost, currencyFormat)
            CostRow("Other", cost.totalOtherExpenses, cost.totalCost, currencyFormat)
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Profit Margin", fontWeight = FontWeight.Bold)
                Text(
                    "${String.format("%.1f", cost.profitMargin)}%",
                    fontWeight = FontWeight.Bold,
                    color = profitColor
                )
            }
        }
    }
}

@Composable
private fun CostRow(
    label: String,
    amount: Double,
    total: Double,
    currencyFormat: NumberFormat
) {
    val percentage = if (total > 0) (amount / total * 100) else 0.0
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(
                "${String.format("%.0f", percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            currencyFormat.format(amount),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AssetPerformanceCard(
    asset: FarmAssetEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
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
                    asset.name ?: "Unnamed Asset",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Qty: ${asset.quantity ?: 0} • ${asset.status ?: "Active"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.Assessment,
                contentDescription = "View Details",
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
            )
        }
    }
}
