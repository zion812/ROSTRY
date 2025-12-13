package com.rio.rostry.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Animated success checkmark - Instagram style
 * Shows green circle with checkmark that animates in
 */
@Composable
fun SuccessCheckmarkAnimation(
    message: String = "Success!",
    onComplete: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Circle scale animation
    val circleScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "circle_scale"
    )
    
    // Circle progress animation (for drawing the circle)
    val circleProgress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "circle_progress"
    )
    
    // Checkmark scale (delayed)
    val checkScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "check_scale",
        finishedListener = {
            if (it == 1f) {
                // Animation complete, trigger callback after delay
            }
        }
    )
    
    // Text alpha (delayed more)
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = 400,
            easing = LinearEasing
        ),
        label = "text_alpha"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
        delay(1000) // Show success for 1 second
        onComplete()
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated circle background
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(circleScale)
            ) {
                val strokeWidth = 8.dp.toPx()
                drawCircle(
                    color = Color(0xFF4CAF50), // Green
                    style = Stroke(width = strokeWidth)
                )
            }
            
            // Solid background circle
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .scale(circleScale)
                    .background(
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )
            
            // Checkmark icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .size(64.dp)
                    .scale(checkScale)
            )
        }
        
        // Success message
        Text(
            text = message,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.graphicsLayer { alpha = textAlpha }
        )
    }
}

/**
 * Inline success checkmark - smaller version for inline use
 */
@Composable
fun InlineSuccessCheck(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "check_scale"
    )
    
    LaunchedEffect(Unit) {
        delay(50)
        startAnimation = true
    }
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .background(
                color = Color(0xFF4CAF50),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Success",
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}
