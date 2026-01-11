package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.components.GlassmorphicCard
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.EnthusiastGoldVariant

/**
 * Priority level for the TodaysFocusCard - determines visual urgency.
 */
enum class FocusPriority {
    NORMAL,
    IMPORTANT,
    URGENT
}

/**
 * Prominent focus card showing the #1 priority task for today.
 * Inspired by Apple's Focus modes and Google Tasks' daily focus feature.
 * 
 * Features:
 * - Gradient background based on priority
 * - Countdown timer display (optional)
 * - Single prominent CTA button
 * - Spring scale animation on interaction
 */
@Composable
fun TodaysFocusCard(
    title: String,
    subtitle: String,
    icon: ImageVector = Icons.Filled.Notifications,
    ctaText: String = "Take Action",
    countdown: String? = null,
    priority: FocusPriority = FocusPriority.NORMAL,
    onCta: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )
    
    val (gradientColors, iconBgColor) = when (priority) {
        FocusPriority.URGENT -> listOf(
            Color(0xFFE91E63).copy(alpha = 0.15f),
            Color(0xFFFF5722).copy(alpha = 0.08f)
        ) to Color(0xFFE91E63)
        FocusPriority.IMPORTANT -> listOf(
            Color(0xFFFF9800).copy(alpha = 0.15f),
            EnthusiastGold.copy(alpha = 0.08f)
        ) to Color(0xFFFF9800)
        FocusPriority.NORMAL -> listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.surface
        ) to MaterialTheme.colorScheme.primary
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .semantics { contentDescription = "Today's focus: $title. $subtitle" },
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCta()
                }
                .padding(Dimens.space_large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.space_large)
            ) {
                // Icon with colored background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(iconBgColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconBgColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Text content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Subtitle with optional countdown
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        countdown?.let { time ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = iconBgColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = iconBgColor
                                )
                            }
                        }
                    }
                }
                
                // CTA arrow
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = ctaText,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Helper function to create common focus cards.
 */
object TodaysFocus {
    @Composable
    fun HatchingDue(count: Int, timeRemaining: String, onClick: () -> Unit) = TodaysFocusCard(
        title = "$count Eggs Ready to Hatch",
        subtitle = "Due soon",
        icon = Icons.Filled.EggAlt,
        countdown = timeRemaining,
        priority = FocusPriority.URGENT,
        ctaText = "Check Now",
        onCta = onClick
    )
}
