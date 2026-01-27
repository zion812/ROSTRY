package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import com.rio.rostry.domain.model.DigitalFarmZone

/**
 * Isometric Coordinate System for Digital Farm
 *
 * Uses a 2:1 ratio isometric projection.
 * Grid is 20x20 tiles divided into zones.
 */
object IsometricGrid {

    const val GRID_SIZE = 20
    const val TILE_SIZE = 40f

    // Zone Boundaries (in grid coordinates)
    // Nursery: Top-Left quadrant
    private val NURSERY_X_RANGE = 0..9
    private val NURSERY_Y_RANGE = 0..4

    // Breeding: Top-Right quadrant
    private val BREEDING_X_RANGE = 10..19
    private val BREEDING_Y_RANGE = 0..4

    // FreeRange: Center strip
    private val FREE_RANGE_Y_RANGE = 5..14

    // Market: Bottom strip
    private val MARKET_Y_RANGE = 15..19

    /**
     * Convert grid coordinates to isometric screen coordinates.
     * Uses 2:1 ratio: x' = (gridX - gridY) * TILE_SIZE
     *                 y' = (gridX + gridY) * (TILE_SIZE / 2)
     */
    fun toIsometric(gridX: Float, gridY: Float): Offset {
        val isoX = (gridX - gridY) * TILE_SIZE
        val isoY = (gridX + gridY) * (TILE_SIZE / 2f)
        return Offset(isoX, isoY)
    }

    /**
     * Convert screen/tap coordinates back to grid coordinates.
     * Inverse of toIsometric.
     */
    fun toGrid(screenX: Float, screenY: Float, scale: Float = 1f, mapCenter: Offset = Offset.Zero, panOffset: Offset = Offset.Zero): Offset {
        // Reverse the transform: local = (screen - mapCenter - pan) / scale
        val localX = (screenX - mapCenter.x - panOffset.x) / scale
        val localY = (screenY - mapCenter.y - panOffset.y) / scale

        // Reverse Isometric: gridX = (isoX / TILE + isoY / (TILE/2)) / 2
        //                    gridY = (isoY / (TILE/2) - isoX / TILE) / 2
        val isoX = localX
        val isoY = localY

        val gridX = (isoX / TILE_SIZE + isoY / (TILE_SIZE / 2f)) / 2f
        val gridY = (isoY / (TILE_SIZE / 2f) - isoX / TILE_SIZE) / 2f

        return Offset(gridX, gridY)
    }

    /**
     * Determine which zone a grid position belongs to.
     */
    fun getZoneForGridPosition(gridX: Float, gridY: Float): DigitalFarmZone {
        val x = gridX.toInt().coerceIn(0, GRID_SIZE - 1)
        val y = gridY.toInt().coerceIn(0, GRID_SIZE - 1)

        return when {
            y in NURSERY_Y_RANGE && x in NURSERY_X_RANGE -> DigitalFarmZone.NURSERY
            y in BREEDING_Y_RANGE && x in BREEDING_X_RANGE -> DigitalFarmZone.BREEDING_UNIT
            y in MARKET_Y_RANGE -> DigitalFarmZone.MARKET_STAND
            y in FREE_RANGE_Y_RANGE -> DigitalFarmZone.FREE_RANGE
            else -> DigitalFarmZone.FREE_RANGE // Default
        }
    }

    /**
     * Get the center of a zone in grid coordinates for spawning entities.
     */
    fun getZoneCenter(zone: DigitalFarmZone): Offset {
        return when (zone) {
            DigitalFarmZone.NURSERY -> Offset(4.5f, 2f)
            DigitalFarmZone.BREEDING_UNIT -> Offset(14.5f, 2f)
            DigitalFarmZone.FREE_RANGE -> Offset(10f, 9.5f)
            DigitalFarmZone.GROW_OUT -> Offset(10f, 12f)
            DigitalFarmZone.READY_DISPLAY -> Offset(5f, 10f)
            DigitalFarmZone.MARKET_STAND -> Offset(10f, 17f)
            DigitalFarmZone.QUARANTINE -> Offset(17.5f, 17f)
            DigitalFarmZone.MAIN_COOP -> Offset(10f, 4f)
        }
    }

    /**
     * Get random position within a zone for bird spawning.
     */
    fun getRandomPositionInZone(zone: DigitalFarmZone): Offset {
        val (xRange, yRange) = when (zone) {
            DigitalFarmZone.NURSERY -> (1f..8f) to (1f..3f)
            DigitalFarmZone.BREEDING_UNIT -> (11f..18f) to (1f..3f)
            DigitalFarmZone.FREE_RANGE -> (1f..18f) to (6f..13f)
            DigitalFarmZone.GROW_OUT -> (1f..18f) to (10f..13f)
            DigitalFarmZone.READY_DISPLAY -> (3f..7f) to (8f..12f)
            DigitalFarmZone.MARKET_STAND -> (5f..15f) to (16f..18f)
            DigitalFarmZone.QUARANTINE -> (16f..19f) to (15f..19f)
            DigitalFarmZone.MAIN_COOP -> (8f..12f) to (2f..6f)
        }
        val x = xRange.start + (xRange.endInclusive - xRange.start) * Math.random().toFloat()
        val y = yRange.start + (yRange.endInclusive - yRange.start) * Math.random().toFloat()
        return Offset(x, y)
    }

    /**
     * Check if a grid position is within a specific zone.
     */
    fun isInZone(gridX: Float, gridY: Float, zone: DigitalFarmZone): Boolean {
        return getZoneForGridPosition(gridX, gridY) == zone
    }

    /**
     * Calculate orbit position for chick around mother.
     * @param index Chick index (0-based)
     * @param totalChicks Total number of chicks orbiting
     * @param orbitRadius Radius of orbit in grid units
     * @param animationPhase Current animation phase (0-2π)
     */
    fun calculateChickOrbitPosition(
        motherPosition: Offset,
        index: Int,
        totalChicks: Int,
        orbitRadius: Float = 1.5f,
        animationPhase: Float = 0f
    ): Offset {
        val angleStep = (2 * Math.PI / totalChicks).toFloat()
        val angle = angleStep * index + animationPhase
        val x = motherPosition.x + orbitRadius * kotlin.math.cos(angle)
        val y = motherPosition.y + orbitRadius * kotlin.math.sin(angle) * 0.5f // Flatten for isometric
        return Offset(x, y)
    }
}
