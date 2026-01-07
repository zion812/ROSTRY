package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Coral
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * Urgent activity types for the live ticker.
 */
sealed class UrgentActivity(
    val emoji: String,
    val priority: Int // 1 = highest
) {
    data class Incubation(
        val batchName: String,
        val currentDay: Int,
        val totalDays: Int,
        val targetTimestamp: Long
    ) : UrgentActivity("🥚", 1)
    
    data class HatchingDue(
        val batchName: String,
        val dueCount: Int,
        val targetTimestamp: Long
    ) : UrgentActivity("🐣", 1)
    
    data class SickBirds(
        val count: Int,
        val severity: String // "critical", "moderate", "low"
    ) : UrgentActivity("🏥", 2)
    
    data class VaccinationDue(
        val birdNames: List<String>,
        val vaccineName: String,
        val dueTimestamp: Long
    ) : UrgentActivity("💉", 3)
    
    data class Custom(
        val message: String,
        val customEmoji: String = "⚠️",
        val targetTimestamp: Long? = null
    ) : UrgentActivity(customEmoji, 4)
}

/**
 * Stock app-inspired live activity ticker for urgent farm activities.
 * Features:
 * - Pulsing "LIVE" indicator
 * - Real-time countdown timer
 * - Swipe to dismiss
 * - Priority-based color coding
 */
@Composable
fun LiveActivityTicker(
    activity: UrgentActivity?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    if (activity == null) return
    
    var visible by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                visible = false
                onDismiss()
                true
            } else false
        }
    )
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Coral.copy(alpha = 0.3f))
                )
            }
        ) {
            LiveTickerContent(
                activity = activity,
                modifier = modifier,
                onDismiss = { 
                    visible = false
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun LiveTickerContent(
    activity: UrgentActivity,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val priorityColor = when (activity.priority) {
        1 -> Coral
        2 -> Color(0xFFFFA726) // Orange
        else -> Color(0xFF42A5F5) // Blue
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = CardDefaults.cardColors(
            containerColor = priorityColor.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pulsing live indicator
            LiveIndicator(color = priorityColor)
            
            // Emoji
            Text(text = activity.emoji, style = MaterialTheme.typography.bodyLarge)
            
            // Message
            Text(
                text = getActivityMessage(activity),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            // Countdown
            val targetTimestamp = getTargetTimestamp(activity)
            if (targetTimestamp != null) {
                CountdownTimer(targetTimestamp = targetTimestamp)
            }
            
            // Dismiss button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun LiveIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liveIndicator")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livePulse"
    )
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CountdownTimer(
    targetTimestamp: Long,
    modifier: Modifier = Modifier
) {
    var remainingMs by remember { mutableLongStateOf(targetTimestamp - System.currentTimeMillis()) }
    
    LaunchedEffect(targetTimestamp) {
        while (remainingMs > 0) {
            delay(1000)
            remainingMs = targetTimestamp - System.currentTimeMillis()
        }
    }
    
    val formattedTime = formatCountdown(remainingMs)
    
    Text(
        text = formattedTime,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = if (remainingMs < TimeUnit.HOURS.toMillis(24)) Coral else MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

private fun formatCountdown(ms: Long): String {
    if (ms <= 0) return "Now!"
    
    val days = TimeUnit.MILLISECONDS.toDays(ms)
    val hours = TimeUnit.MILLISECONDS.toHours(ms) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

private fun getActivityMessage(activity: UrgentActivity): String = when (activity) {
    is UrgentActivity.Incubation -> 
        "${activity.batchName}: Day ${activity.currentDay} of ${activity.totalDays}"
    is UrgentActivity.HatchingDue -> 
        "${activity.batchName}: ${activity.dueCount} eggs hatching soon"
    is UrgentActivity.SickBirds -> 
        "${activity.count} bird${if (activity.count > 1) "s" else ""} need${if (activity.count > 1) "" else "s"} attention"
    is UrgentActivity.VaccinationDue -> 
        "${activity.vaccineName} due for ${activity.birdNames.size} bird${if (activity.birdNames.size > 1) "s" else ""}"
    is UrgentActivity.Custom -> 
        activity.message
}

private fun getTargetTimestamp(activity: UrgentActivity): Long? = when (activity) {
    is UrgentActivity.Incubation -> activity.targetTimestamp
    is UrgentActivity.HatchingDue -> activity.targetTimestamp
    is UrgentActivity.VaccinationDue -> activity.dueTimestamp
    is UrgentActivity.Custom -> activity.targetTimestamp
    else -> null
}
