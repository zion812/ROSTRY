package com.rio.rostry.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.domain.service.*

/**
 * Smart Insights Widget - AI-powered recommendations for farmer dashboard
 * 
 * Displays:
 * - Feed Conversion Ratio (FCR) gauge
 * - Mortality alerts
 * - Cost savings opportunities
 * - Growth trends
 */

@Composable
fun SmartInsightsWidget(
    insights: List<SmartInsight>,
    onInsightClick: (SmartInsight) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with AI badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Smart Insights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // AI Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (insights.isEmpty()) {
                EmptyInsightsState()
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(insights) { insight ->
                        InsightCard(
                            insight = insight,
                            onClick = { onInsightClick(insight) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: SmartInsight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, iconColor, icon) = when (insight.type) {
        InsightType.FEED_EFFICIENCY -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.1f),
            Color(0xFF4CAF50),
            Icons.Default.Restaurant
        )
        InsightType.COST_SAVINGS -> Triple(
            Color(0xFF2196F3).copy(alpha = 0.1f),
            Color(0xFF2196F3),
            Icons.Default.Savings
        )
        InsightType.MORTALITY_ALERT -> Triple(
            Color(0xFFE53935).copy(alpha = 0.1f),
            Color(0xFFE53935),
            Icons.Default.Warning
        )
        InsightType.GROWTH_TREND -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.1f),
            Color(0xFFFF9800),
            Icons.Default.TrendingUp
        )
        InsightType.PRICE_OPPORTUNITY -> Triple(
            Color(0xFF9C27B0).copy(alpha = 0.1f),
            Color(0xFF9C27B0),
            Icons.Default.AttachMoney
        )
    }
    
    val borderColor = when (insight.priority) {
        InsightPriority.CRITICAL -> Color(0xFFE53935)
        InsightPriority.HIGH -> Color(0xFFFF9800)
        else -> Color.Transparent
    }
    
    Card(
        modifier = modifier
            .width(200.dp)
            .then(
                if (insight.priority == InsightPriority.CRITICAL || insight.priority == InsightPriority.HIGH) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(16.dp))
                } else Modifier
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Priority indicator for critical/high
            if (insight.priority == InsightPriority.CRITICAL || insight.priority == InsightPriority.HIGH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    PulsingDot(
                        color = borderColor
                    )
                }
            }
            
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Title
            Text(
                text = insight.title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Value (large)
            Text(
                text = insight.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Description
            Text(
                text = insight.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            
            // Action button if available
            insight.actionLabel?.let { label ->
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PulsingDot(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
private fun EmptyInsightsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Insights,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No insights yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Log more data to unlock AI recommendations",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * FCR Gauge - Visual representation of Feed Conversion Ratio
 */
@Composable
fun FCRGauge(
    fcr: Float,
    rating: FCRRating,
    modifier: Modifier = Modifier
) {
    val color = when (rating) {
        FCRRating.EXCELLENT -> Color(0xFF4CAF50)
        FCRRating.GOOD -> Color(0xFF8BC34A)
        FCRRating.AVERAGE -> Color(0xFFFF9800)
        FCRRating.POOR -> Color(0xFFE53935)
        FCRRating.UNKNOWN -> Color.Gray
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Feed Conversion Ratio",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Large FCR value
            Text(
                text = if (fcr > 0) String.format("%.2f", fcr) else "--",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            // Rating badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.2f)
            ) {
                Text(
                    text = rating.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Scale reference
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Excellent", fontSize = 10.sp, color = Color(0xFF4CAF50))
                Text("Good", fontSize = 10.sp, color = Color(0xFF8BC34A))
                Text("Avg", fontSize = 10.sp, color = Color(0xFFFF9800))
                Text("Poor", fontSize = 10.sp, color = Color(0xFFE53935))
            }
            
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF8BC34A),
                                Color(0xFFFF9800),
                                Color(0xFFE53935)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Mortality Alert Card - Warning display for mortality spikes
 */
@Composable
fun MortalityAlertCard(
    analysis: MortalityAnalysis,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trendIcon = when (analysis.trend) {
        MortalityTrend.INCREASING -> Icons.Default.TrendingUp
        MortalityTrend.DECREASING -> Icons.Default.TrendingDown
        MortalityTrend.STABLE -> Icons.Default.TrendingFlat
    }
    
    val trendColor = when (analysis.trend) {
        MortalityTrend.INCREASING -> Color(0xFFE53935)
        MortalityTrend.DECREASING -> Color(0xFF4CAF50)
        MortalityTrend.STABLE -> Color(0xFFFF9800)
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (analysis.trend == MortalityTrend.INCREASING) 
                Color(0xFFE53935).copy(alpha = 0.1f) 
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonitorHeart,
                        contentDescription = null,
                        tint = trendColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mortality Monitor",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    imageVector = trendIcon,
                    contentDescription = null,
                    tint = trendColor
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Total", value = "${analysis.totalDeaths}")
                StatItem(label = "Rate", value = "${String.format("%.1f", analysis.mortalityRate)}%")
                StatItem(label = "Last 7d", value = "${analysis.recentDeaths}")
            }
            
            if (analysis.alerts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                analysis.alerts.forEach { alert ->
                    Text(
                        text = alert,
                        style = MaterialTheme.typography.bodySmall,
                        color = trendColor
                    )
                }
            }
            
            if (analysis.trend == MortalityTrend.INCREASING) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = trendColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Investigate Now")
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
