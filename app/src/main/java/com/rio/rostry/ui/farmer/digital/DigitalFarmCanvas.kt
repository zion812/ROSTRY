package com.rio.rostry.ui.farmer.digital

import android.graphics.Picture
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.DigitalFarmState
import com.rio.rostry.domain.model.DragState
import com.rio.rostry.domain.model.VisualBird
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive

/**
 * DigitalFarmCanvas: Main rendering composable for the 2.5D Digital Farm.
 *
 * Features:
 * - Pan & Zoom gestures
 * - Tap detection for bird info bubbles
 * - Z-ordered rendering via FarmRenderer
 * - Animation time for idle bobbing
 */
@Composable
fun DigitalFarmCanvas(
    state: DigitalFarmState,
    modifier: Modifier = Modifier,
    onBirdTapped: (VisualBird) -> Unit = {},
    onListedForSale: (String) -> Unit = {}
) {
    // Transform State
    var scale by remember { mutableFloatStateOf(1.5f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Animation Time (for idle bobbing) - with lifecycle awareness
    var animationTime by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        try {
            while (true) {
                ensureActive() // Throws CancellationException if cancelled
                animationTime = System.currentTimeMillis() - startTime
                delay(33) // ~30 FPS - sufficient for idle bobbing, reduces memory pressure
            }
        } catch (_: kotlinx.coroutines.CancellationException) {
            // Normal termination when composable leaves composition
        }
    }

    // Selected Bird for Bubble
    var selectedBird by remember { mutableStateOf<VisualBird?>(null) }
    var bubblePosition by remember { mutableStateOf(Offset.Zero) }

    // Drag State
    var dragState by remember { mutableStateOf(DragState()) }

    // Collect all birds for hit testing
    val allBirds = remember(state) {
        state.freeRange + state.growOut + state.readyDisplay + state.marketReady +
                state.nurseries.flatMap { listOf(it.mother) + it.chicks } +
                state.breedingUnits.flatMap { listOfNotNull(it.rooster) + it.hens }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 4f)
                        panOffset += pan
                    }
                }
                .pointerInput(allBirds, scale, panOffset) {
                    detectTapGestures { tapOffset ->
                        val mapCenter = Offset(size.width / 2f, size.height / 2f)
                        val bird = DragInteractionHandler.findBirdAtPosition(
                            tapOffset, allBirds, scale, panOffset, mapCenter
                        )
                        if (bird != null) {
                            selectedBird = bird
                            bubblePosition = tapOffset
                            onBirdTapped(bird)
                        } else {
                            selectedBird = null
                        }
                    }
                }
        ) {
            val mapCenter = Offset(size.width / 2f, size.height / 2f)

            FarmRenderer.renderFarm(
                drawScope = this,
                state = state,
                scale = scale,
                panOffset = panOffset,
                mapCenter = mapCenter,
                animationTime = animationTime.toFloat(),
                selectedBirdId = selectedBird?.productId,
                dragState = dragState
            )
        }

        // Floating Bubble for selected bird
        AnimatedVisibility(
            visible = selectedBird != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp)
        ) {
            selectedBird?.let { bird ->
                FloatingBubble(bird = bird)
            }
        }
    }
}

/**
 * FloatingBubble: Shows bird stats when tapped.
 */
@Composable
fun FloatingBubble(bird: VisualBird) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = bird.name.ifBlank { "Bird ${bird.productId.take(4)}" },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${bird.weightText ?: "?"} • ${bird.ageText}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (bird.isReadyForSale) {
                Text(
                    text = "⭐ Ready for Sale",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

/**
 * FarmStatsBar: Shows farm stats at the top of the screen.
 */
@Composable
fun FarmStatsBar(
    totalBirds: Int,
    eggsToday: Int,
    readyForSale: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            StatItem(emoji = "🐔", value = "$totalBirds", label = "Birds")
            StatItem(emoji = "🥚", value = "$eggsToday", label = "Eggs")
            StatItem(emoji = "⭐", value = "$readyForSale", label = "Ready")
        }
    }
}

@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$emoji $value", style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
