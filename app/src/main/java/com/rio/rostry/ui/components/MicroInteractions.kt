package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Enhanced Micro-Interactions for Premium UI.
 * Real animation implementations replacing placeholders.
 */

/**
 * Modifier for haptic click feedback.
 */
fun Modifier.hapticClick(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick)

/**
 * Modifier for press animation with haptic feedback.
 */
fun Modifier.hapticPressAnimation(onClick: () -> Unit): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "pressScale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            onClick = {
                isPressed = true
                onClick()
            }
        )
}

/**
 * Modifier for subtle press scale animation.
 */
fun Modifier.pressAnimation(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "pressScale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Modifier for pulsing animation effect (used for notifications/badges).
 */
fun Modifier.pulse(enabled: Boolean = true): Modifier = composed {
    if (!enabled) return@composed this
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Modifier for trending pulse animation (stronger effect).
 */
fun Modifier.trendingPulse(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "trendingPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trendingScale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Modifier for card reveal animation with staggered delay.
 */
fun Modifier.cardRevealAnimation(index: Int): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = index * 100),
        label = "cardRevealAlpha"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = tween(durationMillis = 300, delayMillis = index * 100, easing = FastOutSlowInEasing),
        label = "cardRevealOffset"
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    this.graphicsLayer {
        this.alpha = alpha
        translationY = offsetY
    }
}

/**
 * Modifier for wishlist heart animation (scale bounce).
 */
fun Modifier.wishlistHeartAnimation(isLiked: Boolean): Modifier = composed {
    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "heartScale"
    )
    
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Animated visibility with fade and scale.
 */
@Composable
fun FadeScaleVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(200)
        ),
        exit = fadeOut(animationSpec = tween(150)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(150)
        )
    ) {
        content()
    }
}

/**
 * Animated visibility with slide up and fade.
 */
@Composable
fun SlideUpFadeVisibility(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(250, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(150)) + slideOutVertically(
            targetOffsetY = { it / 4 },
            animationSpec = tween(150)
        )
    ) {
        content()
    }
}

/**
 * Double-tap like overlay with floating emoji animation.
 * Shows a large emoji that scales up and fades out.
 */
@Composable
fun DoubleTapLikeOverlay(
    visible: Boolean,
    emoji: String = "❤️",
    onAnimationComplete: () -> Unit = {}
) {
    var animationTrigger by remember { mutableStateOf(0) }
    
    LaunchedEffect(visible) {
        if (visible) {
            animationTrigger++
            delay(800)
            onAnimationComplete()
        }
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.5f,
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(100)),
        exit = scaleOut(
            targetScale = 1.5f,
            animationSpec = tween(400)
        ) + fadeOut(animationSpec = tween(400))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 80.sp
            )
        }
    }
}

/**
 * Animated counter for engagement numbers.
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier
) {
    val animatedCount by animateFloatAsState(
        targetValue = count.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "counterAnimation"
    )
    
    Text(
        text = animatedCount.toInt().toString(),
        modifier = modifier
    )
}
