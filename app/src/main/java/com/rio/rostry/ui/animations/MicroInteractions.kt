package com.rio.rostry.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun PressAnimatedSurface(content: @Composable () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.97f else 1f, label = "press")
    Surface(tonalElevation = 1.dp) {
        Box(
            Modifier
                .scale(scale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { pressed = !pressed }
                .padding(8.dp)
        ) { content() }
    }
}

// Instagram-style double-tap like animation
@Composable
fun DoubleTapLikeOverlay(visible: Boolean, onAnimationEnd: () -> Unit) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(600)
            onAnimationEnd()
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1.2f else 0.5f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "like_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 0f else 1f,
        animationSpec = tween(durationMillis = 600, easing = LinearEasing),
        label = "like_alpha"
    )

    if (visible) {
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Like",
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale)
                    .alpha(1f - alpha),
                tint = Color.Red
            )
        }
    }
}

// Wishlist heart animation
@Composable
fun Modifier.wishlistHeartAnimation(isInWishlist: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isInWishlist) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "wishlist_scale"
    )
    return this.scale(scale)
}

// Trending badge pulse animation
@Composable
fun Modifier.trendingPulse(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    return this.scale(scale)
}

// Animated counter for engagement metrics
@Composable
fun AnimatedCounter(count: Int, label: String, modifier: Modifier = Modifier) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "counter"
    )
    
    androidx.compose.material3.Text(
        text = "$animatedCount $label",
        modifier = modifier
    )
}

// Shimmer loading effect
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    return this.alpha(alpha)
}

// Card reveal animation with stagger
@Composable
fun Modifier.cardRevealAnimation(index: Int, visible: Boolean = true): Modifier {
    val offset by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = index * 50,
            easing = FastOutSlowInEasing
        ),
        label = "card_reveal"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = index * 50,
            easing = FastOutSlowInEasing
        ),
        label = "card_alpha"
    )
    
    return this
        .graphicsLayer {
            translationY = offset
            this.alpha = alpha
        }
}
