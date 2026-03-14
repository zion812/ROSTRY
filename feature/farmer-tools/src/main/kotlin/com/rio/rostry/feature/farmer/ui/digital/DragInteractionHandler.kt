package com.rio.rostry.ui.farmer.digital

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.rio.rostry.domain.model.DigitalFarmZone
import com.rio.rostry.domain.model.DragState
import com.rio.rostry.domain.model.VisualBird
import timber.log.Timber

/**
 * DragInteractionHandler: Provides drag-and-drop gesture detection for Digital Farm.
 *
 * Detects long-press followed by drag and determines drop zones using
 * coordinate-based calculations.
 */
object DragInteractionHandler {

    private const val LONG_PRESS_THRESHOLD_MS = 600L
    private const val HIT_RADIUS = 50f // Pixel radius for tap/drag start detection

    // Drop zone boundaries in grid coordinates
    data class DropZoneBounds(
        val zone: DigitalFarmZone,
        val minX: Int,
        val maxX: Int,
        val minY: Int,
        val maxY: Int
    )

    // Default zone boundaries (can be customized based on farm layout)
    private val defaultZoneBounds = listOf(
        DropZoneBounds(DigitalFarmZone.FREE_RANGE, 0, 4, 0, 4),
        DropZoneBounds(DigitalFarmZone.READY_DISPLAY, 5, 7, 0, 2),
        DropZoneBounds(DigitalFarmZone.MARKET_STAND, 5, 7, 3, 4),
        DropZoneBounds(DigitalFarmZone.QUARANTINE, 0, 2, 5, 7),
        DropZoneBounds(DigitalFarmZone.BREEDING_AREA, 3, 4, 5, 7),
        DropZoneBounds(DigitalFarmZone.FEED_STORAGE, 6, 7, 5, 7)
    )

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
                Timber.d("Found bird ${bird.id} at position $screenPosition")
                return bird
            }
        }
        return null
    }

    /**
     * Determine the drop zone for a given screen position using coordinate-based calculation.
     */
    fun getDropZone(
        screenPosition: Offset,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset
    ): DigitalFarmZone {
        // Convert screen position to grid coordinates
        val gridPos = IsometricGrid.toGrid(screenPosition.x, screenPosition.y, scale, mapCenter, panOffset)

        // Find which zone contains this grid position
        for (bounds in defaultZoneBounds) {
            if (gridPos.x >= bounds.minX && gridPos.x <= bounds.maxX &&
                gridPos.y >= bounds.minY && gridPos.y <= bounds.maxY) {
                return bounds.zone
            }
        }

        // Default to FREE_RANGE if no specific zone found
        return DigitalFarmZone.FREE_RANGE
    }

    /**
     * Get the grid position for a screen coordinate.
     */
    fun getGridPosition(
        screenPosition: Offset,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset
    ): GridPosition {
        return IsometricGrid.toGrid(screenPosition.x, screenPosition.y, scale, mapCenter, panOffset)
    }

    /**
     * Convert grid position to screen position.
     */
    fun getScreenPosition(
        gridX: Int,
        gridY: Int,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset
    ): Offset {
        val isoPos = IsometricGrid.toIsometric(gridX.toFloat(), gridY.toFloat())
        return Offset(
            mapCenter.x + panOffset.x + (isoPos.x * scale),
            mapCenter.y + panOffset.y + (isoPos.y * scale)
        )
    }

    /**
     * Check if a drop from sourceZone to targetZone is valid.
     * Valid transitions:
     * - FREE_RANGE -> MARKET_STAND (listing for sale)
     * - Any zone -> Same zone (cancel)
     * - QUARANTINE -> FREE_RANGE (release from quarantine)
     * - BREEDING_AREA -> FREE_RANGE (move to range)
     */
    fun isValidDrop(sourceZone: DigitalFarmZone, targetZone: DigitalFarmZone): Boolean {
        return when (sourceZone) {
            DigitalFarmZone.FREE_RANGE -> targetZone == DigitalFarmZone.MARKET_STAND || targetZone == sourceZone
            DigitalFarmZone.READY_DISPLAY -> targetZone == DigitalFarmZone.MARKET_STAND || targetZone == sourceZone
            DigitalFarmZone.QUARANTINE -> targetZone == DigitalFarmZone.FREE_RANGE || targetZone == sourceZone
            DigitalFarmZone.BREEDING_AREA -> targetZone == DigitalFarmZone.FREE_RANGE || targetZone == sourceZone
            DigitalFarmZone.MARKET_STAND -> targetZone == DigitalFarmZone.FREE_RANGE || targetZone == sourceZone
            else -> targetZone == sourceZone
        }
    }

    /**
     * Get the valid drop targets for a given source zone.
     */
    fun getValidDropTargets(sourceZone: DigitalFarmZone): List<DigitalFarmZone> {
        return DigitalFarmZone.entries.filter { target ->
            isValidDrop(sourceZone, target)
        }
    }

    /**
     * Calculate the distance between two grid positions.
     */
    fun getGridDistance(from: GridPosition, to: GridPosition): Float {
        val dx = (to.x - from.x).toFloat()
        val dy = (to.y - from.y).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    /**
     * Check if a grid position is within bounds of the farm.
     */
    fun isValidGridPosition(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && x < 8 && y < 8
    }
}

/**
 * Grid position data class.
 */
data class GridPosition(
    val x: Int,
    val y: Int
)

/**
 * Isometric grid utilities for coordinate conversion.
 */
object IsometricGrid {
    private const val TILE_WIDTH = 64f
    private const val TILE_HEIGHT = 32f

    /**
     * Convert grid coordinates to isometric screen position.
     */
    fun toIsometric(gridX: Float, gridY: Float): Offset {
        val screenX = (gridX - gridY) * (TILE_WIDTH / 2)
        val screenY = (gridX + gridY) * (TILE_HEIGHT / 2)
        return Offset(screenX, screenY)
    }

    /**
     * Convert screen position to grid coordinates.
     */
    fun toGrid(
        screenX: Float,
        screenY: Float,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset
    ): GridPosition {
        // Adjust for pan and center offset
        val adjustedX = (screenX - mapCenter.x - panOffset.x) / scale
        val adjustedY = (screenY - mapCenter.y - panOffset.y) / scale

        // Reverse isometric projection
        val halfWidth = TILE_WIDTH / 2
        val halfHeight = TILE_HEIGHT / 2

        val gridX = ((adjustedY / halfHeight) + (adjustedX / halfWidth)) / 2
        val gridY = ((adjustedY / halfHeight) - (adjustedX / halfWidth)) / 2

        return GridPosition(
            x = gridX.toInt().coerceIn(0, 7),
            y = gridY.toInt().coerceIn(0, 7)
        )
    }

    /**
     * Get the zone for a grid position.
     */
    fun getZoneForGridPosition(x: Int, y: Int): DigitalFarmZone {
        return when {
            x in 0..4 && y in 0..4 -> DigitalFarmZone.FREE_RANGE
            x in 5..7 && y in 0..2 -> DigitalFarmZone.READY_DISPLAY
            x in 5..7 && y in 3..4 -> DigitalFarmZone.MARKET_STAND
            x in 0..2 && y in 5..7 -> DigitalFarmZone.QUARANTINE
            x in 3..4 && y in 5..7 -> DigitalFarmZone.BREEDING_AREA
            x in 6..7 && y in 5..7 -> DigitalFarmZone.FEED_STORAGE
            else -> DigitalFarmZone.FREE_RANGE
        }
    }
}

/**
 * Composable modifier for drag gesture detection with proper position tracking.
 */
@Composable
fun Modifier.farmDragGestures(
    birds: List<VisualBird>,
    scale: Float,
    panOffset: Offset,
    mapCenter: Offset,
    onDragStart: (VisualBird, Offset) -> Unit,
    onDrag: (Offset, DigitalFarmZone) -> Unit,
    onDragEnd: (DigitalFarmZone, DigitalFarmZone) -> Unit,
    onDragCancel: () -> Unit
): Modifier {
    var dragStartTime by remember { mutableStateOf(0L) }
    var isDragging by remember { mutableStateOf(false) }
    var lastPosition by remember { mutableStateOf(Offset.Zero) }
    var draggedBird by remember { mutableStateOf<VisualBird?>(null) }
    var startZone by remember { mutableStateOf(DigitalFarmZone.FREE_RANGE) }

    return this.pointerInput(birds, scale, panOffset, mapCenter) {
        androidx.compose.foundation.gestures.detectDragGestures(
            onDragStart = { offset ->
                dragStartTime = System.currentTimeMillis()
                val bird = DragInteractionHandler.findBirdAtPosition(offset, birds, scale, panOffset, mapCenter)
                if (bird != null) {
                    isDragging = true
                    draggedBird = bird
                    startZone = DragInteractionHandler.getDropZone(offset, scale, panOffset, mapCenter)
                    lastPosition = offset
                    Timber.d("Drag started: bird ${bird.id} from zone $startZone")
                    onDragStart(bird, offset)
                }
            },
            onDrag = { change, dragAmount ->
                if (isDragging && draggedBird != null) {
                    change.consume()
                    lastPosition = change.position

                    // Calculate current drop zone based on position
                    val currentZone = DragInteractionHandler.getDropZone(
                        change.position, scale, panOffset, mapCenter
                    )

                    // Notify of drag with current position and zone
                    onDrag(change.position, currentZone)
                }
            },
            onDragEnd = {
                if (isDragging && draggedBird != null) {
                    // Calculate final drop zone
                    val endZone = DragInteractionHandler.getDropZone(
                        lastPosition, scale, panOffset, mapCenter
                    )

                    Timber.d("Drag ended: bird ${draggedBird?.id} from $startZone to $endZone")
                    onDragEnd(startZone, endZone)
                }
                isDragging = false
                draggedBird = null
            },
            onDragCancel = {
                isDragging = false
                draggedBird = null
                onDragCancel()
            }
        )
    }
}

/**
 * State holder for drag interactions with proper position tracking.
 */
@Composable
fun rememberDragState(): DragState {
    var state by remember { mutableStateOf(DragState()) }
    return state
}

/**
 * Visual feedback for drag operations.
 */
data class DragFeedback(
    val isDragging: Boolean = false,
    val draggedBird: VisualBird? = null,
    val currentZone: DigitalFarmZone = DigitalFarmZone.FREE_RANGE,
    val validTarget: Boolean = true,
    val opacity: Float = 1f
) {
    companion object {
        fun dragging(bird: VisualBird, zone: DigitalFarmZone, isValid: Boolean): DragFeedback {
            return DragFeedback(
                isDragging = true,
                draggedBird = bird,
                currentZone = zone,
                validTarget = isValid,
                opacity = 0.7f
            )
        }

        fun idle(): DragFeedback = DragFeedback()
    }
}