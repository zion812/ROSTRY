package com.rio.rostry.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
 */
@Composable
fun EnthusiastAuraBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2D1B4D), // Deep Purple
                        EnthusiastObsidian
                    ),
                    radius = 2000f
                )
            )
    ) {
        // Subtle top glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            EnthusiastGold.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        )
        content()
    }
}
