package com.rio.rostry.ui.farmer.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.domain.model.FeedRecommendation
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.ui.theme.ROSTRYTheme
/**
 * A card that displays the current feed recommendation and allows quick logging.
 */
@Composable
fun FeedRecommendationCard(
    recommendation: FeedRecommendation?,
    todayLogAmount: Double = 0.0,
    onLogClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onDetailClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recommendation?.feedType?.emoji ?: "🌾",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Suggested Feed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recommendation?.feedType?.displayName ?: "Loading...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (recommendation?.isLowInventoryAlert == true || recommendation?.daysRemaining != null) {
                    val daysLeft = recommendation?.daysRemaining
                    val (color, text) = when {
                        daysLeft == null -> Pair(MaterialTheme.colorScheme.error, "Low Stock")
                        daysLeft <= 3 -> Pair(MaterialTheme.colorScheme.error, "$daysLeft days left")
                        daysLeft <= 7 -> Pair(Color(0xFFFFB300), "$daysLeft days left") // Amber
                        else -> Pair(MaterialTheme.colorScheme.primary, "$daysLeft days supply")
                    }
                    
                    Surface(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (recommendation?.isLowInventoryAlert == true) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelSmall,
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Daily Target",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recommendation?.let { "%.1f kg".format(it.dailyFeedKg) } ?: "--",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                VerticalDivider(modifier = Modifier.height(32.dp))
                
                Column {
                    Text(
                        text = "Stage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recommendation?.stage?.displayName ?: "--",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Button(
                    onClick = onLogClick,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Feed")
                }
            }
            
            if (todayLogAmount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 
                        if (recommendation != null && recommendation.dailyFeedKg > 0) {
                            (todayLogAmount / recommendation.dailyFeedKg).toFloat().coerceIn(0f, 1f)
                        } else 0f
                    },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (todayLogAmount >= (recommendation?.dailyFeedKg ?: 0.0)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "Logged today: %.1f kg".format(todayLogAmount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
