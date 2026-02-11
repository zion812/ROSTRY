package com.rio.rostry.ui.enthusiast.digitalfarm

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.domain.model.*
import com.rio.rostry.ui.enthusiast.digitalfarm.BirdPartRenderer.drawBirdFromAppearance
import kotlin.math.*

// ==================== ISOMETRIC MATH ====================

/**
 * Converts world-space (grid) coordinates to screen-space isometric coordinates.
 * Uses standard isometric projection: 2:1 diamond tiles.
 */
private fun worldToIso(worldX: Float, worldY: Float, tileW: Float, tileH: Float): Offset {
    val screenX = (worldX - worldY) * (tileW / 2f)
    val screenY = (worldX + worldY) * (tileH / 2f)
    return Offset(screenX, screenY)
}

/**
 * Converts screen-space coordinates back to world-space (for tap detection).
 */
private fun isoToWorld(screenX: Float, screenY: Float, tileW: Float, tileH: Float): Offset {
    val worldX = (screenX / (tileW / 2f) + screenY / (tileH / 2f)) / 2f
    val worldY = (screenY / (tileH / 2f) - screenX / (tileW / 2f)) / 2f
    return Offset(worldX, worldY)
}

// ==================== COLORS ====================

private object IsoColors {
    // Sky
    val skyTop = Color(0xFF4FC3F7)
    val skyBottom = Color(0xFF81C784)

    // Terrain
    val grassTop = Color(0xFF66BB6A)
    val grassLight = Color(0xFF81C784)
    val grassDark = Color(0xFF4CAF50)
    val grassSide = Color(0xFF388E3C)
    val dirtSide = Color(0xFF795548)
    val dirtFront = Color(0xFF8D6E63)
    val dirtDark = Color(0xFF5D4037)
    val mud = Color(0xFF6D4C41)
    val puddle = Color(0xFF4DB6AC).copy(alpha = 0.6f)

    // Fence
    val fenceLight = Color(0xFFA1887F)
    val fenceDark = Color(0xFF795548)
    val fencePost = Color(0xFF6D4C41)

    // Coop
    val coopWall = Color(0xFFA1887F)
    val coopRoof = Color(0xFF795548)
    val coopRoofLight = Color(0xFF8D6E63)
    val coopDoor = Color(0xFF5D4037)
    val coopWindow = Color(0xFFBBDEFB)

    // Feeder / Water
    val feederBase = Color(0xFF8D6E63)
    val feederTop = Color(0xFFBCAAA4)
    val feedGrain = Color(0xFFFFCC80)
    val waterBlue = Color(0xFF4FC3F7)
    val troughWood = Color(0xFF795548)

    // Birds
    val birdWhite = Color(0xFFFAFAFA)
    val birdCream = Color(0xFFFFF8E1)
    val birdBrown = Color(0xFFBCAAA4)
    val birdRed = Color(0xFFE57373)    // Comb/wattle
    val birdBeak = Color(0xFFFFB74D)
    val birdFeet = Color(0xFFFFB74D)
    val birdShadow = Color(0x30000000)
    val birdSelected = Color(0xFFFFD700)

    // UI
    val bubbleBg = Color(0xF0FFFFFF)
    val bubbleText = Color(0xFF37474F)
    val bubbleAge = Color(0xFF4CAF50)
    val bubbleBreed = Color(0xFF7E57C2)
    val cloudWhite = Color(0xCCFFFFFF)
    val treeTrunk = Color(0xFF6D4C41)
    val treeGreen = Color(0xFF43A047)
    val treeDark = Color(0xFF2E7D32)

    // Stone
    val stone = Color(0xFF9E9E9E)
    val stoneDark = Color(0xFF757575)
}

// ==================== BIRD PLACEMENT ====================

private data class IsoBirdPlacement(
    val bird: VisualBird,
    val worldX: Float,
    val worldY: Float,
    val facing: Float = 0f, // angle in degrees
    val appearance: BirdAppearance = bird.metadataJson?.let { json ->
        if (json.isNotBlank()) parseAppearanceFromJson(json) else null
    } ?: deriveAppearanceFromBreed(bird.breed, bird.gender, bird.ageWeeks)
)

/**
 * Calculate bird placements on the isometric grid.
 * Distributes birds across the farm with some randomness but no overlap.
 */
private fun calculateBirdPlacements(state: DigitalFarmState): List<IsoBirdPlacement> {
    val placements = mutableListOf<IsoBirdPlacement>()
    val usedPositions = mutableSetOf<Pair<Int, Int>>()
    val random = java.util.Random(42) // Fixed seed for consistent layout

    fun addBird(bird: VisualBird, centerX: Float, centerY: Float, spread: Float) {
        var attempts = 0
        while (attempts < 20) {
            val wx = centerX + (random.nextFloat() - 0.5f) * spread
            val wy = centerY + (random.nextFloat() - 0.5f) * spread
            val key = Pair((wx * 2).toInt(), (wy * 2).toInt())
            if (key !in usedPositions) {
                usedPositions.add(key)
                placements.add(IsoBirdPlacement(bird, wx, wy, random.nextFloat() * 360f))
                return
            }
            attempts++
        }
        // Fallback: place anyway
        placements.add(IsoBirdPlacement(bird, centerX, centerY, random.nextFloat() * 360f))
    }

    // Nursery area (top-left of farm grid, ~1,1 to 3,3)
    state.nurseries.forEach { nursery ->
        addBird(nursery.mother, 1.5f, 1.5f, 1.5f)
        nursery.chicks.forEach { chick ->
            addBird(chick, 1.5f, 1.5f, 1.2f)
        }
    }

    // Free range (center of farm, ~2,3 to 5,5)
    state.freeRange.forEachIndexed { i, bird ->
        addBird(bird, 3.5f, 4f, 2.5f)
    }

    // Grow out (right side, ~4,1 to 6,3)
    state.growOut.forEachIndexed { i, bird ->
        addBird(bird, 5f, 2f, 1.8f)
    }

    // Breeding units (around coops, ~5,4 to 7,6)
    state.breedingUnits.forEach { unit ->
        unit.rooster?.let { addBird(it, 6f, 5f, 1.5f) }
        unit.hens.forEach { hen -> addBird(hen, 6f, 5f, 2f) }
    }

    // Ready display (showcase area, ~3,6 to 5,7)
    state.readyDisplay.forEachIndexed { i, bird ->
        addBird(bird, 4f, 6.5f, 1.5f)
    }

    // Market ready (near stand, ~1,5 to 3,7)
    state.marketReady.forEachIndexed { i, bird ->
        addBird(bird, 2f, 6f, 1.5f)
    }

    // Main coop (center, general)
    state.mainCoop.forEachIndexed { i, bird ->
        addBird(bird, 4f, 3f, 2.5f)
    }

    // Quarantine (isolated, ~7,1)
    state.quarantine.forEachIndexed { i, bird ->
        addBird(bird, 7f, 1f, 1f)
    }

    return placements
}

// ==================== MAIN COMPOSABLE ====================

@Composable
fun IsometricFarmCanvas(
    uiState: DigitalFarmState,
    animationTime: Float,
    selectedBirdId: String?,
    config: DigitalFarmConfig,
    onBirdTapped: (VisualBird) -> Unit,
    onNurseryTapped: (NurseryGroup) -> Unit,
    onBreedingHutTapped: (BreedingUnit) -> Unit,
    onMarketStandTapped: () -> Unit,
    highlightedBirdIds: Set<String> = emptySet(),
    isSearchActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Zoom & pan state (Google Maps-like)
    var scale by remember { mutableFloatStateOf(1.2f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Bird placements
    val birdPlacements = remember(uiState) { calculateBirdPlacements(uiState) }

    // Text measurer for drawing text on canvas
    val textMeasurer = rememberTextMeasurer()

    // Tile dimensions
    val tileW = 100f
    val tileH = 50f

    // Farm grid size
    val gridSize = 8

    Box(modifier = modifier.background(Color(0xFF81C784))) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                // Pinch-to-zoom + pan gesture
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(0.5f, 3.5f)
                        
                        // Adjust offset so zoom centers on the pinch point
                        val scaleChange = newScale / scale
                        offsetX = (offsetX - centroid.x) * scaleChange + centroid.x + pan.x
                        offsetY = (offsetY - centroid.y) * scaleChange + centroid.y + pan.y
                        
                        scale = newScale
                    }
                }
                // Tap gesture handler
                .pointerInput(uiState, scale, offsetX, offsetY) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            // Transform tap from screen space to canvas space
                            val canvasTapX = (tapOffset.x - offsetX - size.width / 2f) / scale
                            val canvasTapY = (tapOffset.y - offsetY - size.height * 0.35f) / scale

                            // Convert to world coords
                            val worldPos = isoToWorld(canvasTapX, canvasTapY, tileW, tileH)

                            // Find closest bird within tap radius
                            val tapRadius = 1.0f / scale // Adaptive radius
                            val hitBird = birdPlacements.minByOrNull { p ->
                                val dist = sqrt(
                                    (p.worldX - worldPos.x).pow(2) +
                                    (p.worldY - worldPos.y).pow(2)
                                )
                                dist
                            }?.takeIf { p ->
                                sqrt(
                                    (p.worldX - worldPos.x).pow(2) +
                                    (p.worldY - worldPos.y).pow(2)
                                ) < tapRadius
                            }

                            if (hitBird != null) {
                                onBirdTapped(hitBird.bird)
                            }
                        },
                        onDoubleTap = { _ ->
                            // Double-tap to reset zoom
                            scale = 1.2f
                            offsetX = 0f
                            offsetY = 0f
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height

            // Center the isometric grid in the canvas
            val centerX = w / 2f + offsetX
            val centerY = h * 0.35f + offsetY

            // ============ SKY GRADIENT ============
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(IsoColors.skyTop, IsoColors.skyBottom),
                    startY = 0f,
                    endY = h
                ),
                size = size
            )

            // Apply zoom + pan transformation for THE ENTIRE SCENE
            translate(left = centerX, top = centerY) {
                scale(scale = scale) {

                    // ============ CLOUDS ============
                    // Drawn in world space relative to center, so they zoom with the map
                    val cloudBaseY = -h * 0.25f // Adjusted for new coordinate system (0,0 is map center)
                    
                    // Note: Removed 'centerX' and '* scale' factors as transformation handles them
                    drawIsoCloud(-180f + animationTime * 8f % 1000f - 500f, cloudBaseY, 0.8f)
                    drawIsoCloud(100f + animationTime * 5f % 1000f - 500f, cloudBaseY + 30f, 0.6f)
                    drawIsoCloud(-50f + animationTime * 6.5f % 1000f - 500f, cloudBaseY + 60f, 0.7f)

                    // ============ BACKGROUND TREES ============
                    // Trees spaced relative to the grid
                    val treeBaseY = -gridSize * tileH / 2f - 40f
                    for (i in 0..5) {
                        val treeX = (i - 3) * 120f + (i * 37 % 60 - 30)
                        val treeY = treeBaseY + (i % 3) * 15f
                        // Tree scale is relative to world now
                        drawIsoTree(treeX, treeY, 0.7f + (i % 3) * 0.15f, animationTime)
                    }

                    // ============ TERRAIN: Elevated isometric island ============
                    drawIsoTerrain(gridSize, tileW, tileH)

                    // ============ GRASS TUFTS ============
                    drawIsoGrassTufts(gridSize, tileW, tileH, animationTime)

                    // ============ STONES / PEBBLES ============
                    drawIsoStones(tileW, tileH)

                    // ============ PUDDLE ============
                    drawIsoPuddle(3.5f, 3f, tileW, tileH)

                    // ============ FENCES ============
                    drawIsoFences(gridSize, tileW, tileH)

                    // ============ STRUCTURES (sorted back-to-front for depth) ============
                    // Coop (back-right)
                    val coopIso = worldToIso(5.5f, 1f, tileW, tileH)
                    drawIsoCoop(coopIso.x, coopIso.y, tileW * 0.8f)

                    // Second smaller coop
                    val coop2Iso = worldToIso(6f, 4.5f, tileW, tileH)
                    drawIsoSmallCoop(coop2Iso.x, coop2Iso.y, tileW * 0.6f)

                    // Feeder
                    val feederIso = worldToIso(4f, 2.5f, tileW, tileH)
                    drawIsoFeeder(feederIso.x, feederIso.y, tileW * 0.4f)

                    // Water trough
                    val troughIso = worldToIso(2.5f, 4f, tileW, tileH)
                    drawIsoWaterTrough(troughIso.x, troughIso.y, tileW * 0.45f)

                    // Nest box
                    val nestIso = worldToIso(1f, 2f, tileW, tileH)
                    drawIsoNestBox(nestIso.x, nestIso.y, tileW * 0.35f)

                    // ============ BIRDS (sorted by depth for proper layering) ============
                    val sortedPlacements = birdPlacements.sortedBy { it.worldX + it.worldY }
                    
                    // Shadows first (always behind birds)
                    sortedPlacements.forEach { p ->
                        val iso = worldToIso(p.worldX, p.worldY, tileW, tileH)
                        val shadowScale = when (p.appearance.bodySize) {
                            BodySize.TINY -> 0.4f
                            BodySize.BANTAM -> 0.55f
                            BodySize.SMALL -> 0.75f
                            BodySize.MEDIUM -> 1f
                            BodySize.LARGE -> 1.2f
                            BodySize.XLARGE -> 1.4f
                        }
                        val shadowSize = 14f * shadowScale
                        val isHighlighted = !isSearchActive || highlightedBirdIds.contains(p.bird.productId)
                        val shadowAlpha = if (isHighlighted) 1f else 0.2f
                        drawOval(
                            color = IsoColors.birdShadow.copy(alpha = shadowAlpha),
                            topLeft = Offset(iso.x - shadowSize, iso.y + shadowSize * 0.3f),
                            size = Size(shadowSize * 2f, shadowSize * 0.8f)
                        )
                    }

                    // Birds rendered via BirdPartRenderer (BGMI-style part system)
                    sortedPlacements.forEach { p ->
                        val iso = worldToIso(p.worldX, p.worldY, tileW, tileH)
                        val isSelected = p.bird.productId == selectedBirdId
                        val isHighlighted = !isSearchActive || highlightedBirdIds.contains(p.bird.productId)
                        val bobOffset = if (animationTime > 0) {
                            sin((animationTime * 3f + p.worldX * 7f + p.worldY * 11f).toDouble()).toFloat() * 2f
                        } else 0f

                        // Dim non-matching birds during search
                        if (!isHighlighted) {
                            drawContext.canvas.saveLayer(
                                Rect(iso.x - 40f, iso.y - 60f, iso.x + 40f, iso.y + 20f),
                                Paint().apply { alpha = 0.25f }
                            )
                        }

                        // Glow ring for search-matched birds
                        if (isSearchActive && isHighlighted) {
                            val glowPulse = sin((animationTime * 5f).toDouble()).toFloat() * 0.3f + 0.7f
                            drawCircle(
                                color = Color(0xFF00E5FF).copy(alpha = glowPulse * 0.4f),
                                radius = 22f,
                                center = Offset(iso.x, iso.y - 10f)
                            )
                        }

                        drawBirdFromAppearance(
                            x = iso.x,
                            y = iso.y,
                            appearance = p.appearance,
                            isSelected = isSelected,
                            animTime = animationTime,
                            bobOffset = bobOffset
                        )

                        if (!isHighlighted) {
                            drawContext.canvas.restore()
                        }
                    }

                    // ============ BIRD CODE ID TAGS (zoom-dependent, > 1.8x) ============
                    if (scale > 1.8f) {
                        sortedPlacements.forEach { p ->
                            val iso = worldToIso(p.worldX, p.worldY, tileW, tileH)
                            val isHighlighted = !isSearchActive || highlightedBirdIds.contains(p.bird.productId)
                            if (isHighlighted) {
                                val idText = p.bird.birdCode ?: p.bird.name.take(8)
                                val idResult = textMeasurer.measure(
                                    AnnotatedString(idText),
                                    style = TextStyle(
                                        fontSize = (7f / scale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                val tagW = idResult.size.width + 8f
                                val tagH = idResult.size.height + 4f
                                val tagX = iso.x - tagW / 2f
                                val tagY = iso.y + 12f

                                // Tag background — dark pill
                                drawRoundRect(
                                    color = Color(0xCC1B5E20),
                                    topLeft = Offset(tagX, tagY),
                                    size = Size(tagW, tagH),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                                drawText(
                                    idResult,
                                    topLeft = Offset(tagX + 4f, tagY + 2f)
                                )
                            }
                        }
                    }

                    // ============ ZONE LABELS ============
                    drawZoneLabels(uiState, tileW, tileH, textMeasurer, scale)

                    // ============ BIRD INFO BUBBLES (zoom-dependent) ============
                    if (scale > 1.5f) {
                        birdPlacements.forEach { p ->
                            val iso = worldToIso(p.worldX, p.worldY, tileW, tileH)
                            drawBirdInfoBubble(
                                x = iso.x,
                                y = iso.y,
                                bird = p.bird,
                                isSelected = p.bird.productId == selectedBirdId,
                                textMeasurer = textMeasurer,
                                scale = scale
                            )
                        }
                    }

                    // ============ SELECTED BIRD HIGHLIGHT BUBBLE ============
                    if (selectedBirdId != null && scale <= 1.5f) {
                        birdPlacements.find { it.bird.productId == selectedBirdId }?.let { p ->
                            val iso = worldToIso(p.worldX, p.worldY, tileW, tileH)
                            drawBirdInfoBubble(
                                x = iso.x,
                                y = iso.y,
                                bird = p.bird,
                                isSelected = true,
                                textMeasurer = textMeasurer,
                                scale = scale
                            )
                        }
                    }
                }
            }

            // ============ ZOOM INDICATOR (Outside transform to stay fixed) ============
            val zoomPercent = ((scale / 1.2f) * 100).toInt()
            val zoomText = "${zoomPercent}%"
            val zoomResult = textMeasurer.measure(
                AnnotatedString(zoomText),
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            )
            val zoomBgRect = Rect(
                offset = Offset(w - zoomResult.size.width - 28f, h - 52f),
                size = Size(zoomResult.size.width + 20f, zoomResult.size.height + 10f)
            )
            drawRoundRect(
                color = Color(0x88000000),
                topLeft = zoomBgRect.topLeft,
                size = zoomBgRect.size,
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawText(
                zoomResult,
                topLeft = Offset(zoomBgRect.left + 10f, zoomBgRect.top + 5f)
            )
        }
    }
}

// ==================== DRAWING FUNCTIONS ====================

// --- Terrain ---

private fun DrawScope.drawIsoTerrain(gridSize: Int, tileW: Float, tileH: Float) {
    val depthHeight = 40f

    // Top face (grass)
    val topPath = Path().apply {
        moveTo(0f, -gridSize * tileH / 2f) // top
        lineTo(gridSize * tileW / 2f, 0f)   // right
        lineTo(0f, gridSize * tileH / 2f)   // bottom
        lineTo(-gridSize * tileW / 2f, 0f)  // left
        close()
    }
    drawPath(topPath, IsoColors.grassTop)

    // Add grass texture stripes
    for (i in 1 until gridSize) {
        val y1 = -gridSize * tileH / 2f + i * tileH
        val x1 = -i * tileW / 2f
        val x2 = (gridSize - i) * tileW / 2f
        drawLine(
            color = IsoColors.grassLight.copy(alpha = 0.3f),
            start = Offset(x1, y1 - tileH / 2f),
            end = Offset(x2, y1 + (gridSize - i) * tileH / 2f - i * tileH / 2f),
            strokeWidth = 1f
        )
    }

    // Right side face (dirt)
    val rightPath = Path().apply {
        moveTo(gridSize * tileW / 2f, 0f)           // top-right
        lineTo(0f, gridSize * tileH / 2f)            // bottom-center
        lineTo(0f, gridSize * tileH / 2f + depthHeight)  // bottom-center-depth
        lineTo(gridSize * tileW / 2f, depthHeight)    // right-depth
        close()
    }
    drawPath(rightPath, IsoColors.dirtSide)

    // Left side face (darker dirt)
    val leftPath = Path().apply {
        moveTo(-gridSize * tileW / 2f, 0f)            // top-left
        lineTo(0f, gridSize * tileH / 2f)              // bottom-center
        lineTo(0f, gridSize * tileH / 2f + depthHeight)    // bottom-center-depth
        lineTo(-gridSize * tileW / 2f, depthHeight)    // left-depth
        close()
    }
    drawPath(leftPath, IsoColors.dirtFront)

    // Bottom edge highlight
    drawLine(
        color = IsoColors.dirtDark,
        start = Offset(-gridSize * tileW / 2f, depthHeight),
        end = Offset(0f, gridSize * tileH / 2f + depthHeight),
        strokeWidth = 2f
    )
    drawLine(
        color = IsoColors.dirtDark,
        start = Offset(gridSize * tileW / 2f, depthHeight),
        end = Offset(0f, gridSize * tileH / 2f + depthHeight),
        strokeWidth = 2f
    )
}

// --- Grass Tufts ---

private fun DrawScope.drawIsoGrassTufts(gridSize: Int, tileW: Float, tileH: Float, animTime: Float) {
    val random = java.util.Random(123)
    for (i in 0..25) {
        val wx = random.nextFloat() * gridSize
        val wy = random.nextFloat() * gridSize
        val iso = worldToIso(wx, wy, tileW, tileH)
        val sway = sin((animTime * 2f + wx * 5f).toDouble()).toFloat() * 2f
        val grassH = 6f + random.nextFloat() * 8f

        // Multiple blades
        for (b in -1..1) {
            drawLine(
                color = if (random.nextBoolean()) IsoColors.grassDark else Color(0xFF558B2F),
                start = Offset(iso.x + b * 3f, iso.y),
                end = Offset(iso.x + b * 3f + sway, iso.y - grassH),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

// --- Stones ---

private fun DrawScope.drawIsoStones(tileW: Float, tileH: Float) {
    val stones = listOf(
        Pair(6.5f, 2f), Pair(1.8f, 5.5f), Pair(5f, 6.5f),
        Pair(7f, 3.5f), Pair(0.8f, 3f), Pair(3f, 7f)
    )
    val random = java.util.Random(456)
    stones.forEach { (wx, wy) ->
        val iso = worldToIso(wx, wy, tileW, tileH)
        val stoneSize = 4f + random.nextFloat() * 6f
        drawOval(
            color = IsoColors.stone,
            topLeft = Offset(iso.x - stoneSize, iso.y - stoneSize * 0.4f),
            size = Size(stoneSize * 2f, stoneSize * 0.8f)
        )
        drawOval(
            color = IsoColors.stoneDark,
            topLeft = Offset(iso.x - stoneSize + 1f, iso.y - stoneSize * 0.3f),
            size = Size(stoneSize * 1.4f, stoneSize * 0.5f)
        )
    }
}

// --- Puddle / Mud ---

private fun DrawScope.drawIsoPuddle(wx: Float, wy: Float, tileW: Float, tileH: Float) {
    val iso = worldToIso(wx, wy, tileW, tileH)
    // Mud area
    drawOval(
        color = IsoColors.mud.copy(alpha = 0.5f),
        topLeft = Offset(iso.x - 35f, iso.y - 12f),
        size = Size(70f, 24f)
    )
    // Water puddle
    drawOval(
        color = IsoColors.puddle,
        topLeft = Offset(iso.x - 25f, iso.y - 8f),
        size = Size(50f, 16f)
    )
    // Reflection highlight
    drawOval(
        color = Color.White.copy(alpha = 0.2f),
        topLeft = Offset(iso.x - 12f, iso.y - 5f),
        size = Size(18f, 5f)
    )
}

// --- Fences ---

private fun DrawScope.drawIsoFences(gridSize: Int, tileW: Float, tileH: Float) {
    val fencePoints = listOf(
        // Back-left edge
        Pair(Pair(0f, 0f), Pair(gridSize.toFloat(), 0f)),
        // Back-right edge
        Pair(Pair(gridSize.toFloat(), 0f), Pair(gridSize.toFloat(), gridSize.toFloat())),
        // Front-right edge
        Pair(Pair(gridSize.toFloat(), gridSize.toFloat()), Pair(0f, gridSize.toFloat())),
        // Front-left edge
        Pair(Pair(0f, gridSize.toFloat()), Pair(0f, 0f))
    )

    fencePoints.forEach { (start, end) ->
        val segments = 8
        for (i in 0 until segments) {
            val t1 = i.toFloat() / segments
            val t2 = (i + 1).toFloat() / segments
            val wx1 = start.first + (end.first - start.first) * t1
            val wy1 = start.second + (end.second - start.second) * t1
            val wx2 = start.first + (end.first - start.first) * t2
            val wy2 = start.second + (end.second - start.second) * t2

            val iso1 = worldToIso(wx1, wy1, tileW, tileH)
            val iso2 = worldToIso(wx2, wy2, tileW, tileH)

            // Fence post
            val postHeight = 18f
            drawLine(
                color = IsoColors.fencePost,
                start = Offset(iso1.x, iso1.y),
                end = Offset(iso1.x, iso1.y - postHeight),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Top rail
            drawLine(
                color = IsoColors.fenceLight,
                start = Offset(iso1.x, iso1.y - postHeight),
                end = Offset(iso2.x, iso2.y - postHeight),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )

            // Bottom rail
            drawLine(
                color = IsoColors.fenceDark,
                start = Offset(iso1.x, iso1.y - postHeight * 0.5f),
                end = Offset(iso2.x, iso2.y - postHeight * 0.5f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

// --- Coop (Main) ---

private fun DrawScope.drawIsoCoop(x: Float, y: Float, size: Float) {
    val w = size
    val h = size * 0.7f
    val roofH = size * 0.5f

    // Shadow
    drawOval(
        color = IsoColors.birdShadow,
        topLeft = Offset(x - w * 0.6f, y + h * 0.1f),
        size = Size(w * 1.2f, h * 0.3f)
    )

    // Left wall
    val leftWall = Path().apply {
        moveTo(x - w * 0.5f, y)
        lineTo(x - w * 0.5f, y - h)
        lineTo(x, y - h - roofH * 0.3f)
        lineTo(x, y - h * 0.15f)
        close()
    }
    drawPath(leftWall, IsoColors.coopWall)

    // Right wall
    val rightWall = Path().apply {
        moveTo(x, y - h * 0.15f)
        lineTo(x, y - h - roofH * 0.3f)
        lineTo(x + w * 0.5f, y - h)
        lineTo(x + w * 0.5f, y)
        close()
    }
    drawPath(rightWall, IsoColors.coopWall.copy(alpha = 0.85f))

    // Door
    drawRoundRect(
        color = IsoColors.coopDoor,
        topLeft = Offset(x + w * 0.1f, y - h * 0.6f),
        size = Size(w * 0.22f, h * 0.45f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Window (left wall)
    drawRoundRect(
        color = IsoColors.coopWindow,
        topLeft = Offset(x - w * 0.35f, y - h * 0.7f),
        size = Size(w * 0.18f, w * 0.12f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Roof left slope
    val roofLeft = Path().apply {
        moveTo(x - w * 0.6f, y - h)
        lineTo(x, y - h - roofH)
        lineTo(x + w * 0.1f, y - h - roofH + roofH * 0.15f)
        lineTo(x - w * 0.5f, y - h + roofH * 0.15f)
        close()
    }
    drawPath(roofLeft, IsoColors.coopRoof)

    // Roof right slope
    val roofRight = Path().apply {
        moveTo(x, y - h - roofH)
        lineTo(x + w * 0.6f, y - h)
        lineTo(x + w * 0.5f, y - h + roofH * 0.15f)
        lineTo(x + w * 0.1f, y - h - roofH + roofH * 0.15f)
        close()
    }
    drawPath(roofRight, IsoColors.coopRoofLight)
}

// --- Small Coop ---

private fun DrawScope.drawIsoSmallCoop(x: Float, y: Float, size: Float) {
    val w = size * 0.8f
    val h = size * 0.55f

    // Shadow
    drawOval(
        color = IsoColors.birdShadow,
        topLeft = Offset(x - w * 0.5f, y + 2f),
        size = Size(w, h * 0.25f)
    )

    // Box body
    val body = Path().apply {
        moveTo(x - w * 0.4f, y)
        lineTo(x - w * 0.4f, y - h)
        lineTo(x + w * 0.4f, y - h)
        lineTo(x + w * 0.4f, y)
        close()
    }
    drawPath(body, IsoColors.coopWall)

    // Roof
    val roof = Path().apply {
        moveTo(x - w * 0.5f, y - h)
        lineTo(x, y - h - size * 0.35f)
        lineTo(x + w * 0.5f, y - h)
        close()
    }
    drawPath(roof, IsoColors.coopRoof)

    // Door hole
    drawCircle(
        color = IsoColors.coopDoor,
        radius = w * 0.15f,
        center = Offset(x, y - h * 0.45f)
    )
}

// --- Feeder ---

private fun DrawScope.drawIsoFeeder(x: Float, y: Float, size: Float) {
    // Hopper (top container)
    val hopperPath = Path().apply {
        moveTo(x - size * 0.3f, y - size * 1.2f)
        lineTo(x + size * 0.3f, y - size * 1.2f)
        lineTo(x + size * 0.35f, y - size * 0.4f)
        lineTo(x - size * 0.35f, y - size * 0.4f)
        close()
    }
    drawPath(hopperPath, IsoColors.feederTop)

    // Lid
    drawRoundRect(
        color = IsoColors.feederBase,
        topLeft = Offset(x - size * 0.35f, y - size * 1.3f),
        size = Size(size * 0.7f, size * 0.12f),
        cornerRadius = CornerRadius(3f, 3f)
    )

    // Base tray
    drawOval(
        color = IsoColors.feederBase,
        topLeft = Offset(x - size * 0.45f, y - size * 0.5f),
        size = Size(size * 0.9f, size * 0.3f)
    )

    // Grain visible in tray
    drawOval(
        color = IsoColors.feedGrain,
        topLeft = Offset(x - size * 0.35f, y - size * 0.45f),
        size = Size(size * 0.7f, size * 0.2f)
    )

    // Stand legs
    drawLine(
        color = IsoColors.feederBase,
        start = Offset(x - size * 0.2f, y - size * 0.4f),
        end = Offset(x - size * 0.25f, y),
        strokeWidth = 2.5f
    )
    drawLine(
        color = IsoColors.feederBase,
        start = Offset(x + size * 0.2f, y - size * 0.4f),
        end = Offset(x + size * 0.25f, y),
        strokeWidth = 2.5f
    )
}

// --- Water Trough ---

private fun DrawScope.drawIsoWaterTrough(x: Float, y: Float, size: Float) {
    // Trough body (isometric rectangle)
    val bodyPath = Path().apply {
        moveTo(x - size * 0.5f, y - size * 0.2f)
        lineTo(x - size * 0.4f, y - size * 0.5f)
        lineTo(x + size * 0.4f, y - size * 0.5f)
        lineTo(x + size * 0.5f, y - size * 0.2f)
        close()
    }
    drawPath(bodyPath, IsoColors.troughWood)

    // Water surface
    val waterPath = Path().apply {
        moveTo(x - size * 0.38f, y - size * 0.45f)
        lineTo(x + size * 0.38f, y - size * 0.45f)
        lineTo(x + size * 0.45f, y - size * 0.25f)
        lineTo(x - size * 0.45f, y - size * 0.25f)
        close()
    }
    drawPath(waterPath, IsoColors.waterBlue)

    // Water highlight
    drawLine(
        color = Color.White.copy(alpha = 0.4f),
        start = Offset(x - size * 0.2f, y - size * 0.37f),
        end = Offset(x + size * 0.1f, y - size * 0.37f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )

    // Legs
    drawLine(
        color = IsoColors.fenceDark,
        start = Offset(x - size * 0.4f, y - size * 0.2f),
        end = Offset(x - size * 0.4f, y),
        strokeWidth = 3f
    )
    drawLine(
        color = IsoColors.fenceDark,
        start = Offset(x + size * 0.4f, y - size * 0.2f),
        end = Offset(x + size * 0.4f, y),
        strokeWidth = 3f
    )
}

// --- Nest Box ---

private fun DrawScope.drawIsoNestBox(x: Float, y: Float, size: Float) {
    // Box
    drawRoundRect(
        color = IsoColors.fenceDark,
        topLeft = Offset(x - size * 0.4f, y - size * 0.6f),
        size = Size(size * 0.8f, size * 0.6f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    // Straw inside
    drawOval(
        color = Color(0xFFFFCC80),
        topLeft = Offset(x - size * 0.3f, y - size * 0.55f),
        size = Size(size * 0.6f, size * 0.25f)
    )
    // Egg
    drawOval(
        color = Color(0xFFFFF8E1),
        topLeft = Offset(x - size * 0.1f, y - size * 0.48f),
        size = Size(size * 0.18f, size * 0.22f)
    )
}

// Bird rendering is now handled by BirdPartRenderer (BGMI-style part system)
// See BirdPartRenderer.kt and BirdAppearance.kt for the full part-based rendering engine

// --- Info Bubble (speech bubble with age/breed/weight) ---

private fun DrawScope.drawBirdInfoBubble(
    x: Float,
    y: Float,
    bird: VisualBird,
    isSelected: Boolean,
    textMeasurer: TextMeasurer,
    scale: Float
) {
    val bubbleY = y - 55f
    val nameText = bird.name.take(12)
    val ageText = "📅 ${bird.ageText}"
    val breedText = bird.breed?.let { "🏷️ $it" } ?: ""
    val weightText = bird.weightText?.let { "⚖️ $it" } ?: ""

    val nameResult = textMeasurer.measure(
        AnnotatedString(nameText),
        style = TextStyle(
            fontSize = (9f / scale).sp,
            fontWeight = FontWeight.Bold,
            color = IsoColors.bubbleText
        )
    )
    val ageResult = textMeasurer.measure(
        AnnotatedString(ageText),
        style = TextStyle(
            fontSize = (8f / scale).sp,
            fontWeight = FontWeight.Normal,
            color = IsoColors.bubbleAge
        )
    )
    val breedResult = if (breedText.isNotEmpty()) textMeasurer.measure(
        AnnotatedString(breedText),
        style = TextStyle(
            fontSize = (8f / scale).sp,
            fontWeight = FontWeight.Normal,
            color = IsoColors.bubbleBreed
        )
    ) else null
    val weightResult = if (weightText.isNotEmpty()) textMeasurer.measure(
        AnnotatedString(weightText),
        style = TextStyle(
            fontSize = (8f / scale).sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFF9800)
        )
    ) else null

    val allWidths = listOfNotNull(
        nameResult.size.width,
        ageResult.size.width,
        breedResult?.size?.width,
        weightResult?.size?.width
    )
    val bubbleW = (allWidths.maxOrNull() ?: 60) + 18f
    val lineH = nameResult.size.height
    val lineCount = 2 + (if (breedResult != null) 1 else 0) + (if (weightResult != null) 1 else 0)
    val bubbleH = lineH * lineCount + 12f

    // Bubble background with glassmorphism
    drawRoundRect(
        color = IsoColors.bubbleBg,
        topLeft = Offset(x - bubbleW / 2f, bubbleY - bubbleH),
        size = Size(bubbleW, bubbleH),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // Bubble outline
    drawRoundRect(
        color = if (isSelected) IsoColors.birdSelected else Color(0x30000000),
        topLeft = Offset(x - bubbleW / 2f, bubbleY - bubbleH),
        size = Size(bubbleW, bubbleH),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = if (isSelected) 2f else 1f)
    )

    // Pointer triangle
    val pointer = Path().apply {
        moveTo(x - 6f, bubbleY - 1f)
        lineTo(x, bubbleY + 7f)
        lineTo(x + 6f, bubbleY - 1f)
        close()
    }
    drawPath(pointer, IsoColors.bubbleBg)

    // Draw text lines
    var currentY = bubbleY - bubbleH + 5f
    val leftX = x - bubbleW / 2f + 9f

    drawText(nameResult, topLeft = Offset(leftX, currentY))
    currentY += lineH

    drawText(ageResult, topLeft = Offset(leftX, currentY))
    currentY += lineH

    breedResult?.let {
        drawText(it, topLeft = Offset(leftX, currentY))
        currentY += lineH
    }

    weightResult?.let {
        drawText(it, topLeft = Offset(leftX, currentY))
    }
}

// --- Cloud ---

private fun DrawScope.drawIsoCloud(x: Float, y: Float, s: Float) {
    drawOval(
        color = IsoColors.cloudWhite,
        topLeft = Offset(x - 40f * s, y - 12f * s),
        size = Size(80f * s, 24f * s)
    )
    drawOval(
        color = IsoColors.cloudWhite,
        topLeft = Offset(x - 25f * s, y - 20f * s),
        size = Size(50f * s, 28f * s)
    )
    drawOval(
        color = IsoColors.cloudWhite,
        topLeft = Offset(x + 5f * s, y - 16f * s),
        size = Size(45f * s, 22f * s)
    )
}

// --- Tree ---

private fun DrawScope.drawIsoTree(x: Float, y: Float, s: Float, animTime: Float) {
    val sway = sin((animTime * 1.5f + x * 0.01f).toDouble()).toFloat() * 2f * s

    // Trunk
    drawLine(
        color = IsoColors.treeTrunk,
        start = Offset(x, y),
        end = Offset(x + sway * 0.3f, y - 30f * s),
        strokeWidth = 6f * s,
        cap = StrokeCap.Round
    )

    // Foliage layers (low-poly look: triangles)
    val foliage1 = Path().apply {
        moveTo(x - 22f * s + sway, y - 20f * s)
        lineTo(x + sway * 0.5f, y - 55f * s)
        lineTo(x + 22f * s + sway, y - 20f * s)
        close()
    }
    drawPath(foliage1, IsoColors.treeGreen)

    val foliage2 = Path().apply {
        moveTo(x - 18f * s + sway, y - 32f * s)
        lineTo(x + sway * 0.5f, y - 62f * s)
        lineTo(x + 18f * s + sway, y - 32f * s)
        close()
    }
    drawPath(foliage2, IsoColors.treeDark)

    val foliage3 = Path().apply {
        moveTo(x - 12f * s + sway, y - 45f * s)
        lineTo(x + sway * 0.5f, y - 68f * s)
        lineTo(x + 12f * s + sway, y - 45f * s)
        close()
    }
    drawPath(foliage3, IsoColors.treeGreen.copy(alpha = 0.9f))
}

// ==================== ZONE LABELS ====================

/**
 * Draw zone labels with bird counts on the isometric farm.
 * Each zone gets a distinct colored banner with emoji + name + count.
 */
private fun DrawScope.drawZoneLabels(
    state: DigitalFarmState,
    tileW: Float,
    tileH: Float,
    textMeasurer: TextMeasurer,
    scale: Float
) {
    // Zone label data: (zone, worldX, worldY, emoji, name, color)
    data class ZoneLabel(
        val zone: DigitalFarmZone,
        val wx: Float, val wy: Float,
        val emoji: String, val name: String,
        val bgColor: Color
    )

    val labels = listOf(
        ZoneLabel(DigitalFarmZone.NURSERY, 1.5f, 0.2f, "🐣", "Nursery", Color(0xCC4CAF50)),
        ZoneLabel(DigitalFarmZone.MAIN_COOP, 4f, 0.2f, "🏠", "Main Coop", Color(0xCC795548)),
        ZoneLabel(DigitalFarmZone.BREEDING_UNIT, 6.5f, 0.2f, "💕", "Breeding", Color(0xCCE91E63)),
        ZoneLabel(DigitalFarmZone.FREE_RANGE, 2f, 3.5f, "🌿", "Free Range", Color(0xCC43A047)),
        ZoneLabel(DigitalFarmZone.QUARANTINE, 6.5f, 3.5f, "⚠️", "Quarantine", Color(0xCCFF5722)),
        ZoneLabel(DigitalFarmZone.GROW_OUT, 2f, 6f, "📈", "Grow Out", Color(0xCC1565C0)),
        ZoneLabel(DigitalFarmZone.MARKET_STAND, 6f, 6f, "🏪", "Market", Color(0xCCFF9800)),
        ZoneLabel(DigitalFarmZone.READY_DISPLAY, 6.5f, 1.5f, "⭐", "Ready", Color(0xCCFFC107))
    )

    labels.forEach { label ->
        val count = state.countForZone(label.zone)
        if (count > 0 || label.zone == DigitalFarmZone.FREE_RANGE) {
            val iso = worldToIso(label.wx, label.wy, tileW, tileH)
            val labelText = "${label.emoji} ${label.name} ($count)"
            val result = textMeasurer.measure(
                AnnotatedString(labelText),
                style = TextStyle(
                    fontSize = (9f / scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            val padH = 6f
            val padW = 10f
            val bgW = result.size.width + padW * 2
            val bgH = result.size.height + padH * 2
            val bgX = iso.x - bgW / 2f
            val bgY = iso.y - bgH / 2f

            // Label background pill
            drawRoundRect(
                color = label.bgColor,
                topLeft = Offset(bgX, bgY),
                size = Size(bgW, bgH),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Label border
            drawRoundRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(bgX, bgY),
                size = Size(bgW, bgH),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 1f)
            )
            // Label text
            drawText(
                result,
                topLeft = Offset(bgX + padW, bgY + padH)
            )
        }
    }
}
