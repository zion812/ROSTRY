package com.rio.rostry.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer loading effect - Instagram/Facebook style
 * Animated gradient sweep across content placeholders
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )
    
    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }
}

/**
 * Shimmer loading placeholder for buttons
 */
@Composable
fun ShimmerButton(
    modifier: Modifier = Modifier
) {
    ShimmerEffect(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
    }
}

/**
 * Shimmer loading placeholder for text
 */
@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    width: Float = 1f // 0.0 to 1.0
) {
    ShimmerEffect(
        modifier = modifier
            .fillMaxWidth(width)
            .height(20.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
    }
}

/**
 * Shimmer loading placeholder for circular avatar
 */
@Composable
fun ShimmerCircle(
    size: androidx.compose.ui.unit.Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    ShimmerEffect(
        modifier = modifier.size(size)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {}
    }
}

/**
 * Complete shimmer loading card - for auth screen
 */
@Composable
fun ShimmerAuthCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logo placeholder
        ShimmerCircle(size = 120.dp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title placeholder
        ShimmerText(width = 0.6f)
        
        // Subtitle placeholder
        ShimmerText(width = 0.8f, modifier = Modifier.height(16.dp))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Button placeholders
        repeat(3) {
            ShimmerButton()
        }
    }
}

/**
 * OTP input shimmer loader
 */
@Composable
fun ShimmerOtpInputs(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        repeat(6) {
            ShimmerEffect(
                modifier = Modifier
                    .size(56.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {}
            }
        }
    }
}
