package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.EnthusiastGold

/**
 * Individual stat item for the QuickStatsRow.
 */
data class QuickStat(
    val value: String,
    val label: String,
    val icon: ImageVector? = null,
    val trend: Float? = null,  // Positive = up, Negative = down
    val color: Color = Color.Unspecified
)

/**
 * Amazon Alexa app-inspired horizontal stat bubbles with micro-interactions.
 * Shows key KPIs in a horizontally scrollable row with tap-to-drill-down.
 * 
 * Features:
 * - Horizontal scroll with momentum
 * - Spring scale animation on press
 * - Trend indicators (up/down arrows)
 * - Gradient backgrounds for visual hierarchy
 */
@Composable
fun QuickStatsRow(
    stats: List<QuickStat>,
    onStatClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.width(4.dp))
        
        stats.forEachIndexed { index, stat ->
            QuickStatBubble(
                stat = stat,
                onClick = { onStatClick(index) }
            )
        }
        
        Spacer(Modifier.width(4.dp))
    }
}

@Composable
private fun QuickStatBubble(
    stat: QuickStat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bubbleScale"
    )
    
    val baseColor = if (stat.color != Color.Unspecified) {
        stat.color
    } else {
        MaterialTheme.colorScheme.primary
    }
    
    Surface(
        modifier = modifier
            .scale(scale)
            .semantics {
                contentDescription = "${stat.label}: ${stat.value}"
                role = Role.Button
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        color = baseColor.copy(alpha = 0.1f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Value with optional trend indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = baseColor
                )
                
                stat.trend?.let { trend ->
                    val trendIcon = if (trend >= 0) "↑" else "↓"
                    val trendColor = if (trend >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    Text(
                        text = trendIcon,
                        fontSize = 14.sp,
                        color = trendColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Label
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Predefined stats for common Enthusiast metrics.
 */
object EnthusiastStats {
    fun breedingSuccess(rate: Float) = QuickStat(
        value = "${(rate * 100).toInt()}%",
        label = "Breeding",
        icon = Icons.Filled.Favorite,
        color = Color(0xFFE91E63)
    )
    
    fun transfers(count: Int, trend: Float? = null) = QuickStat(
        value = count.toString(),
        label = "Transfers",
        icon = Icons.Filled.Send,
        trend = trend,
        color = Color(0xFF2196F3)
    )
    
    fun trustScore(score: Float) = QuickStat(
        value = "${score.toInt()}%",
        label = "Trust",
        icon = Icons.Filled.Verified,
        color = EnthusiastGold
    )
    
    fun engagement(score: Float, trend: Float? = null) = QuickStat(
        value = "%.1f".format(score),
        label = "Engagement",
        icon = Icons.Filled.TrendingUp,
        trend = trend,
        color = Color(0xFF673AB7)
    )
}
