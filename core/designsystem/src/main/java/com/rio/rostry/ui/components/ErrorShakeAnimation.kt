package com.rio.rostry.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Shake animation modifier - for error feedback
 * Shakes the composable horizontally when triggered
 */
fun Modifier.shake(trigger: Boolean): Modifier = composed {
    val density = LocalDensity.current
    val offsetX = remember { Animatable(0f) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            // Shake animation sequence
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = repeatable(
                    iterations = 3,
                    animation = keyframes {
                        durationMillis = 200
                        0f at 0
                        -30f at 50 with FastOutSlowInEasing
                        30f at 100 with FastOutSlowInEasing
                        -30f at 150 with FastOutSlowInEasing
                        0f at 200 with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    
    this.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}

/**
 * State holder for shake animation
 */
class ShakeController {
    private val _shake = mutableStateOf(false)
    val shake: State<Boolean> = _shake
    
    fun shake() {
        _shake.value = true
        // Reset after animation
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(600)
            _shake.value = false
        }
    }
}

/**
 * Remember shake controller
 */
@Composable
fun rememberShakeController(): ShakeController {
    return remember { ShakeController() }
}

/**
 * Pulse animation for error state
 */
@Composable
fun Modifier.errorPulse(isError: Boolean): Modifier = composed {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(isError) {
        if (isError) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = repeatable(
                    iterations = 2,
                    animation = keyframes {
                        durationMillis = 200
                        1f at 0
                        1.05f at 100 with FastOutSlowInEasing
                        1f at 200 with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    
    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/**
 * Combined error animation - shake + color pulse
 */
@Composable
fun Modifier.errorAnimation(
    isError: Boolean,
    onAnimationEnd: () -> Unit = {}
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val colorPulse = remember { Animatable(0f) }
    
    LaunchedEffect(isError) {
        if (isError) {
            // Shake horizontally
            launch {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = repeatable(
                        iterations = 3,
                        animation = keyframes {
                            durationMillis = 200
                            0f at 0
                            -20f at 50
                            20f at 100
                            -20f at 150
                            0f at 200
                        }
                    )
                )
            }
            
            // Color pulse
            launch {
                colorPulse.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 200)
                )
                colorPulse.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200)
                )
                onAnimationEnd()
            }
        }
    }
    
    this.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}
