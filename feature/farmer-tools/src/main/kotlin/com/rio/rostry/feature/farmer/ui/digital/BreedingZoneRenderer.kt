package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rio.rostry.domain.model.DigitalFarmZone
import kotlin.math.cos
import kotlin.math.sin

/**
 * BreedingZoneRenderer: Renders breeding zones with visual enhancements.
 * 
 * Features:
 * - Zone boundaries with distinct colors
 * - Pairing indicators (hearts, connection lines)
 * - Nest sprites
 * - Egg visualization
 * - Fertility heatmap overlay
 */
object BreedingZoneRenderer {
    
    // Zone colors
    private val ZONE_COLORS = mapOf(
        DigitalFarmZone.NURSERY to Color(0xFFFFE4B5), // Light peach
        DigitalFarmZone.BREEDING_UNIT to Color(0xFFFFB6C1), // Light pink
        DigitalFarmZone.FREE_RANGE to Color(0xFF98FB98), // Pale green
        DigitalFarmZone.GROW_OUT to Color(0xFFADD8E6), // Light blue
        DigitalFarmZone.READY_DISPLAY to Color(0xFFDDA0DD), // Plum
        DigitalFarmZone.READY_DISPLAY to Color(0xFFDDA0DD), // Plum
        DigitalFarmZone.MARKET_STAND to Color(0xFFFFD700), // Gold
        DigitalFarmZone.QUARANTINE to Color(0xFFEF5350), // Red
        DigitalFarmZone.MAIN_COOP to Color(0xFF8D6E63) // Brown
    )
    
    /**
     * Breeding pair for visualization.
     */
    data class BreedingPair(
        val male: Offset,
        val female: Offset,
        val pairId: String,
        val nestPosition: Offset,
        val eggCount: Int = 0,
        val fertilityScore: Float = 0f // 0-1
    )
    
    /**
     * Render all zone tiles with isometric projection.
     */
    fun DrawScope.renderZoneTiles(
        scale: Float = 1f,
        mapCenter: Offset = center
    ) {
        val tileSize = IsometricGrid.TILE_SIZE * scale
        
        // Render ground tiles for each zone
        for (y in 0 until IsometricGrid.GRID_SIZE) {
            for (x in 0 until IsometricGrid.GRID_SIZE) {
                val zone = IsometricGrid.getZoneForGridPosition(x.toFloat(), y.toFloat())
                val color = ZONE_COLORS[zone] ?: Color.Gray
                
                val iso = IsometricGrid.toIsometric(x.toFloat(), y.toFloat())
                val screenPos = Offset(
                    mapCenter.x + iso.x * scale,
                    mapCenter.y + iso.y * scale
                )
                
                // Draw isometric tile (diamond shape)
                drawIsometricTile(screenPos, tileSize, color.copy(alpha = 0.4f))
            }
        }
        
        // Draw zone boundaries
        DigitalFarmZone.entries.forEach { zone ->
            drawZoneBoundary(zone, scale, mapCenter)
        }
    }
    
    /**
     * Draw a single isometric tile.
     */
    private fun DrawScope.drawIsometricTile(
        center: Offset,
        size: Float,
        color: Color
    ) {
        val halfWidth = size
        val halfHeight = size / 2f
        
        val path = Path().apply {
            moveTo(center.x, center.y - halfHeight)  // Top
            lineTo(center.x + halfWidth, center.y)    // Right
            lineTo(center.x, center.y + halfHeight)   // Bottom
            lineTo(center.x - halfWidth, center.y)    // Left
            close()
        }
        
        drawPath(path, color)
        drawPath(path, Color.Black.copy(alpha = 0.1f), style = Stroke(width = 1f))
    }
    
    /**
     * Draw zone boundary lines.
     */
    private fun DrawScope.drawZoneBoundary(
        zone: DigitalFarmZone,
        scale: Float,
        mapCenter: Offset
    ) {
        val color = (ZONE_COLORS[zone] ?: Color.Gray).copy(alpha = 0.8f)
        val bounds = getZoneBoundsOffset(zone)
        
        val corners = listOf(
            IsometricGrid.toIsometric(bounds.first, bounds.second),
            IsometricGrid.toIsometric(bounds.third, bounds.second),
            IsometricGrid.toIsometric(bounds.third, bounds.fourth),
            IsometricGrid.toIsometric(bounds.first, bounds.fourth)
        ).map { iso ->
            Offset(mapCenter.x + iso.x * scale, mapCenter.y + iso.y * scale)
        }
        
        // Draw boundary lines
        val path = Path().apply {
            moveTo(corners[0].x, corners[0].y)
            corners.drop(1).forEach { lineTo(it.x, it.y) }
            close()
        }
        
        drawPath(path, color, style = Stroke(width = 3f))
    }
    
    /**
     * Render breeding pairs with connection lines and hearts.
     */
    fun DrawScope.renderBreedingPairs(
        pairs: List<BreedingPair>,
        scale: Float = 1f,
        mapCenter: Offset = center,
        animationTime: Float = 0f
    ) {
        pairs.forEach { pair ->
            // Convert to screen coordinates
            val maleScreen = gridToScreen(pair.male, scale, mapCenter)
            val femaleScreen = gridToScreen(pair.female, scale, mapCenter)
            val nestScreen = gridToScreen(pair.nestPosition, scale, mapCenter)
            
            // Draw connection line (curved)
            drawPairingLine(maleScreen, femaleScreen, pair.fertilityScore)
            
            // Draw heart indicator
            val heartPos = Offset(
                (maleScreen.x + femaleScreen.x) / 2f,
                (maleScreen.y + femaleScreen.y) / 2f - 20f
            )
            drawHeart(heartPos, pair.fertilityScore, animationTime)
            
            // Draw nest
            drawNest(nestScreen, pair.eggCount)
        }
    }
    
    /**
     * Draw a curved pairing line between male and female.
     */
    private fun DrawScope.drawPairingLine(
        start: Offset,
        end: Offset,
        fertility: Float
    ) {
        val color = when {
            fertility >= 0.8f -> Color(0xFFE91E63) // Pink (high fertility)
            fertility >= 0.5f -> Color(0xFFFF9800) // Orange (medium)
            else -> Color(0xFF9E9E9E) // Gray (low)
        }
        
        val midX = (start.x + end.x) / 2f
        val midY = minOf(start.y, end.y) - 30f // Curve upward
        
        val path = Path().apply {
            moveTo(start.x, start.y)
            quadraticTo(midX, midY, end.x, end.y)
        }
        
        drawPath(path, color.copy(alpha = 0.6f), style = Stroke(width = 2f))
    }
    
    /**
     * Draw a heart indicator.
     */
    private fun DrawScope.drawHeart(
        position: Offset,
        fertility: Float,
        animationTime: Float
    ) {
        val pulseScale = 1f + sin(animationTime * 0.005f) * 0.1f * fertility
        val size = 12f * pulseScale
        
        val color = when {
            fertility >= 0.8f -> Color(0xFFE91E63)
            fertility >= 0.5f -> Color(0xFFFF5722)
            else -> Color(0xFF9E9E9E)
        }
        
        // Simple heart shape using two circles and a triangle
        val leftCircle = Offset(position.x - size * 0.3f, position.y)
        val rightCircle = Offset(position.x + size * 0.3f, position.y)
        
        drawCircle(color, size * 0.5f, leftCircle)
        drawCircle(color, size * 0.5f, rightCircle)
        
        val path = Path().apply {
            moveTo(position.x - size * 0.6f, position.y + size * 0.1f)
            lineTo(position.x, position.y + size)
            lineTo(position.x + size * 0.6f, position.y + size * 0.1f)
            close()
        }
        drawPath(path, color)
    }
    
    /**
     * Draw a nest with eggs.
     */
    private fun DrawScope.drawNest(position: Offset, eggCount: Int) {
        val nestColor = Color(0xFF8B4513) // Brown
        val eggColor = Color(0xFFFFFAF0) // Cream
        
        // Draw nest base (ellipse)
        drawOval(
            color = nestColor,
            topLeft = Offset(position.x - 20f, position.y - 8f),
            size = Size(40f, 16f)
        )
        
        // Draw eggs (up to 5)
        val eggsToDraw = minOf(eggCount, 5)
        repeat(eggsToDraw) { i ->
            val angle = (i.toFloat() / eggsToDraw) * 2f * Math.PI.toFloat()
            val eggX = position.x + cos(angle) * 10f
            val eggY = position.y - 4f + sin(angle) * 3f
            
            drawOval(
                color = eggColor,
                topLeft = Offset(eggX - 4f, eggY - 6f),
                size = Size(8f, 12f)
            )
        }
        
        // Show count if more than 5
        if (eggCount > 5) {
            // Would draw text here, but DrawScope doesn't support text directly
            // In real implementation, use native canvas or compose Text
        }
    }
    
    /**
     * Render fertility heatmap overlay.
     */
    fun DrawScope.renderFertilityHeatmap(
        fertilityData: Map<Offset, Float>, // Grid position to fertility score
        scale: Float = 1f,
        mapCenter: Offset = center
    ) {
        fertilityData.forEach { (gridPos, fertility) ->
            val screenPos = gridToScreen(gridPos, scale, mapCenter)
            
            val color = when {
                fertility >= 0.8f -> Color(0x40E91E63) // High - pink
                fertility >= 0.5f -> Color(0x40FFEB3B) // Medium - yellow
                else -> Color(0x209E9E9E) // Low - gray
            }
            
            drawCircle(
                color = color,
                radius = 30f * scale,
                center = screenPos
            )
        }
    }
    
    /**
     * Convert grid position to screen position.
     */
    private fun gridToScreen(
        gridPos: Offset,
        scale: Float,
        mapCenter: Offset
    ): Offset {
        val iso = IsometricGrid.toIsometric(gridPos.x, gridPos.y)
        return Offset(
            mapCenter.x + iso.x * scale,
            mapCenter.y + iso.y * scale
        )
    }
    
    /**
     * Get zone bounds as (minX, minY, maxX, maxY).
     */
    private fun getZoneBoundsOffset(zone: DigitalFarmZone): Quadruple<Float, Float, Float, Float> {
        return when (zone) {
            DigitalFarmZone.NURSERY -> Quadruple(0f, 0f, 9f, 4f)
            DigitalFarmZone.BREEDING_UNIT -> Quadruple(10f, 0f, 19f, 4f)
            DigitalFarmZone.FREE_RANGE -> Quadruple(0f, 5f, 19f, 14f)
            DigitalFarmZone.GROW_OUT -> Quadruple(0f, 10f, 19f, 14f)
            DigitalFarmZone.READY_DISPLAY -> Quadruple(3f, 8f, 7f, 12f)
            DigitalFarmZone.MARKET_STAND -> Quadruple(5f, 15f, 15f, 19f)
            DigitalFarmZone.QUARANTINE -> Quadruple(16f, 15f, 19f, 19f)
            DigitalFarmZone.MAIN_COOP -> Quadruple(8f, 2f, 12f, 6f)
        }
    }
    
    data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
