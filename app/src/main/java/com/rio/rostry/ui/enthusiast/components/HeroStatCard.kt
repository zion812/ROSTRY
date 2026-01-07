package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.ui.components.PremiumCard
import com.rio.rostry.ui.components.pulse
import com.rio.rostry.ui.theme.Coral
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.Emerald
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.RostryBlue
import kotlinx.coroutines.delay

/**
 * Nike Run Club-inspired hero stat card.
 * Features:
 * - Large hero metric display
 * - Trend indicator with percentage change
 * - Animated progress bar
 * - Context explanation
 */
@Composable
fun HeroStatCard(
    title: String,
    value: Float,
    maxValue: Float = 100f,
    unit: String = "%",
    previousValue: Float? = null,
    contextText: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    
    // Calculate trend
    val trend = previousValue?.let { ((value - it) / it) * 100 }
    val isTrendPositive = trend?.let { it >= 0 } ?: true
    
    // Animate progress
    var animatedProgress by remember { mutableStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(1000),
        label = "heroProgress"
    )
    
    LaunchedEffect(value) {
        animatedProgress = (value / maxValue).coerceIn(0f, 1f)
    }
    
    // Determine color based on value
    val valueColor = when {
        value >= 80 -> Emerald
        value >= 60 -> EnthusiastGold
        value >= 40 -> Color(0xFFFFA726)
        else -> Coral
    }
    
    PremiumCard(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Title
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Hero value with trend
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "%.1f".format(value),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.headlineMedium,
                    color = valueColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Trend indicator
                trend?.let {
                    Spacer(Modifier.weight(1f))
                    TrendIndicator(
                        trend = it,
                        isPositive = isTrendPositive,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Animated progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(valueColor.copy(alpha = 0.7f), valueColor)
                            )
                        )
                )
            }
            
            // Context text
            contextText?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun TrendIndicator(
    trend: Float,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isPositive) Emerald else Coral
    val icon = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown
    val prefix = if (isPositive) "+" else ""
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (isPositive) "Trending up" else "Trending down",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$prefix%.1f%%".format(trend),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

/**
 * Achievement data class.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji
    val unlockedAt: Long = System.currentTimeMillis()
)

/**
 * Slide-in achievement unlock notification banner.
 * Features:
 * - Golden pulsing badge
 * - Slide-in animation
 * - Auto-dismiss after delay
 * - Manual dismiss
 */
@Composable
fun AchievementBanner(
    achievement: Achievement?,
    modifier: Modifier = Modifier,
    autoDismissMillis: Long = 5000,
    onDismiss: () -> Unit = {}
) {
    var visible by remember(achievement) { mutableStateOf(achievement != null) }
    val haptic = LocalHapticFeedback.current
    
    LaunchedEffect(achievement) {
        if (achievement != null) {
            visible = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(autoDismissMillis)
            visible = false
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = visible && achievement != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        achievement?.let {
            AchievementBannerContent(
                achievement = it,
                onDismiss = {
                    visible = false
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun AchievementBannerContent(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        EnthusiastGold.copy(alpha = 0.2f),
                        EnthusiastGold.copy(alpha = 0.15f),
                        EnthusiastGold.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pulsing badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                EnthusiastGold,
                                EnthusiastGold.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .pulse(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 24.sp
                )
            }
            
            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ACHIEVEMENT UNLOCKED!",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnthusiastGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Dismiss button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
