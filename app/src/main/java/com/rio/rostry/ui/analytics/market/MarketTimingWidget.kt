package com.rio.rostry.ui.analytics.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.service.SeasonType

@Composable
fun MarketTimingWidget(
    viewModel: MarketTimingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.marketState.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoGraph,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Market Intelligence",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            when (val currentState = state) {
                is MarketTimingState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MarketTimingState.Error -> {
                    Text("Failed to load insights", color = MaterialTheme.colorScheme.error)
                }
                is MarketTimingState.Success -> {
                    // 1. Seasonality Card
                    SeasonalityCard(currentState.seasonalContext)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 2. Best Time to Sell
                    BestTimeCard(currentState.bestTimeToSell)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 3. Growth Projection (if active batches)
                    // Simplified for widget view
                    if (currentState.growthPrediction.status != com.rio.rostry.domain.service.GrowthStatus.INSUFFICIENT_DATA) {
                        GrowthInsightRow(currentState.growthPrediction)
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonalityCard(context: com.rio.rostry.domain.service.SeasonalContext) {
    val (color, label) = when (context.seasonType) {
        SeasonType.PEAK -> Color(0xFF4CAF50) to "Peak Season"
        SeasonType.NORMAL -> Color(0xFFFFC107) to "Normal Season"
        SeasonType.LOW -> Color(0xFFFF5722) to "Low Season"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Current Market Demand",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        
        context.upcomingOpportunity?.let { opportunity ->
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = opportunity,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun BestTimeCard(prediction: com.rio.rostry.domain.service.BestTimeToSell) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Best Time to Sell",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${prediction.timing} (${prediction.recommendation})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (prediction.expectedPriceIncrease > 0) {
                Text(
                    text = "Expected +${prediction.expectedPriceIncrease.toInt()}% price",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun GrowthInsightRow(prediction: com.rio.rostry.domain.service.GrowthPrediction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.TrendingUp,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Active Batch Projection",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Ready in ~${prediction.weeksToOptimalWeight} weeks",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Growth: ${prediction.status.name.replace("_", " ")}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
