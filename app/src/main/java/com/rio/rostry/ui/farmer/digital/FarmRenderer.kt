package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import com.rio.rostry.domain.model.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * FarmRenderer: Core rendering logic for the 2.5D Digital Farm.
 *
 * Responsibilities:
 * - Draw static background (ground tiles, zones, fences)
 * - Draw asset structures (Huts, Cages, Market Stand, Nests)
 * - Draw birds with Z-ordering (sorted by Y)
 * - Draw overlays (gold stars, status icons)
 */
object FarmRenderer {

    // Zone Colors
    private val NURSERY_COLOR = Color(0xFFFFF9C4) // Light Yellow
    private val BREEDING_COLOR = Color(0xFFFFCDD2) // Light Pink
    private val FREE_RANGE_COLOR = Color(0xFFC8E6C9) // Light Green
    private val MARKET_COLOR = Color(0xFFBBDEFB) // Light Blue
    private val DEFAULT_COLOR = Color(0xFFA5D6A7) // Green

    // Asset Colors
    private val HUT_ROOF_COLOR = Color(0xFFB71C1C) // Dark Red
    private val HUT_WALL_COLOR = Color(0xFF8D6E63) // Brown
    private val CAGE_BAR_COLOR = Color(0xFF757575) // Gray
    private val GOLD_STAR_COLOR = Color(0xFFFFD700) // Gold
    private val NEST_COLOR = Color(0xFF8D6E63) // Brown
    private val MARKET_AWNING_COLOR = Color(0xFFE57373) // Red

    /**
     * Main render function. Draws the entire farm scene.
     */
    fun renderFarm(
        drawScope: DrawScope,
        state: DigitalFarmState,
        scale: Float,
        panOffset: Offset,
        mapCenter: Offset,
        animationTime: Float = 0f,
        selectedBirdId: String? = null,
        dragState: DragState? = null
    ) {
        drawScope.apply {
            // 1. Draw Ground/Tiles
            drawGroundTiles(scale, panOffset, mapCenter)

            // 2. Collect all drawable entities for Z-ordering
            val entities = mutableListOf<DrawableEntity>()

            // Add Nursery Nests
            state.nurseries.forEach { nursery ->
                entities.add(
                    DrawableEntity(
                        id = "nest_${nursery.mother.productId}",
                        type = EntityType.NEST,
                        y = nursery.nestPosition.y,
                        position = nursery.nestPosition,
                        data = nursery
                    )
                )
            }

            // Add Breeding Huts
            state.breedingUnits.forEach { unit ->
                entities.add(
                    DrawableEntity(
                        id = "hut_${unit.unitId}",
                        type = EntityType.HUT,
                        y = unit.hutPosition.y,
                        position = unit.hutPosition,
                        data = unit
                    )
                )
            }

            // Add Ready Display Cages
            state.readyDisplay.filter { it.gender?.equals("male", true) == true }.forEach { bird ->
                entities.add(
                    DrawableEntity(
                        id = "cage_${bird.productId}",
                        type = EntityType.CAGE,
                        y = bird.position.y,
                        position = bird.position,
                        data = CageDisplay(bird, bird.position, showGoldStar = true)
                    )
                )
            }

            // Add all birds as individual entities
            val allBirds = state.freeRange + state.growOut + state.marketReady
            allBirds.forEach { bird ->
                entities.add(
                    DrawableEntity(
                        id = bird.productId,
                        type = EntityType.BIRD,
                        y = bird.position.y,
                        position = bird.position,
                        data = bird
                    )
                )
            }

            // 3. Sort by Y for Z-ordering (lower Y drawn first = further back)
            val sortedEntities = entities.sortedBy { it.y }

            // 4. Draw entities in order
            sortedEntities.forEach { entity ->
                val isoPos = IsometricGrid.toIsometric(entity.position.x, entity.position.y)
                val screenPos = Offset(
                    mapCenter.x + panOffset.x + (isoPos.x * scale),
                    mapCenter.y + panOffset.y + (isoPos.y * scale)
                )

                when (entity.type) {
                    EntityType.NEST -> drawNurseryNest(screenPos, scale, entity.data as NurseryGroup)
                    EntityType.HUT -> drawBreedingHut(screenPos, scale, entity.data as BreedingUnit, animationTime)
                    EntityType.CAGE -> drawCageAsset(screenPos, scale, entity.data as CageDisplay)
                    EntityType.BIRD -> drawBird(screenPos, scale, entity.data as VisualBird, selectedBirdId, animationTime)
                    EntityType.MARKET_STAND -> drawMarketStand(screenPos, scale, listOf())
                    else -> { /* Fence, Feeder drawn as part of static background */ }
                }
            }

            // 5. Draw Nursery Chicks (orbiting mothers)
            state.nurseries.forEach { nursery ->
                nursery.chicks.forEachIndexed { index, chick ->
                    val orbitPos = IsometricGrid.calculateChickOrbitPosition(
                        motherPosition = nursery.nestPosition,
                        index = index,
                        totalChicks = nursery.chicks.size,
                        orbitRadius = 1.5f,
                        animationPhase = animationTime * 0.001f
                    )
                    val isoPos = IsometricGrid.toIsometric(orbitPos.x, orbitPos.y)
                    val screenPos = Offset(
                        mapCenter.x + panOffset.x + (isoPos.x * scale),
                        mapCenter.y + panOffset.y + (isoPos.y * scale)
                    )
                    drawChick(screenPos, scale, chick, animationTime)
                }
            }

            // 6. Draw Breeding Unit Birds (constrained to hut area)
            state.breedingUnits.forEach { unit ->
                val hutIsoPos = IsometricGrid.toIsometric(unit.hutPosition.x, unit.hutPosition.y)
                val hutScreenPos = Offset(
                    mapCenter.x + panOffset.x + (hutIsoPos.x * scale),
                    mapCenter.y + panOffset.y + (hutIsoPos.y * scale)
                )
                drawBreedingUnitBirds(hutScreenPos, scale, unit, animationTime)
            }

            // 7. Draw Dragged Bird (if any)
            dragState?.let { drag ->
                if (drag.isDragging && drag.draggedBird != null) {
                    drawDraggedBird(drag.currentPosition, scale, drag.draggedBird)
                }
            }
        }
    }

    /**
     * Draw ground tiles with zone-based coloring.
     */
    private fun DrawScope.drawGroundTiles(scale: Float, panOffset: Offset, mapCenter: Offset) {
        for (row in 0 until IsometricGrid.GRID_SIZE) {
            for (col in 0 until IsometricGrid.GRID_SIZE) {
                val zone = IsometricGrid.getZoneForGridPosition(col.toFloat(), row.toFloat())
                val baseColor = when (zone) {
                    DigitalFarmZone.NURSERY -> NURSERY_COLOR
                    DigitalFarmZone.BREEDING_UNIT -> BREEDING_COLOR
                    DigitalFarmZone.MARKET_STAND -> MARKET_COLOR
                    DigitalFarmZone.FREE_RANGE, DigitalFarmZone.GROW_OUT -> FREE_RANGE_COLOR
                    else -> DEFAULT_COLOR
                }

                val color = if ((row + col) % 2 == 0) baseColor else baseColor.copy(alpha = 0.85f)
                val isoPos = IsometricGrid.toIsometric(col.toFloat(), row.toFloat())
                val screenX = mapCenter.x + panOffset.x + (isoPos.x * scale)
                val screenY = mapCenter.y + panOffset.y + (isoPos.y * scale)

                val halfW = IsometricGrid.TILE_SIZE * scale
                val halfH = (IsometricGrid.TILE_SIZE / 2f) * scale

                val path = Path().apply {
                    moveTo(screenX, screenY - halfH)
                    lineTo(screenX + halfW, screenY)
                    lineTo(screenX, screenY + halfH)
                    lineTo(screenX - halfW, screenY)
                    close()
                }
                drawPath(path, color)
                drawPath(path, Color.Black.copy(alpha = 0.03f), style = Stroke(width = 0.5f * scale))
            }
        }
    }

    /**
     * Draw Nursery Nest asset with straw texture.
     */
    private fun DrawScope.drawNurseryNest(position: Offset, scale: Float, nursery: NurseryGroup) {
        val radius = 30f * scale
        // Nest base (brown circle)
        drawCircle(NEST_COLOR, radius, position)
        // Straw texture (radial lines)
        for (i in 0 until 8) {
            val angle = (i * 45f) * (Math.PI / 180f).toFloat()
            val endX = position.x + radius * 0.8f * cos(angle)
            val endY = position.y + radius * 0.8f * sin(angle)
            drawLine(NEST_COLOR.copy(alpha = 0.6f), position, Offset(endX, endY), strokeWidth = 2f * scale)
        }
        // Eggs indicator if ghost eggs
        if (nursery.chicks.isEmpty()) {
            drawCircle(Color(0xFFFFF8E1), radius * 0.3f, position) // Egg color
        }
    }

    /**
     * Draw Breeding Hut with roof, walls, door, and egg count badge.
     */
    private fun DrawScope.drawBreedingHut(position: Offset, scale: Float, unit: BreedingUnit, animationTime: Float) {
        val hutWidth = 60f * scale
        val hutHeight = 40f * scale
        val roofHeight = 25f * scale

        // Base (wall)
        drawRect(
            HUT_WALL_COLOR,
            topLeft = Offset(position.x - hutWidth / 2, position.y - hutHeight),
            size = Size(hutWidth, hutHeight)
        )

        // Roof (Triangle)
        val roofPath = Path().apply {
            moveTo(position.x, position.y - hutHeight - roofHeight)
            lineTo(position.x - hutWidth / 2 - 5 * scale, position.y - hutHeight)
            lineTo(position.x + hutWidth / 2 + 5 * scale, position.y - hutHeight)
            close()
        }
        drawPath(roofPath, HUT_ROOF_COLOR)

        // Door
        drawRect(
            Color(0xFF5D4037),
            topLeft = Offset(position.x - 8 * scale, position.y - hutHeight * 0.6f),
            size = Size(16f * scale, hutHeight * 0.6f)
        )

        // Egg Count Badge
        if (unit.eggsCollectedToday > 0 || unit.showGhostEggs) {
            val badgeColor = if (unit.showGhostEggs) Color.Red else Color(0xFFFFF176)
            drawCircle(badgeColor, 12f * scale, Offset(position.x + hutWidth / 2, position.y - hutHeight - roofHeight / 2))
            // Note: Text drawing requires TextMeasurer, simplified to circle indicator
        }
    }

    /**
     * Draw Cage asset for ready-to-sell roosters with gold star.
     */
    private fun DrawScope.drawCageAsset(position: Offset, scale: Float, cageDisplay: CageDisplay) {
        val cageWidth = 40f * scale
        val cageHeight = 50f * scale
        val barSpacing = 5f * scale

        // Base platform
        drawRect(
            Color(0xFF8D6E63),
            topLeft = Offset(position.x - cageWidth / 2, position.y - 5 * scale),
            size = Size(cageWidth, 5f * scale)
        )

        // Vertical bars
        var x = position.x - cageWidth / 2
        while (x <= position.x + cageWidth / 2) {
            drawLine(CAGE_BAR_COLOR, Offset(x, position.y - cageHeight), Offset(x, position.y), strokeWidth = 2f * scale)
            x += barSpacing
        }

        // Bird inside (smaller, centered)
        drawBird(Offset(position.x, position.y - cageHeight / 2), scale * 0.7f, cageDisplay.bird, null, 0f)

        // Gold Star overlay
        if (cageDisplay.showGoldStar) {
            drawStar(Offset(position.x + cageWidth / 2 - 5 * scale, position.y - cageHeight), 10f * scale, GOLD_STAR_COLOR)
        }
    }

    /**
     * Draw a 5-point star.
     */
    private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
        val path = Path()
        val angleStep = (Math.PI / 5).toFloat() // 36 degrees
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) radius else radius * 0.5f
            val angle = (i * angleStep) - (Math.PI / 2).toFloat()
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
    }

    /**
     * Draw a single bird entity.
     */
    private fun DrawScope.drawBird(position: Offset, scale: Float, bird: VisualBird, selectedBirdId: String?, animationTime: Float) {
        val radius = when {
            bird.ageWeeks < 4 -> 8f * scale
            bird.ageWeeks < 12 -> 12f * scale
            else -> 16f * scale
        }

        // Idle bobbing
        val bobOffset = (sin(animationTime * 0.003f + bird.productId.hashCode()) * 2f) * scale

        val drawPosition = Offset(position.x, position.y + bobOffset)

        // Selection glow
        if (bird.productId == selectedBirdId) {
            drawCircle(Color.White.copy(alpha = 0.5f), radius * 1.8f, drawPosition)
        }

        // Ready-to-sell glow
        if (bird.isReadyForSale) {
            drawCircle(GOLD_STAR_COLOR.copy(alpha = 0.4f), radius * 1.5f, drawPosition)
        }

        // Bird body
        val birdColor = when (bird.color?.lowercase()) {
            "red" -> Color(0xFFB71C1C)
            "brown" -> Color(0xFF8D6E63)
            "black" -> Color(0xFF424242)
            "white" -> Color(0xFFFAFAFA)
            else -> Color(0xFF795548)
        }
        drawCircle(birdColor, radius, drawPosition)

        // Status indicator
        when (bird.statusIndicator) {
            BirdStatusIndicator.VACCINE_DUE -> drawCircle(Color.Red, radius * 0.3f, Offset(drawPosition.x + radius * 0.7f, drawPosition.y - radius * 0.7f))
            BirdStatusIndicator.WEIGHT_READY -> drawStar(Offset(drawPosition.x, drawPosition.y - radius * 1.3f), 5f * scale, GOLD_STAR_COLOR)
            BirdStatusIndicator.SICK -> drawCircle(Color.Red.copy(alpha = 0.8f), radius * 0.4f, drawPosition)
            else -> {}
        }
    }

    /**
     * Draw a small chick (for nursery orbiting).
     */
    private fun DrawScope.drawChick(position: Offset, scale: Float, chick: VisualBird, animationTime: Float) {
        val radius = 5f * scale
        val bobOffset = (sin(animationTime * 0.005f + chick.productId.hashCode()) * 1f) * scale
        val drawPosition = Offset(position.x, position.y + bobOffset)

        drawCircle(Color(0xFFFFF176), radius, drawPosition) // Yellow chick
        drawCircle(Color(0xFFFFB74D), radius * 0.4f, Offset(drawPosition.x + radius * 0.5f, drawPosition.y - radius * 0.3f)) // Beak
    }

    /**
     * Draw birds inside a breeding hut (constrained area).
     */
    private fun DrawScope.drawBreedingUnitBirds(hutPosition: Offset, scale: Float, unit: BreedingUnit, animationTime: Float) {
        val yardWidth = 50f * scale
        val yardHeight = 30f * scale

        // Rooster (center)
        unit.rooster?.let { rooster ->
            drawBird(Offset(hutPosition.x, hutPosition.y + yardHeight * 0.3f), scale, rooster, null, animationTime)
        }

        // Hens (arranged around rooster)
        unit.hens.forEachIndexed { index, hen ->
            val offsetX = ((index % 3) - 1) * 15f * scale
            val offsetY = (index / 3) * 10f * scale + yardHeight * 0.5f
            drawBird(Offset(hutPosition.x + offsetX, hutPosition.y + offsetY), scale * 0.85f, hen, null, animationTime)
        }
    }

    /**
     * Draw Market Stand asset.
     */
    private fun DrawScope.drawMarketStand(position: Offset, scale: Float, birds: List<VisualBird>) {
        val standWidth = 80f * scale
        val standHeight = 30f * scale
        val awningHeight = 20f * scale

        // Table
        drawRect(
            Color(0xFF8D6E63),
            topLeft = Offset(position.x - standWidth / 2, position.y - standHeight),
            size = Size(standWidth, standHeight)
        )

        // Awning (striped)
        val awningPath = Path().apply {
            moveTo(position.x - standWidth / 2 - 10 * scale, position.y - standHeight)
            lineTo(position.x, position.y - standHeight - awningHeight)
            lineTo(position.x + standWidth / 2 + 10 * scale, position.y - standHeight)
            close()
        }
        drawPath(awningPath, MARKET_AWNING_COLOR)
    }

    /**
     * Draw a bird being dragged (ghost effect).
     */
    private fun DrawScope.drawDraggedBird(position: Offset, scale: Float, bird: VisualBird) {
        val radius = 16f * scale
        drawCircle(Color(0xFF795548).copy(alpha = 0.5f), radius, position) // Ghost
        drawCircle(Color.White, radius, position, style = Stroke(width = 2f * scale)) // Outline
    }
}
