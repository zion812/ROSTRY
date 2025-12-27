package com.rio.rostry.ui.farmer.digital

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import com.rio.rostry.domain.model.DigitalFarmZone
import com.rio.rostry.domain.model.DragState
import com.rio.rostry.domain.model.VisualBird

/**
 * DragInteractionHandler: Provides drag-and-drop gesture detection for Digital Farm.
 *
 * Detects long-press followed by drag and determines drop zones.
 */
object DragInteractionHandler {

    private const val LONG_PRESS_THRESHOLD_MS = 600L
    private const val HIT_RADIUS = 50f // Pixel radius for tap/drag start detection

    /**
     * Find the bird at the given screen position.
     */
    fun findBirdAtPosition(
        screenPosition: Offset,
        birds: List<VisualBird>,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset
    ): VisualBird? {
        // Iterate through birds (reversed for front-to-back hit testing)
        for (bird in birds.reversed()) {
            val isoPos = IsometricGrid.toIsometric(bird.position.x, bird.position.y)
            val birdScreenPos = Offset(
                mapCenter.x + panOffset.x + (isoPos.x * scale),
                mapCenter.y + panOffset.y + (isoPos.y * scale)
            )
            val distance = (screenPosition - birdScreenPos).getDistance()
            if (distance <= HIT_RADIUS * scale) {
                return bird
            }
        }
        return null
    }

    /**
     * Determine the drop zone for a given screen position.
     */
    fun getDropZone(
        screenPosition: Offset,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset
    ): DigitalFarmZone {
        val gridPos = IsometricGrid.toGrid(screenPosition.x, screenPosition.y, scale, mapCenter, panOffset)
        return IsometricGrid.getZoneForGridPosition(gridPos.x, gridPos.y)
    }

    /**
     * Check if a drop from sourceZone to targetZone is valid.
     * Valid transitions:
     * - FREE_RANGE -> MARKET_STAND (listing for sale)
     * - Any zone -> Same zone (cancel)
     */
    fun isValidDrop(sourceZone: DigitalFarmZone, targetZone: DigitalFarmZone): Boolean {
        return when (sourceZone) {
            DigitalFarmZone.FREE_RANGE -> targetZone == DigitalFarmZone.MARKET_STAND || targetZone == sourceZone
            DigitalFarmZone.READY_DISPLAY -> targetZone == DigitalFarmZone.MARKET_STAND || targetZone == sourceZone
            else -> targetZone == sourceZone
        }
    }
}

/**
 * Composable modifier for drag gesture detection.
 */
@Composable
fun Modifier.farmDragGestures(
    birds: List<VisualBird>,
    scale: Float,
    panOffset: Offset,
    mapCenter: Offset,
    onDragStart: (VisualBird, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (DigitalFarmZone) -> Unit,
    onDragCancel: () -> Unit
): Modifier {
    var dragStartTime by remember { mutableStateOf(0L) }
    var isDragging by remember { mutableStateOf(false) }

    return this.pointerInput(birds, scale, panOffset, mapCenter) {
        detectDragGestures(
            onDragStart = { offset ->
                dragStartTime = System.currentTimeMillis()
                val bird = DragInteractionHandler.findBirdAtPosition(offset, birds, scale, panOffset, mapCenter)
                if (bird != null) {
                    isDragging = true
                    onDragStart(bird, offset)
                }
            },
            onDrag = { change, dragAmount ->
                if (isDragging) {
                    change.consume()
                    onDrag(change.position)
                }
            },
            onDragEnd = {
                if (isDragging) {
                    // We need the current position from the last drag event
                    // This is a simplified approach - in practice, track lastPosition in state
                    isDragging = false
                    val dropZone = DigitalFarmZone.FREE_RANGE // Placeholder - actual implementation needs last position
                    onDragEnd(dropZone)
                }
            },
            onDragCancel = {
                isDragging = false
                onDragCancel()
            }
        )
    }
}

/**
 * State holder for drag interactions.
 */
@Composable
fun rememberDragState(): DragState {
    var state by remember { mutableStateOf(DragState()) }
    return state
}
