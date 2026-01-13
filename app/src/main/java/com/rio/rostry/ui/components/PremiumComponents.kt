package com.rio.rostry.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.EnthusiastGlass
import com.rio.rostry.ui.theme.EnthusiastGold
import com.rio.rostry.ui.theme.EnthusiastGoldVariant
import com.rio.rostry.ui.theme.EnthusiastObsidian

/**
 * A ultra-premium card for the Enthusiast persona.
 * Features a gold gradient border, glassmorphism, and haptic interactive feedback.
 */
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, label = "cardScale")
    val haptic = LocalHapticFeedback.current

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null, // Custom ripple or none for glass
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Satisfying thud
                onClick()
            }
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        EnthusiastGold,
                        Color.Transparent,
                        EnthusiastGoldVariant
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(EnthusiastGlass)
            .then(clickModifier)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * A background surface that provides the "Aura" effect for the Enthusiast persona.
 * The aura color changes based on trust score:
 * - Low (0-40): Muted gray gradient
 * - Medium (40-70): Soft blue glow
 * - High (70-100): Golden aura
 * 
 * Includes subtle pulsing animation for premium feel.
 */
@Composable
fun EnthusiastAuraBackground(
    trustScore: Float = 50f,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "auraPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraPulseAlpha"
    )
    
    // Determine aura colors based on trust score
    val (primaryAuraColor, secondaryAuraColor) = when {
        trustScore < 40f -> Pair(
            Color(0xFF3A3A3A), // Muted gray
            Color(0xFF2A2A2A)
        )
        trustScore < 70f -> Pair(
            Color(0xFF1565C0).copy(alpha = 0.15f), // Soft blue
            EnthusiastObsidian
        )
        else -> Pair(
            EnthusiastGold.copy(alpha = pulseAlpha), // Golden with pulse
            Color(0xFF2D1B4D) // Deep purple
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryAuraColor,
                        EnthusiastObsidian
                    ),
                    radius = 2000f
                )
            )
    ) {
        // Aura glow layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryAuraColor,
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )
        // Subtle top gold glow (consistent premium feel)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            EnthusiastGold.copy(alpha = 0.03f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 800f
                    )
                )
        )
        content()
    }
}

/**
 * A glassmorphic card variant with blur effect and subtle border.
 * Perfect for overlaying content on Enthusiast screens.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "glassCardScale"
    )
    val haptic = LocalHapticFeedback.current

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .then(clickModifier)
            .padding(16.dp)
    ) {
        content()
    }
}

/**
 * Premium button with gradient background for Enthusiast CTAs.
 */
@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(
            EnthusiastGold,
            EnthusiastGoldVariant
        )
    )
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.5f
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) gradient else Brush.linearGradient(
                colors = listOf(Color.Gray, Color.DarkGray)
            ))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
            color = if (enabled) EnthusiastObsidian else Color.White.copy(alpha = 0.6f)
        )
    }
}

