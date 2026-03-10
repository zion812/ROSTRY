package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rio.rostry.domain.model.DigitalFarmZone

/**
 * ZoneManager: Manages and renders zone-based layout for the Digital Farm 2.5D.
 * 
 * Zone Types:
 * - COOP: Main housing area
 * - FEEDING_AREA: Where birds eat
 * - WATER_STATION: Drinking area
 * - BREEDING_PEN: For breeding pairs
 * - EGG_COLLECTION: Egg laying and collection
 */
object ZoneManager {
    
    data class FarmZone(
        val zoneId: String,
        val type: ZoneType,
        val bounds: Rect,
        val capacity: Int,
        val currentOccupancy: Int = 0,
        val color: Color,
        val label: String
    ) {
        val isFull: Boolean get() = currentOccupancy >= capacity
        val occupancyPercent: Float get() = if (capacity > 0) currentOccupancy.toFloat() / capacity else 0f
    }
    
    enum class ZoneType {
        COOP,
        FEEDING_AREA,
        WATER_STATION,
        BREEDING_PEN,
        EGG_COLLECTION,
        FREE_RANGE,
        QUARANTINE
    }
    
    /**
     * Default zone colors for each type.
     */
    private val zoneColors = mapOf(
        ZoneType.COOP to Color(0xFFB8860B),          // Dark goldenrod
        ZoneType.FEEDING_AREA to Color(0xFF8B4513), // Saddle brown
        ZoneType.WATER_STATION to Color(0xFF4682B4),// Steel blue
        ZoneType.BREEDING_PEN to Color(0xFFFF69B4), // Hot pink
        ZoneType.EGG_COLLECTION to Color(0xFFFFF5EE),// Seashell
        ZoneType.FREE_RANGE to Color(0xFF90EE90),    // Light green
        ZoneType.QUARANTINE to Color(0xFFEF5350)     // Red
    )
    
    /**
     * Create default farm zones for a 20x20 grid.
     */
    fun createDefaultZones(): List<FarmZone> {
        return listOf(
            FarmZone(
                zoneId = "coop_1",
                type = ZoneType.COOP,
                bounds = Rect(0f, 0f, 5f, 3f),
                capacity = 20,
                color = zoneColors[ZoneType.COOP]!!,
                label = "Main Coop"
            ),
            FarmZone(
                zoneId = "feeding_1",
                type = ZoneType.FEEDING_AREA,
                bounds = Rect(6f, 0f, 10f, 3f),
                capacity = 15,
                color = zoneColors[ZoneType.FEEDING_AREA]!!,
                label = "Feeding Area"
            ),
            FarmZone(
                zoneId = "water_1",
                type = ZoneType.WATER_STATION,
                bounds = Rect(11f, 0f, 14f, 3f),
                capacity = 10,
                color = zoneColors[ZoneType.WATER_STATION]!!,
                label = "Water Station"
            ),
            FarmZone(
                zoneId = "breeding_1",
                type = ZoneType.BREEDING_PEN,
                bounds = Rect(0f, 4f, 6f, 8f),
                capacity = 8,
                color = zoneColors[ZoneType.BREEDING_PEN]!!,
                label = "Breeding Pen"
            ),
            FarmZone(
                zoneId = "eggs_1",
                type = ZoneType.EGG_COLLECTION,
                bounds = Rect(15f, 0f, 19f, 4f),
                capacity = 12,
                color = zoneColors[ZoneType.EGG_COLLECTION]!!,
                label = "Egg Collection"
            ),
            FarmZone(
                zoneId = "free_range",
                type = ZoneType.FREE_RANGE,
                bounds = Rect(0f, 9f, 19f, 19f),
                capacity = 50,
                color = zoneColors[ZoneType.FREE_RANGE]!!,
                label = "Free Range"
            )
        )
    }
    
    /**
     * Render zones on the canvas using isometric projection.
     */
    fun DrawScope.renderZones(
        zones: List<FarmZone>,
        cellSize: Float,
        isometricTransform: (Float, Float) -> Offset
    ) {
        zones.forEach { zone ->
            renderZone(zone, cellSize, isometricTransform)
        }
    }
    
    /**
     * Render a single zone as an isometric rectangle.
     */
    private fun DrawScope.renderZone(
        zone: FarmZone,
        cellSize: Float,
        isometricTransform: (Float, Float) -> Offset
    ) {
        // Get corner points in isometric space
        val topLeft = isometricTransform(zone.bounds.left, zone.bounds.top)
        val topRight = isometricTransform(zone.bounds.right, zone.bounds.top)
        val bottomRight = isometricTransform(zone.bounds.right, zone.bounds.bottom)
        val bottomLeft = isometricTransform(zone.bounds.left, zone.bounds.bottom)
        
        // Create isometric diamond path
        val path = Path().apply {
            moveTo(topLeft.x, topLeft.y)
            lineTo(topRight.x, topRight.y)
            lineTo(bottomRight.x, bottomRight.y)
            lineTo(bottomLeft.x, bottomLeft.y)
            close()
        }
        
        // Fill with zone color (semi-transparent)
        drawPath(
            path = path,
            color = zone.color.copy(alpha = 0.3f)
        )
        
        // Draw border
        val borderAlpha = if (zone.isFull) 0.8f else 0.5f
        drawPath(
            path = path,
            color = zone.color.copy(alpha = borderAlpha),
            style = Stroke(width = if (zone.isFull) 3f else 2f)
        )
        
        // Draw occupancy indicator bar at bottom
        if (zone.currentOccupancy > 0) {
            val barWidth = (zone.bounds.width * cellSize * 0.6f) * zone.occupancyPercent
            val barCenter = isometricTransform(
                (zone.bounds.left + zone.bounds.right) / 2,
                zone.bounds.bottom - 0.5f
            )
            
            // Background bar
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(barCenter.x - zone.bounds.width * cellSize * 0.3f, barCenter.y),
                end = Offset(barCenter.x + zone.bounds.width * cellSize * 0.3f, barCenter.y),
                strokeWidth = 4f
            )
            
            // Occupancy fill
            val fillColor = when {
                zone.occupancyPercent >= 0.9f -> Color.Red
                zone.occupancyPercent >= 0.7f -> Color.Yellow
                else -> Color.Green
            }
            drawLine(
                color = fillColor,
                start = Offset(barCenter.x - zone.bounds.width * cellSize * 0.3f, barCenter.y),
                end = Offset(barCenter.x - zone.bounds.width * cellSize * 0.3f + barWidth, barCenter.y),
                strokeWidth = 4f
            )
        }
    }
    
    /**
     * Find the zone at a given grid position.
     */
    fun findZoneAt(zones: List<FarmZone>, gridX: Float, gridY: Float): FarmZone? {
        return zones.find { zone ->
            gridX >= zone.bounds.left && gridX < zone.bounds.right &&
            gridY >= zone.bounds.top && gridY < zone.bounds.bottom
        }
    }
    
    /**
     * Get the best zone for a bird based on its type and lifecycle stage.
     */
    fun getBestZoneForBird(
        zones: List<FarmZone>,
        birdType: String,
        lifecycleStage: String,
        isBreeding: Boolean
    ): FarmZone? {
        val preferredType = when {
            isBreeding -> ZoneType.BREEDING_PEN
            lifecycleStage == "CHICK" -> ZoneType.COOP
            lifecycleStage == "LAYER" -> ZoneType.EGG_COLLECTION
            lifecycleStage == "ADULT" -> ZoneType.FREE_RANGE
            else -> ZoneType.FREE_RANGE
        }
        
        // Find zone of preferred type that isn't full
        return zones
            .filter { it.type == preferredType && !it.isFull }
            .minByOrNull { it.occupancyPercent }
            ?: zones.filter { !it.isFull }.firstOrNull() // Fallback to any available zone
    }
    
    /**
     * Update zone occupancy counts based on bird positions.
     */
    fun updateOccupancy(
        zones: List<FarmZone>,
        birdPositions: List<Offset>
    ): List<FarmZone> {
        return zones.map { zone ->
            val count = birdPositions.count { pos ->
                pos.x >= zone.bounds.left && pos.x < zone.bounds.right &&
                pos.y >= zone.bounds.top && pos.y < zone.bounds.bottom
            }
            zone.copy(currentOccupancy = count)
        }
    }
    
    /**
     * Map DigitalFarmZone enum to ZoneType.
     */
    fun DigitalFarmZone.toZoneType(): ZoneType {
        return when (this) {
            DigitalFarmZone.NURSERY -> ZoneType.COOP
            DigitalFarmZone.BREEDING_UNIT -> ZoneType.BREEDING_PEN
            DigitalFarmZone.FREE_RANGE -> ZoneType.FREE_RANGE
            DigitalFarmZone.GROW_OUT -> ZoneType.FEEDING_AREA
            DigitalFarmZone.READY_DISPLAY -> ZoneType.FREE_RANGE
            DigitalFarmZone.MARKET_STAND -> ZoneType.FREE_RANGE
            DigitalFarmZone.QUARANTINE -> ZoneType.QUARANTINE
            DigitalFarmZone.MAIN_COOP -> ZoneType.COOP
        }
    }
}
