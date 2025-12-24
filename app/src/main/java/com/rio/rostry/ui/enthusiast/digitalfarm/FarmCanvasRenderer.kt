package com.rio.rostry.ui.enthusiast.digitalfarm

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.rio.rostry.domain.model.*
import kotlin.math.*

/**
 * Farm Canvas Renderer - Handles all drawing operations for the Digital Farm.
 * 
 * Optimized for performance:
 * - Pre-computed positions
 * - Reusable Path objects
 * - Batched drawing operations
 * - Minimal object allocation during draw
 */
object FarmCanvasRenderer {

    // Reusable paths to avoid allocation in draw loop
    private val roofPath = Path()
    private val starPath = Path()
    private val beakPath = Path()
    private val grassPath = Path()
    private val cloudPath = Path()
    private val treePath = Path()

    // Color palette (pre-allocated)
    object FarmColors {
        val skyBlue = Color(0xFF87CEEB)
        val skyGradientTop = Color(0xFF1E90FF)
        val grassLight = Color(0xFF90EE90)
        val grassDark = Color(0xFF228B22)
        val grassMedium = Color(0xFF7CFC00)
        val dirt = Color(0xFF8B4513)
        val dirtLight = Color(0xFFD2B48C)
        val fenceWood = Color(0xFFDEB887)
        val fencePost = Color(0xFF8B4513)
        val hutYellow = Color(0xFFFFD700)
        val hutOrange = Color(0xFFFF8C00)
        val roofRed = Color(0xFFDC143C)
        val roofMaroon = Color(0xFF800000)
        val doorBrown = Color(0xFF5D4037)
        val birdWhite = Color(0xFFFFFAFA)
        val birdBrown = Color(0xFFD2691E)
        val birdOrange = Color(0xFFFFA500)
        val chickYellow = Color(0xFFFFEB3B)
        val combRed = Color(0xFFDC143C)
        val beakOrange = Color(0xFFFFA500)
        val legOrange = Color(0xFFE65100)
        val goldStar = Color(0xFFFFD700)
        val goldStarHighlight = Color(0xFFFFF8DC)
        val eggWhite = Color(0xFFFFFAF0)
        val eggBrown = Color(0xFFD2B48C)
        val awningRed = Color(0xFFDC143C)
        val awningWhite = Color(0xFFFFFAFA)
        val cloudWhite = Color(0xFFFFFFFF)
        val shadow = Color(0x40000000)
        val bubbleBlue = Color(0xFF4FC3F7)
        val statusVaccineDue = Color(0xFFE53935)
        val statusWeightReady = Color(0xFFFFD700)
        val statusSick = Color(0xFFFF5722)
        val treeGreen = Color(0xFF2E7D32)
        val treeTrunk = Color(0xFF795548)
    }

    /**
     * Main rendering function - draws the complete farm
     */
    fun DrawScope.renderFarm(
        state: DigitalFarmState,
        animationTime: Float = 0f,  // For idle animations
        selectedBirdId: String? = null
    ) {
        val width = size.width
        val height = size.height

        // Layer 0: Sky with gradient
        drawSky(width, height)

        // Layer 1: Clouds (animated)
        drawClouds(width, height, animationTime)

        // Layer 2: Background trees
        drawBackgroundTrees(width, height)

        // Layer 3: Ground with gradient
        drawGround(width, height)

        // Layer 4: Shadows (for 2.5D depth)
        drawShadows(state, width, height)

        // Layer 5: Fences (back)
        drawFencesBack(width, height)

        // Layer 6: Structures
        drawBreedingHuts(state.breedingUnits, width, height, animationTime)
        drawMarketStand(width, height, state.marketReady.size)

        // Layer 7: Birds (sorted by Y for proper overlap)
        val allBirds = collectAllBirdsForRendering(state, width, height)
        val sortedBirds = allBirds.sortedBy { it.renderY }
        sortedBirds.forEach { renderBird ->
            drawBirdSprite(
                renderBird.x,
                renderBird.y,
                renderBird.bird,
                animationTime,
                isSelected = renderBird.bird.productId == selectedBirdId
            )
        }

        // Layer 8: Nurseries (special grouping)
        drawNurseries(state.nurseries, width, height, animationTime)

        // Layer 9: Fences (front)
        drawFencesFront(width, height)

        // Layer 10: Status indicators (on top)
        sortedBirds.forEach { renderBird ->
            drawStatusIndicator(renderBird.x, renderBird.y - 30f, renderBird.bird.statusIndicator)
        }
    }

    private data class RenderableBird(
        val x: Float,
        val y: Float,
        val renderY: Float, // For z-sorting
        val bird: VisualBird
    )

    private fun collectAllBirdsForRendering(
        state: DigitalFarmState,
        width: Float,
        height: Float
    ): List<RenderableBird> {
        val result = mutableListOf<RenderableBird>()

        // Free range birds
        state.freeRange.forEach { bird ->
            val x = bird.position.x * width
            val y = bird.position.y * height
            result.add(RenderableBird(x, y, y, bird))
        }

        // Grow out birds
        state.growOut.forEach { bird ->
            val x = bird.position.x * width
            val y = bird.position.y * height
            result.add(RenderableBird(x, y, y, bird))
        }

        // Ready display birds
        state.readyDisplay.forEach { bird ->
            val x = bird.position.x * width
            val y = bird.position.y * height
            result.add(RenderableBird(x, y, y, bird))
        }

        // Market birds
        state.marketReady.take(6).forEachIndexed { i, bird ->
            val x = width * 0.72f + (i % 3) * 25f
            val y = height * 0.73f + (i / 3) * 20f
            result.add(RenderableBird(x, y, y, bird))
        }

        return result
    }

    // ==================== SKY & ATMOSPHERE ====================

    private fun DrawScope.drawSky(width: Float, height: Float) {
        val gradient = Brush.verticalGradient(
            colors = listOf(FarmColors.skyGradientTop, FarmColors.skyBlue, FarmColors.grassLight),
            startY = 0f,
            endY = height * 0.5f
        )
        drawRect(brush = gradient, size = Size(width, height * 0.5f))
    }

    private fun DrawScope.drawClouds(width: Float, height: Float, animTime: Float) {
        // Parallax cloud movement
        val cloudOffset = (animTime * 10f) % (width * 1.5f)

        // Cloud 1 (large, slow)
        drawCloud(
            x = width * 0.2f + cloudOffset * 0.3f,
            y = height * 0.08f,
            scale = 1.2f
        )

        // Cloud 2 (small, fast)
        drawCloud(
            x = width * 0.6f + cloudOffset * 0.5f,
            y = height * 0.12f,
            scale = 0.8f
        )

        // Cloud 3 (medium)
        drawCloud(
            x = width * 0.9f + cloudOffset * 0.4f,
            y = height * 0.05f,
            scale = 1.0f
        )
    }

    private fun DrawScope.drawCloud(x: Float, y: Float, scale: Float) {
        val wrapX = x % (size.width * 1.5f) - size.width * 0.25f
        val baseRadius = 20f * scale

        // Cloud puffs (3 circles overlapping)
        drawCircle(FarmColors.cloudWhite, radius = baseRadius, center = Offset(wrapX, y))
        drawCircle(FarmColors.cloudWhite, radius = baseRadius * 1.3f, center = Offset(wrapX + baseRadius, y + 5f))
        drawCircle(FarmColors.cloudWhite, radius = baseRadius * 0.9f, center = Offset(wrapX + baseRadius * 2f, y))
        drawCircle(FarmColors.cloudWhite, radius = baseRadius * 0.7f, center = Offset(wrapX - baseRadius * 0.5f, y + 3f))
    }

    private fun DrawScope.drawBackgroundTrees(width: Float, height: Float) {
        // Row of trees at horizon line
        val treeY = height * 0.28f
        val treeCount = 6

        repeat(treeCount) { i ->
            val treeX = width * (0.05f + i * 0.18f)
            val treeScale = 0.6f + (i % 3) * 0.15f

            // Tree trunk
            drawRect(
                FarmColors.treeTrunk,
                topLeft = Offset(treeX - 4f * treeScale, treeY),
                size = Size(8f * treeScale, 25f * treeScale)
            )

            // Tree foliage (triangular)
            treePath.reset()
            treePath.moveTo(treeX, treeY - 35f * treeScale)
            treePath.lineTo(treeX - 18f * treeScale, treeY + 5f)
            treePath.lineTo(treeX + 18f * treeScale, treeY + 5f)
            treePath.close()
            drawPath(treePath, FarmColors.treeGreen)
        }
    }

    // ==================== GROUND ====================

    private fun DrawScope.drawGround(width: Float, height: Float) {
        // Main grass area with gradient
        val grassGradient = Brush.verticalGradient(
            colors = listOf(FarmColors.grassMedium, FarmColors.grassLight, FarmColors.dirtLight),
            startY = height * 0.3f,
            endY = height
        )
        drawRect(
            brush = grassGradient,
            topLeft = Offset(0f, height * 0.3f),
            size = Size(width, height * 0.7f)
        )

        // Dirt path in middle
        drawOval(
            color = FarmColors.dirtLight.copy(alpha = 0.5f),
            topLeft = Offset(width * 0.3f, height * 0.5f),
            size = Size(width * 0.4f, height * 0.3f)
        )

        // Grass tufts (scattered)
        repeat(30) { i ->
            val x = (i * width / 30) + (i * 17 % 40) - 20f
            val y = height * (0.35f + (i % 7) * 0.08f)
            if (x > 0 && x < width && y < height * 0.9f) {
                drawGrassTuft(x, y)
            }
        }
    }

    private fun DrawScope.drawGrassTuft(x: Float, y: Float) {
        grassPath.reset()
        grassPath.moveTo(x, y)
        grassPath.lineTo(x - 3f, y - 12f)
        grassPath.moveTo(x, y)
        grassPath.lineTo(x, y - 15f)
        grassPath.moveTo(x, y)
        grassPath.lineTo(x + 3f, y - 10f)
        drawPath(grassPath, FarmColors.grassDark, style = Stroke(width = 2f))
    }

    // ==================== SHADOWS ====================

    private fun DrawScope.drawShadows(state: DigitalFarmState, width: Float, height: Float) {
        // Hut shadows
        state.breedingUnits.forEachIndexed { i, _ ->
            val hutX = width * (0.15f + i * 0.12f)
            val hutY = height * 0.22f
            drawOval(
                FarmColors.shadow,
                topLeft = Offset(hutX - 35f, hutY + 45f),
                size = Size(70f, 15f)
            )
        }

        // Market stand shadow
        drawOval(
            FarmColors.shadow,
            topLeft = Offset(width * 0.72f - 45f, height * 0.77f + 35f),
            size = Size(90f, 20f)
        )
    }

    // ==================== FENCES ====================

    private fun DrawScope.drawFencesBack(width: Float, height: Float) {
        val fenceY = height * 0.38f

        // Back fence (horizontal)
        drawFenceSegment(
            startX = width * 0.08f,
            endX = width * 0.92f,
            y = fenceY
        )
    }

    private fun DrawScope.drawFencesFront(width: Float, height: Float) {
        val fenceY = height * 0.82f

        // Front fence (with gate gap)
        drawFenceSegment(
            startX = width * 0.08f,
            endX = width * 0.4f,
            y = fenceY
        )
        drawFenceSegment(
            startX = width * 0.55f,
            endX = width * 0.92f,
            y = fenceY
        )

        // Side fences
        drawVerticalFence(x = width * 0.08f, startY = height * 0.38f, endY = height * 0.82f)
        drawVerticalFence(x = width * 0.92f, startY = height * 0.38f, endY = height * 0.82f)
    }

    private fun DrawScope.drawFenceSegment(startX: Float, endX: Float, y: Float) {
        // Top rail
        drawLine(FarmColors.fenceWood, Offset(startX, y), Offset(endX, y), strokeWidth = 5f)
        // Bottom rail
        drawLine(FarmColors.fenceWood, Offset(startX, y + 15f), Offset(endX, y + 15f), strokeWidth = 4f)

        // Posts
        val postCount = ((endX - startX) / 50f).toInt().coerceAtLeast(2)
        repeat(postCount) { i ->
            val postX = startX + (i * (endX - startX) / (postCount - 1))
            drawRect(
                FarmColors.fencePost,
                topLeft = Offset(postX - 3f, y - 8f),
                size = Size(6f, 35f)
            )
        }
    }

    private fun DrawScope.drawVerticalFence(x: Float, startY: Float, endY: Float) {
        val segmentCount = ((endY - startY) / 50f).toInt().coerceAtLeast(2)

        repeat(segmentCount) { i ->
            val segY = startY + (i * (endY - startY) / segmentCount)
            // Horizontal rails
            drawLine(FarmColors.fenceWood, Offset(x - 15f, segY), Offset(x + 15f, segY), strokeWidth = 4f)
            // Post
            drawRect(
                FarmColors.fencePost,
                topLeft = Offset(x - 3f, segY - 5f),
                size = Size(6f, 25f)
            )
        }
    }

    // ==================== STRUCTURES ====================

    private fun DrawScope.drawBreedingHuts(
        units: List<BreedingUnit>,
        width: Float,
        height: Float,
        animTime: Float
    ) {
        units.forEachIndexed { index, unit ->
            val hutX = width * (0.15f + index * 0.12f)
            val hutY = height * 0.18f

            drawHut(hutX, hutY, index)

            // Draw ghost eggs if needed
            if (unit.showGhostEggs) {
                drawGhostEggIndicator(hutX, hutY + 50f, animTime)
            }

            // Draw egg count if logged today
            if (unit.eggsCollectedToday > 0) {
                drawEggCount(hutX + 25f, hutY - 10f, unit.eggsCollectedToday)
            }
        }
    }

    private fun DrawScope.drawHut(x: Float, y: Float, index: Int) {
        // Choose color variation based on index
        val wallColor = when (index % 3) {
            0 -> FarmColors.hutYellow
            1 -> FarmColors.hutOrange
            else -> Color(0xFFFFE4B5)
        }

        // Hut body with slight 3D effect
        drawRoundRect(
            color = wallColor.copy(alpha = 0.8f),
            topLeft = Offset(x - 28f, y + 3f),
            size = Size(56f, 45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )
        drawRoundRect(
            color = wallColor,
            topLeft = Offset(x - 30f, y),
            size = Size(60f, 45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
        )

        // Roof
        roofPath.reset()
        roofPath.moveTo(x - 40f, y)
        roofPath.lineTo(x, y - 28f)
        roofPath.lineTo(x + 40f, y)
        roofPath.close()
        drawPath(roofPath, FarmColors.roofRed)

        // Roof highlight
        roofPath.reset()
        roofPath.moveTo(x - 35f, y - 3f)
        roofPath.lineTo(x, y - 25f)
        roofPath.lineTo(x + 5f, y - 23f)
        roofPath.lineTo(x - 30f, y)
        roofPath.close()
        drawPath(roofPath, FarmColors.roofRed.copy(alpha = 0.7f))

        // Door
        drawRoundRect(
            color = FarmColors.doorBrown,
            topLeft = Offset(x - 8f, y + 18f),
            size = Size(16f, 27f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )

        // Door handle
        drawCircle(
            color = Color(0xFFBDBDBD),
            radius = 2f,
            center = Offset(x + 4f, y + 32f)
        )

        // Window
        drawRoundRect(
            color = Color(0xFF87CEEB).copy(alpha = 0.7f),
            topLeft = Offset(x + 15f, y + 8f),
            size = Size(12f, 12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
        )
    }

    private fun DrawScope.drawGhostEggIndicator(x: Float, y: Float, animTime: Float) {
        // Pulsing animation
        val pulse = (sin(animTime * 3f) * 0.15f + 0.85f).toFloat()
        val alpha = (sin(animTime * 2f) * 0.3f + 0.5f).toFloat()

        repeat(3) { i ->
            val eggX = x - 20f + (i * 18f)
            val eggY = y + sin(animTime * 2f + i).toFloat() * 3f

            // Egg shadow
            drawOval(
                FarmColors.shadow,
                topLeft = Offset(eggX - 1f, eggY + 16f * pulse),
                size = Size(12f * pulse, 4f)
            )

            // Egg
            drawOval(
                FarmColors.eggWhite.copy(alpha = alpha),
                topLeft = Offset(eggX, eggY),
                size = Size(10f * pulse, 15f * pulse)
            )
        }
    }

    private fun DrawScope.drawEggCount(x: Float, y: Float, count: Int) {
        // Badge background
        drawCircle(
            color = Color(0xFFFFC107),
            radius = 12f,
            center = Offset(x, y)
        )
        // Would need native text API for the count
    }

    private fun DrawScope.drawMarketStand(width: Float, height: Float, birdCount: Int) {
        val standX = width * 0.75f
        val standY = height * 0.73f

        // Table top
        drawRoundRect(
            color = FarmColors.fenceWood,
            topLeft = Offset(standX - 45f, standY - 5f),
            size = Size(90f, 12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
        )

        // Table legs
        drawRect(FarmColors.fencePost, Offset(standX - 40f, standY + 5f), Size(8f, 35f))
        drawRect(FarmColors.fencePost, Offset(standX + 32f, standY + 5f), Size(8f, 35f))

        // Awning frame
        val awningTop = standY - 45f
        roofPath.reset()
        roofPath.moveTo(standX - 55f, standY - 5f)
        roofPath.lineTo(standX - 50f, awningTop)
        roofPath.lineTo(standX + 50f, awningTop)
        roofPath.lineTo(standX + 55f, standY - 5f)
        roofPath.close()
        drawPath(roofPath, FarmColors.awningRed)

        // White stripes on awning
        repeat(6) { i ->
            if (i % 2 == 0) {
                val stripeX = standX - 45f + (i * 18f)
                drawRect(
                    FarmColors.awningWhite,
                    Offset(stripeX, awningTop + 2f),
                    Size(9f, standY - awningTop - 7f)
                )
            }
        }

        // Display items on table (eggs and coins)
        repeat(4) { i ->
            val itemX = standX - 30f + (i * 20f)
            val itemY = standY - 15f

            if (i % 2 == 0) {
                // Egg
                drawOval(
                    FarmColors.eggBrown,
                    topLeft = Offset(itemX, itemY),
                    size = Size(12f, 16f)
                )
            } else {
                // Coin
                drawCircle(
                    FarmColors.goldStar,
                    radius = 8f,
                    center = Offset(itemX + 6f, itemY + 8f)
                )
            }
        }

        // Bird count badge if any listed
        if (birdCount > 0) {
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = 14f,
                center = Offset(standX + 45f, standY - 40f)
            )
        }
    }

    // ==================== BIRDS ====================

    private fun DrawScope.drawBirdSprite(
        x: Float,
        y: Float,
        bird: VisualBird,
        animTime: Float,
        isSelected: Boolean
    ) {
        val birdSize = when (bird.zone) {
            DigitalFarmZone.NURSERY -> 15f // Chicks are small
            DigitalFarmZone.FREE_RANGE -> 22f
            DigitalFarmZone.GROW_OUT -> 28f
            DigitalFarmZone.READY_DISPLAY -> 35f
            DigitalFarmZone.MARKET_STAND -> 20f
            else -> 25f
        }

        val birdColor = when {
            bird.color?.lowercase()?.contains("white") == true -> FarmColors.birdWhite
            bird.color?.lowercase()?.contains("brown") == true -> FarmColors.birdBrown
            bird.color?.lowercase()?.contains("black") == true -> Color(0xFF333333)
            bird.color?.lowercase()?.contains("yellow") == true -> FarmColors.chickYellow
            else -> FarmColors.birdWhite
        }

        // Idle animation
        val bobY = sin(animTime * 2f + x.hashCode() * 0.01f).toFloat() * 2f
        val headBob = sin(animTime * 3f + y.hashCode() * 0.01f).toFloat() * 1f

        // Selection highlight
        if (isSelected) {
            drawCircle(
                color = FarmColors.bubbleBlue.copy(alpha = 0.4f),
                radius = birdSize * 1.5f,
                center = Offset(x, y + bobY)
            )
        }

        // Bird shadow
        drawOval(
            FarmColors.shadow,
            topLeft = Offset(x - birdSize * 0.4f, y + birdSize * 0.3f),
            size = Size(birdSize * 0.8f, birdSize * 0.2f)
        )

        // Body (oval)
        drawOval(
            color = birdColor,
            topLeft = Offset(x - birdSize * 0.5f, y - birdSize * 0.3f + bobY),
            size = Size(birdSize, birdSize * 0.65f)
        )

        // Tail feathers
        val tailBaseX = x - birdSize * 0.45f
        val tailBaseY = y - birdSize * 0.1f + bobY
        beakPath.reset()
        beakPath.moveTo(tailBaseX, tailBaseY)
        beakPath.lineTo(tailBaseX - birdSize * 0.3f, tailBaseY - birdSize * 0.15f)
        beakPath.lineTo(tailBaseX - birdSize * 0.25f, tailBaseY + birdSize * 0.1f)
        beakPath.close()
        drawPath(beakPath, birdColor.copy(alpha = 0.8f))

        // Head
        val headX = x + birdSize * 0.35f
        val headY = y - birdSize * 0.35f + bobY + headBob
        drawCircle(
            color = birdColor,
            radius = birdSize * 0.28f,
            center = Offset(headX, headY)
        )

        // Eye
        drawCircle(
            color = Color.Black,
            radius = birdSize * 0.06f,
            center = Offset(headX + birdSize * 0.12f, headY - birdSize * 0.05f)
        )

        // Comb (only for adults)
        if (birdSize > 20f) {
            drawComb(headX, headY - birdSize * 0.2f, birdSize * 0.15f)
        }

        // Beak
        beakPath.reset()
        beakPath.moveTo(headX + birdSize * 0.2f, headY)
        beakPath.lineTo(headX + birdSize * 0.4f, headY + birdSize * 0.05f)
        beakPath.lineTo(headX + birdSize * 0.2f, headY + birdSize * 0.1f)
        beakPath.close()
        drawPath(beakPath, FarmColors.beakOrange)

        // Wattle (red thing under beak)
        if (birdSize > 25f) {
            drawCircle(
                FarmColors.combRed,
                radius = birdSize * 0.08f,
                center = Offset(headX + birdSize * 0.15f, headY + birdSize * 0.18f)
            )
        }

        // Legs
        val legY = y + birdSize * 0.25f + bobY
        drawLine(
            FarmColors.legOrange,
            Offset(x - birdSize * 0.15f, legY),
            Offset(x - birdSize * 0.2f, legY + birdSize * 0.3f),
            strokeWidth = 2.5f
        )
        drawLine(
            FarmColors.legOrange,
            Offset(x + birdSize * 0.1f, legY),
            Offset(x + birdSize * 0.15f, legY + birdSize * 0.3f),
            strokeWidth = 2.5f
        )

        // Gold star for ready birds
        if (bird.zone == DigitalFarmZone.READY_DISPLAY) {
            drawGoldStarBadge(x, y - birdSize * 1.2f + bobY, animTime)
        }
    }

    private fun DrawScope.drawComb(x: Float, y: Float, size: Float) {
        // 3 bumps for the comb
        drawCircle(FarmColors.combRed, radius = size * 0.6f, center = Offset(x - size * 0.5f, y))
        drawCircle(FarmColors.combRed, radius = size * 0.8f, center = Offset(x, y - size * 0.3f))
        drawCircle(FarmColors.combRed, radius = size * 0.6f, center = Offset(x + size * 0.5f, y))
    }

    private fun DrawScope.drawGoldStarBadge(x: Float, y: Float, animTime: Float) {
        val starPulse = (sin(animTime * 4f) * 0.1f + 1f).toFloat()
        val starSize = 22f * starPulse

        // Glow effect
        drawCircle(
            color = FarmColors.goldStar.copy(alpha = 0.3f),
            radius = starSize * 1.3f,
            center = Offset(x, y)
        )

        // Star badge background
        drawRoundRect(
            color = FarmColors.goldStar,
            topLeft = Offset(x - starSize * 0.8f, y - starSize * 0.8f),
            size = Size(starSize * 1.6f, starSize * 1.6f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(starSize * 0.3f)
        )

        // Star shape
        starPath.reset()
        val points = 5
        val outerRadius = starSize * 0.5f
        val innerRadius = starSize * 0.2f
        repeat(points * 2) { i ->
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = (Math.PI / 2 - Math.PI * 2 * i / (points * 2)).toFloat()
            val px = x + cos(angle) * radius
            val py = y - sin(angle) * radius
            if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
        }
        starPath.close()
        drawPath(starPath, Color.White)

        // Sparkles
        repeat(4) { i ->
            val sparkleAngle = (animTime + i * PI / 2).toFloat()
            val sparkleDistance = starSize * 1.1f
            val sparkleX = x + cos(sparkleAngle) * sparkleDistance
            val sparkleY = y - sin(sparkleAngle) * sparkleDistance
            drawCircle(Color.White, radius = 2f, center = Offset(sparkleX, sparkleY))
        }
    }

    private fun DrawScope.drawStatusIndicator(x: Float, y: Float, status: BirdStatusIndicator) {
        when (status) {
            BirdStatusIndicator.VACCINE_DUE -> {
                // Red syringe icon (simplified)
                drawCircle(FarmColors.statusVaccineDue, radius = 10f, center = Offset(x, y))
                drawRect(Color.White, Offset(x - 2f, y - 6f), Size(4f, 12f))
            }
            BirdStatusIndicator.SICK -> {
                // Red cross
                drawCircle(FarmColors.statusSick, radius = 10f, center = Offset(x, y))
                drawRect(Color.White, Offset(x - 5f, y - 1.5f), Size(10f, 3f))
                drawRect(Color.White, Offset(x - 1.5f, y - 5f), Size(3f, 10f))
            }
            BirdStatusIndicator.WEIGHT_READY -> {
                // Handled by gold star in drawBirdSprite
            }
            BirdStatusIndicator.WEIGHT_WARNING -> {
                // Yellow warning triangle
                starPath.reset()
                starPath.moveTo(x, y - 8f)
                starPath.lineTo(x + 8f, y + 6f)
                starPath.lineTo(x - 8f, y + 6f)
                starPath.close()
                drawPath(starPath, Color(0xFFFFC107))
                drawRect(Color.Black, Offset(x - 1f, y - 3f), Size(2f, 5f))
                drawCircle(Color.Black, radius = 1f, center = Offset(x, y + 3f))
            }
            BirdStatusIndicator.NEW_ARRIVAL -> {
                // Sparkle effect
                repeat(4) { i ->
                    val angle = (i * PI / 2).toFloat()
                    drawLine(
                        Color(0xFF4FC3F7),
                        Offset(x, y),
                        Offset(x + cos(angle) * 8f, y - sin(angle) * 8f),
                        strokeWidth = 2f
                    )
                }
            }
            BirdStatusIndicator.NONE -> { /* No indicator */ }
        }
    }

    // ==================== NURSERIES ====================

    private fun DrawScope.drawNurseries(
        nurseries: List<NurseryGroup>,
        width: Float,
        height: Float,
        animTime: Float
    ) {
        nurseries.forEach { nursery ->
            val nestX = nursery.nestPosition.x * width
            val nestY = nursery.nestPosition.y * height

            // Nest (straw pile)
            drawOval(
                FarmColors.dirtLight,
                topLeft = Offset(nestX - 30f, nestY + 15f),
                size = Size(60f, 18f)
            )

            // Straw texture lines
            repeat(5) { i ->
                val strawX = nestX - 25f + i * 12f
                drawLine(
                    FarmColors.dirt,
                    Offset(strawX, nestY + 20f),
                    Offset(strawX + 5f, nestY + 28f),
                    strokeWidth = 1.5f
                )
            }

            // Mother hen (larger, sitting)
            val motherBob = sin(animTime * 1.5f).toFloat() * 2f
            drawOval(
                FarmColors.birdWhite,
                topLeft = Offset(nestX - 18f, nestY - 5f + motherBob),
                size = Size(36f, 28f)
            )

            // Mother head
            val headX = nestX + 12f
            val headY = nestY - 8f + motherBob
            drawCircle(FarmColors.birdWhite, radius = 10f, center = Offset(headX, headY))
            drawComb(headX, headY - 9f, 5f)

            // Mother eye
            drawCircle(Color.Black, radius = 2f, center = Offset(headX + 4f, headY - 2f))

            // Mother beak
            beakPath.reset()
            beakPath.moveTo(headX + 8f, headY)
            beakPath.lineTo(headX + 15f, headY + 2f)
            beakPath.lineTo(headX + 8f, headY + 4f)
            beakPath.close()
            drawPath(beakPath, FarmColors.beakOrange)

            // Chicks orbiting around mother
            nursery.chicks.forEachIndexed { i, chick ->
                val chickAngle = animTime * 0.5f + (i * 2 * PI / nursery.chicks.size).toFloat()
                val chickRadius = 35f
                val chickX = nestX + cos(chickAngle) * chickRadius
                val chickY = nestY + 5f + sin(chickAngle) * chickRadius * 0.4f // Elliptical orbit

                drawChick(chickX, chickY, animTime + i)
            }

            // Chick count badge
            if (nursery.chicks.size > 1) {
                drawCircle(
                    Color(0xFF4CAF50),
                    radius = 10f,
                    center = Offset(nestX + 30f, nestY - 15f)
                )
            }
        }
    }

    private fun DrawScope.drawChick(x: Float, y: Float, animSeed: Float) {
        val bobY = sin(animSeed * 3f).toFloat() * 2f

        // Body
        drawCircle(
            FarmColors.chickYellow,
            radius = 7f,
            center = Offset(x, y + bobY)
        )

        // Head
        drawCircle(
            FarmColors.chickYellow,
            radius = 5f,
            center = Offset(x + 5f, y - 4f + bobY)
        )

        // Eye
        drawCircle(
            Color.Black,
            radius = 1f,
            center = Offset(x + 6f, y - 5f + bobY)
        )

        // Beak
        drawCircle(
            FarmColors.beakOrange,
            radius = 1.5f,
            center = Offset(x + 9f, y - 3f + bobY)
        )

        // Tiny legs
        drawLine(
            FarmColors.legOrange,
            Offset(x - 2f, y + 6f + bobY),
            Offset(x - 3f, y + 10f + bobY),
            strokeWidth = 1f
        )
        drawLine(
            FarmColors.legOrange,
            Offset(x + 2f, y + 6f + bobY),
            Offset(x + 3f, y + 10f + bobY),
            strokeWidth = 1f
        )
    }

    // ==================== HIT TESTING ====================

    /**
     * Determine what was tapped at the given position
     */
    fun hitTest(
        tapX: Float,
        tapY: Float,
        canvasWidth: Float,
        canvasHeight: Float,
        state: DigitalFarmState
    ): HitTestResult {
        val normalizedX = tapX / canvasWidth
        val normalizedY = tapY / canvasHeight

        // Check breeding huts
        state.breedingUnits.forEachIndexed { index, unit ->
            val hutX = 0.15f + index * 0.12f
            val hutY = 0.18f
            if (abs(normalizedX - hutX) < 0.06f && abs(normalizedY - hutY) < 0.08f) {
                return HitTestResult.BreedingHutHit(unit)
            }
        }

        // Check market stand
        if (normalizedX in 0.65f..0.85f && normalizedY in 0.65f..0.85f) {
            return HitTestResult.MarketStandHit
        }

        // Check nurseries
        state.nurseries.forEach { nursery ->
            val nestX = nursery.nestPosition.x
            val nestY = nursery.nestPosition.y
            if (abs(normalizedX - nestX) < 0.06f && abs(normalizedY - nestY) < 0.06f) {
                return HitTestResult.NurseryHit(nursery)
            }
        }

        // Check individual birds (all zones)
        val hitRadius = 0.04f
        val allBirds = state.freeRange + state.growOut + state.readyDisplay + state.marketReady
        allBirds.forEach { bird ->
            if (abs(normalizedX - bird.position.x) < hitRadius &&
                abs(normalizedY - bird.position.y) < hitRadius
            ) {
                return HitTestResult.BirdHit(bird)
            }
        }

        return HitTestResult.Nothing
    }

    sealed class HitTestResult {
        data class BirdHit(val bird: VisualBird) : HitTestResult()
        data class NurseryHit(val nursery: NurseryGroup) : HitTestResult()
        data class BreedingHutHit(val unit: BreedingUnit) : HitTestResult()
        data object MarketStandHit : HitTestResult()
        data object Nothing : HitTestResult()
    }
}
