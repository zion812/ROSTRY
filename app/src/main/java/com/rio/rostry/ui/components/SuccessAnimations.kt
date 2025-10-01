package com.rio.rostry.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class AnimationType { CHECKMARK, CONFETTI, FIREWORKS, CUSTOM }

@Composable
fun SuccessAnimation(
    animationType: AnimationType = AnimationType.CHECKMARK,
    modifier: Modifier = Modifier
) {
    when (animationType) {
        AnimationType.CHECKMARK -> CheckmarkAnimation(modifier)
        AnimationType.CONFETTI -> ConfettiAnimation(modifier)
        AnimationType.FIREWORKS -> FireworksAnimation(modifier)
        AnimationType.CUSTOM -> CheckmarkAnimation(modifier) // Fallback
    }
}

@Composable
fun CheckmarkAnimation(modifier: Modifier = Modifier) {
    val scale by rememberInfiniteTransition(label = "checkmark").animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        imageVector = Icons.Filled.CheckCircle,
        contentDescription = "Success",
        modifier = modifier.scale(scale),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ConfettiAnimation(modifier: Modifier = Modifier) {
    val particles = remember {
        List(20) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -0.1f,
                color = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val yPos = size.height * (particle.y + progress)
            val xPos = size.width * (particle.x + (progress * 0.2f - 0.1f))
            drawCircle(
                color = particle.color,
                radius = 8f,
                center = Offset(xPos, yPos)
            )
        }
    }
}

@Composable
fun FireworksAnimation(modifier: Modifier = Modifier) {
    // Simplified fireworks - similar to confetti but with burst pattern
    ConfettiAnimation(modifier)
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color
)

@Composable
fun CelebrationDialog(
    visible: Boolean,
    title: String,
    message: String,
    animation: AnimationType = AnimationType.CHECKMARK,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(visible) {
        if (visible) {
            HapticFeedback.success(context)
            delay(3000)
            onDismiss()
        }
    }

    if (visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SuccessAnimation(
                        animationType = animation,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onDismiss) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

object HapticFeedback {
    fun success(context: Context) {
        performHaptic(context, HapticType.SUCCESS)
    }

    fun error(context: Context) {
        performHaptic(context, HapticType.ERROR)
    }

    fun selection(context: Context) {
        performHaptic(context, HapticType.SELECTION)
    }

    fun impact(context: Context) {
        performHaptic(context, HapticType.IMPACT)
    }

    private fun performHaptic(context: Context, type: HapticType) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    HapticType.SUCCESS -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
                    HapticType.SELECTION -> VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticType.IMPACT -> VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                it.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(when (type) {
                    HapticType.SUCCESS -> 50
                    HapticType.ERROR -> 100
                    HapticType.SELECTION -> 20
                    HapticType.IMPACT -> 30
                })
            }
        }
    }

    private enum class HapticType { SUCCESS, ERROR, SELECTION, IMPACT }
}
