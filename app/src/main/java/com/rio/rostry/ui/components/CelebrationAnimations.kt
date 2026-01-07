package com.rio.rostry.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.ui.theme.Coral
import com.rio.rostry.ui.theme.ElectricBlue
import com.rio.rostry.ui.theme.Emerald
import com.rio.rostry.ui.theme.EnthusiastGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Celebration particle data class (renamed to avoid conflict with SuccessAnimations).
 */
private data class CelebrationParticle(
    var x: Float,
    var y: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    var velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val shape: CelebrationShape
)

private enum class CelebrationShape { RECTANGLE, CIRCLE, STAR }

/**
 * Confetti celebration effect with falling particles.
 * 
 * @param visible Whether to show the confetti
 * @param particleCount Number of particles to generate
 * @param durationMillis How long the animation runs
 * @param colors Colors to use for particles
 * @param onComplete Callback when animation finishes
 */
@Composable
fun ConfettiEffect(
    visible: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    durationMillis: Int = 3000,
    colors: List<Color> = listOf(EnthusiastGold, ElectricBlue, Coral, Emerald),
    onComplete: () -> Unit = {}
) {
    if (!visible) return
    
    var animationProgress by remember { mutableStateOf(0f) }
    val particles = remember { mutableStateListOf<CelebrationParticle>() }
    
    LaunchedEffect(visible) {
        // Initialize particles
        particles.clear()
        repeat(particleCount) {
            particles.add(
                CelebrationParticle(
                    x = Random.nextFloat(),
                    y = -Random.nextFloat() * 0.3f,
                    color = colors.random(),
                    size = Random.nextFloat() * 8f + 4f,
                    rotation = Random.nextFloat() * 360f,
                    velocityX = (Random.nextFloat() - 0.5f) * 0.01f,
                    velocityY = Random.nextFloat() * 0.015f + 0.005f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 10f,
                    shape = CelebrationShape.values().random()
                )
            )
        }
        
        // Animate
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < durationMillis) {
            particles.forEachIndexed { index, particle ->
                particles[index] = particle.copy(
                    x = particle.x + particle.velocityX,
                    y = particle.y + particle.velocityY,
                    rotation = particle.rotation + particle.rotationSpeed
                )
            }
            animationProgress = (System.currentTimeMillis() - startTime).toFloat() / durationMillis
            delay(16) // ~60fps
        }
        
        onComplete()
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val fadeOutAlpha = if (animationProgress > 0.7f) {
            1f - ((animationProgress - 0.7f) / 0.3f)
        } else 1f
        
        particles.forEach { particle ->
            val x = particle.x * size.width
            val y = particle.y * size.height
            
            if (y < size.height && y > 0) {
                rotate(particle.rotation, Offset(x, y)) {
                    when (particle.shape) {
                        CelebrationShape.RECTANGLE -> {
                            drawRect(
                                color = particle.color.copy(alpha = fadeOutAlpha),
                                topLeft = Offset(x - particle.size / 2, y - particle.size),
                                size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 2)
                            )
                        }
                        CelebrationShape.CIRCLE -> {
                            drawCircle(
                                color = particle.color.copy(alpha = fadeOutAlpha),
                                radius = particle.size,
                                center = Offset(x, y)
                            )
                        }
                        CelebrationShape.STAR -> {
                            drawStar(
                                color = particle.color.copy(alpha = fadeOutAlpha),
                                center = Offset(x, y),
                                radius = particle.size
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws a simple star shape.
 */
private fun DrawScope.drawStar(color: Color, center: Offset, radius: Float) {
    val path = Path()
    val points = 5
    val innerRadius = radius * 0.4f
    
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = Math.toRadians((i * 360.0 / (points * 2)) - 90.0)
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    
    drawPath(path, color)
}

/**
 * Floating emoji effect (used for double-tap like actions).
 */
@Composable
fun FloatingEmojiEffect(
    visible: Boolean,
    emoji: String = "👑",
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    if (!visible) return
    
    val scale = remember { Animatable(0.5f) }
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    
    LaunchedEffect(visible) {
        // Scale up
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        )
        
        // Float up and fade out in parallel
        coroutineScope {
            launch {
                offsetY.animateTo(
                    targetValue = -150f,
                    animationSpec = tween(1200, easing = FastOutSlowInEasing)
                )
            }
            launch {
                delay(600)
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(600)
                )
            }
        }
        
        onComplete()
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 64.sp,
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.toInt()) }
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}

/**
 * Pulse ring effect expanding from center.
 */
@Composable
fun PulseRingEffect(
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = EnthusiastGold,
    onComplete: () -> Unit = {}
) {
    if (!visible) return
    
    val radius = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    
    LaunchedEffect(visible) {
        coroutineScope {
            launch {
                radius.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            }
            launch {
                delay(200)
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(400)
                )
            }
        }
        
        onComplete()
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val maxRadius = size.minDimension / 2
        drawCircle(
            color = color.copy(alpha = alpha.value * 0.6f),
            radius = maxRadius * radius.value,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
        )
    }
}

/**
 * Success celebration combining confetti and floating emoji.
 */
@Composable
fun SuccessCelebration(
    visible: Boolean,
    modifier: Modifier = Modifier,
    emoji: String = "🎉",
    onComplete: () -> Unit = {}
) {
    Box(modifier = modifier) {
        ConfettiEffect(
            visible = visible,
            particleCount = 30,
            durationMillis = 2500
        )
        FloatingEmojiEffect(
            visible = visible,
            emoji = emoji,
            onComplete = onComplete
        )
    }
}
