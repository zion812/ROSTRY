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
    
    // ==================== OPTIMIZATION UTILITIES ====================
    
    /**
     * View culling - check if element is visible in viewport
     * Reduces rendering of off-screen elements for performance
     */
    private fun shouldRenderElement(
        x: Float, y: Float, size: Float,
        viewportWidth: Float, viewportHeight: Float,
        scale: Float = 1f, 
        panX: Float = 0f, panY: Float = 0f
    ): Boolean {
        val margin = size * 2 // Extra margin for safety
        val adjustedX = x * scale + panX
        val adjustedY = y * scale + panY
        
        return adjustedX > -margin && 
               adjustedX < viewportWidth + margin &&
               adjustedY > -margin && 
               adjustedY < viewportHeight + margin
    }
    
    /**
     * Frame skip optimization for distant/small elements
     * Returns true if this frame should skip rendering this element
     */
    private fun frameShouldSkip(
        frameCount: Long,
        importance: Int = 1 // 1 = every frame, 2 = every 2nd, etc.
    ): Boolean {
        return frameCount % importance != 0L
    }
    
    /**
     * Bird batch optimization - groups nearby birds for efficient rendering
     */
    private data class BirdBatch(
        val centerX: Float,
        val centerY: Float,
        val birds: List<VisualBird>
    )

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

        // Time-based sky colors for day/night cycle
        val skyMorningTop = Color(0xFFFFE4B5)   // Warm peach/golden
        val skyMorningBottom = Color(0xFF87CEEB) // Light blue
        val skyAfternoonTop = Color(0xFF1E90FF) // Bright blue
        val skyAfternoonBottom = Color(0xFF87CEEB) // Sky blue
        val skyEveningTop = Color(0xFFFF6B6B)   // Warm sunset red
        val skyEveningBottom = Color(0xFFFF8C42) // Orange horizon
        val skyNightTop = Color(0xFF0D1B2A)     // Deep dark blue
        val skyNightBottom = Color(0xFF1B263B)  // Dark blue-gray
        
        // Weather overlay colors
        val rainOverlay = Color(0x20607D8B)     // Bluish tint for rain
        val cloudyOverlay = Color(0x30757575)   // Gray tint for overcast
    }

    /**
     * Linear interpolation between two colors for smooth age transitions
     */
    private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
        val clampedFraction = fraction.coerceIn(0f, 1f)
        return Color(
            red = start.red + (end.red - start.red) * clampedFraction,
            green = start.green + (end.green - start.green) * clampedFraction,
            blue = start.blue + (end.blue - start.blue) * clampedFraction,
            alpha = start.alpha + (end.alpha - start.alpha) * clampedFraction
        )
    }

    /**
     * Main rendering function - draws the complete farm
     * 
     * @param config Optional configuration for Lite mode optimization.
     *               When config flags are disabled, expensive effects are skipped.
     */
    fun DrawScope.renderFarm(
        state: DigitalFarmState,
        animationTime: Float = 0f,  // For idle animations
        selectedBirdId: String? = null,
        timeOfDay: TimeOfDay = TimeOfDay.fromCurrentTime(),
        weather: WeatherType = WeatherType.SUNNY,
        config: DigitalFarmConfig? = null  // Lite mode config
    ) {
        val width = size.width
        val height = size.height
        
        // Performance flags (default to premium if no config)
        val enableParticles = config?.enableParticles ?: true
        val enableDayNightCycle = config?.enableDayNightCycle ?: true
        val enableFlocking = config?.enableFlocking ?: true
        val groupingMode = config?.groupingMode ?: GroupingMode.INDIVIDUAL

        // Layer 0: Sky with gradient (time-based or static "Golden Hour")
        if (enableDayNightCycle) {
            drawSky(width, height, timeOfDay)
        } else {
            // Static "Golden Hour" lighting for Lite mode
            drawSky(width, height, TimeOfDay.AFTERNOON)
        }

        // Layer 1: Clouds (animated) - skip if particles disabled
        if (enableParticles) {
            drawClouds(width, height, animationTime)
        }
        
        // Layer 1.5: Overcast effect for cloudy/rainy weather
        if (enableParticles && (weather == WeatherType.CLOUDY || weather == WeatherType.RAINY)) {
            drawOvercastEffect(width, height, animationTime)
        }

        // Layer 2: Background trees
        drawBackgroundTrees(width, height)

        // Layer 3: Ground with gradient
        drawGround(width, height)

        // Layer 4: Shadows (for 2.5D depth)
        drawShadows(state, width, height)

        // Layer 5: Fences (back)
        drawFencesBack(width, height)
        
        // Layer 5.5: Wind effect on grass - skip if particles disabled
        if (enableParticles && (weather == WeatherType.WINDY || weather == WeatherType.RAINY)) {
            val windStrength = if (weather == WeatherType.RAINY) 0.7f else 1f
            drawWindEffect(width, height, animationTime, windStrength)
        }

        // Layer 6: Structures
        drawBreedingHuts(state.breedingUnits, width, height, if (enableParticles) animationTime else 0f)
        drawMarketStand(width, height, state.marketReady.size)

        // Layer 7: Birds - Lite mode uses batch avatars, Premium uses individual birds
        if (groupingMode == GroupingMode.BY_BATCH) {
            // Lite Mode: Render batch avatars
            drawBatchAvatars(state, width, height)
        } else {
            // Premium Mode: Render individual birds with flocking
            val effectiveAnimTime = if (enableFlocking) animationTime else 0f
            val allBirds = collectAllBirdsForRendering(state, width, height, effectiveAnimTime)
            val sortedBirds = allBirds.sortedBy { it.renderY }
            sortedBirds.forEach { renderBird ->
                drawBirdSprite(
                    renderBird.x,
                    renderBird.y,
                    renderBird.bird,
                    effectiveAnimTime,
                    isSelected = renderBird.bird.productId == selectedBirdId
                )
            }
            
            // Layer 10: Status indicators (on top) - only for individual mode
            sortedBirds.forEach { renderBird ->
                drawStatusIndicator(renderBird.x, renderBird.y - 30f, renderBird.bird.statusIndicator)
            }
        }

        // Layer 8: Nurseries (special grouping)
        drawNurseries(state.nurseries, width, height, if (enableParticles) animationTime else 0f)

        // Layer 9: Fences (front)
        drawFencesFront(width, height)
        
        // Layer 11: Rain effect (on top of everything) - skip if particles disabled
        if (enableParticles && weather == WeatherType.RAINY) {
            drawRainEffect(width, height, animationTime, intensity = 1f)
        }
    }
    
    /**
     * Render zone drop targets for drag-and-drop interaction
     * Call this when a bird is being dragged to show valid drop zones
     */
    fun DrawScope.renderZoneDropTargets(
        width: Float,
        height: Float,
        animationTime: Float,
        activeZone: DigitalFarmZone? = null
    ) {
        val zones = listOf(
            ZoneTarget(DigitalFarmZone.FREE_RANGE, 0.1f, 0.5f, 0.4f, 0.35f, Color(0xFF4CAF50)),
            ZoneTarget(DigitalFarmZone.GROW_OUT, 0.55f, 0.5f, 0.35f, 0.35f, Color(0xFF2196F3)),
            ZoneTarget(DigitalFarmZone.READY_DISPLAY, 0.4f, 0.3f, 0.2f, 0.15f, Color(0xFFFFD700)),
            ZoneTarget(DigitalFarmZone.MARKET_STAND, 0.7f, 0.7f, 0.2f, 0.2f, Color(0xFFFF5722))
        )
        
        val pulseAlpha = (0.3f + sin(animationTime * 4f) * 0.15f).coerceIn(0.1f, 0.5f)
        
        zones.forEach { zone ->
            val isActive = zone.type == activeZone
            val alpha = if (isActive) pulseAlpha * 2f else pulseAlpha
            val strokeWidth = if (isActive) 4f else 2f
            
            // Zone highlight rectangle
            val left = zone.x * width
            val top = zone.y * height
            val zoneWidth = zone.w * width
            val zoneHeight = zone.h * height
            
            // Filled background
            drawRoundRect(
                color = zone.color.copy(alpha = alpha * 0.3f),
                topLeft = Offset(left, top),
                size = Size(zoneWidth, zoneHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f)
            )
            
            // Border
            drawRoundRect(
                color = zone.color.copy(alpha = alpha + 0.3f),
                topLeft = Offset(left, top),
                size = Size(zoneWidth, zoneHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f),
                style = Stroke(width = strokeWidth)
            )
            
            // Zone label indicator (small circle)
            if (isActive) {
                drawCircle(
                    color = zone.color,
                    radius = 8f,
                    center = Offset(left + zoneWidth / 2, top + 15f)
                )
            }
        }
    }
    
    private data class ZoneTarget(
        val type: DigitalFarmZone,
        val x: Float,
        val y: Float,
        val w: Float,
        val h: Float,
        val color: Color
    )

    private data class RenderableBird(
        val x: Float,
        val y: Float,
        val renderY: Float, // For z-sorting
        val bird: VisualBird
    )

    private fun collectAllBirdsForRendering(
        state: DigitalFarmState,
        width: Float,
        height: Float,
        animationTime: Float = 0f
    ): List<RenderableBird> {
        val result = mutableListOf<RenderableBird>()

        // Free range birds - with flocking behavior
        state.freeRange.forEachIndexed { index, bird ->
            val baseX = bird.position.x * width
            val baseY = bird.position.y * height
            
            // Flocking: birds move in subtle waves together
            val flockPhase = animationTime * 0.3f
            val individualOffset = bird.productId.hashCode() * 0.001f
            
            // Subtle wandering movement
            val wanderX = sin(flockPhase + individualOffset) * 8f
            val wanderY = cos(flockPhase * 0.7f + individualOffset) * 4f
            
            // Group cohesion - birds in same area move similarly
            val groupId = (index / 4) // Groups of 4
            val groupOffset = sin(flockPhase + groupId) * 5f
            
            val x = baseX + wanderX + groupOffset
            val y = baseY + wanderY
            result.add(RenderableBird(x, y, y, bird))
        }

        // Grow out birds - slower movement, less wandering
        state.growOut.forEachIndexed { index, bird ->
            val baseX = bird.position.x * width
            val baseY = bird.position.y * height
            
            // Slower, calmer movement
            val flockPhase = animationTime * 0.15f
            val individualOffset = bird.productId.hashCode() * 0.001f
            val wanderX = sin(flockPhase + individualOffset) * 4f
            val wanderY = cos(flockPhase * 0.5f + individualOffset) * 2f
            
            val x = baseX + wanderX
            val y = baseY + wanderY
            result.add(RenderableBird(x, y, y, bird))
        }

        // Ready display birds - stationary, proud stance
        state.readyDisplay.forEach { bird ->
            val x = bird.position.x * width
            val y = bird.position.y * height
            result.add(RenderableBird(x, y, y, bird))
        }

        // Market birds - tightly packed
        state.marketReady.take(6).forEachIndexed { i, bird ->
            val x = width * 0.72f + (i % 3) * 25f
            val y = height * 0.73f + (i / 3) * 20f
            result.add(RenderableBird(x, y, y, bird))
        }

        return result
    }
    
    // ==================== LITE MODE: BATCH AVATARS ====================
    
    /**
     * Draw batch avatars for Lite Mode (Farmer persona).
     * Renders one large avatar per batch with quantity badge and status halo.
     * Much more efficient than individual bird rendering.
     */
    private fun DrawScope.drawBatchAvatars(
        state: DigitalFarmState,
        width: Float,
        height: Float
    ) {
        // Group birds by zone and aggregate into batches
        data class BatchAggregate(
            val zone: DigitalFarmZone,
            val count: Int,
            val hasHealthAlert: Boolean,
            val hasVaccineDue: Boolean,
            val readyForSale: Int
        )
        
        // Aggregate free range
        val freeRangeBatch = BatchAggregate(
            zone = DigitalFarmZone.FREE_RANGE,
            count = state.freeRange.size,
            hasHealthAlert = state.freeRange.any { it.statusIndicator == BirdStatusIndicator.SICK },
            hasVaccineDue = state.freeRange.any { it.statusIndicator == BirdStatusIndicator.VACCINE_DUE },
            readyForSale = state.freeRange.count { it.isReadyForSale }
        )
        
        // Aggregate grow out
        val growOutBatch = BatchAggregate(
            zone = DigitalFarmZone.GROW_OUT,
            count = state.growOut.size,
            hasHealthAlert = state.growOut.any { it.statusIndicator == BirdStatusIndicator.SICK },
            hasVaccineDue = state.growOut.any { it.statusIndicator == BirdStatusIndicator.VACCINE_DUE },
            readyForSale = state.growOut.count { it.isReadyForSale }
        )
        
        // Aggregate ready display
        val readyBatch = BatchAggregate(
            zone = DigitalFarmZone.READY_DISPLAY,
            count = state.readyDisplay.size,
            hasHealthAlert = false,
            hasVaccineDue = false,
            readyForSale = state.readyDisplay.size
        )
        
        // Aggregate market
        val marketBatch = BatchAggregate(
            zone = DigitalFarmZone.MARKET_STAND,
            count = state.marketReady.size,
            hasHealthAlert = false,
            hasVaccineDue = false,
            readyForSale = state.marketReady.size
        )
        
        val batches = listOf(freeRangeBatch, growOutBatch, readyBatch, marketBatch)
            .filter { it.count > 0 }
        
        // Zone positions for avatars
        val zonePositions = mapOf(
            DigitalFarmZone.FREE_RANGE to Offset(width * 0.25f, height * 0.55f),
            DigitalFarmZone.GROW_OUT to Offset(width * 0.65f, height * 0.55f),
            DigitalFarmZone.READY_DISPLAY to Offset(width * 0.45f, height * 0.38f),
            DigitalFarmZone.MARKET_STAND to Offset(width * 0.78f, height * 0.75f)
        )
        
        batches.forEach { batch ->
            val pos = zonePositions[batch.zone] ?: return@forEach
            drawBatchAvatar(pos.x, pos.y, batch.count, batch.hasHealthAlert, batch.hasVaccineDue, batch.readyForSale > 0)
        }
    }
    
    /**
     * Draw a single batch avatar with status halo and quantity badge.
     */
    private fun DrawScope.drawBatchAvatar(
        x: Float, 
        y: Float, 
        count: Int,
        hasHealthAlert: Boolean,
        hasVaccineDue: Boolean,
        isReadyForSale: Boolean
    ) {
        val avatarSize = 50f
        
        // Status halo (behind avatar)
        val haloColor = when {
            hasHealthAlert -> FarmColors.statusSick
            hasVaccineDue -> FarmColors.statusVaccineDue
            isReadyForSale -> FarmColors.goldStar
            else -> Color(0xFF4CAF50) // Green = healthy
        }
        
        // Draw halo glow
        drawCircle(
            color = haloColor.copy(alpha = 0.3f),
            radius = avatarSize * 1.2f,
            center = Offset(x, y)
        )
        drawCircle(
            color = haloColor.copy(alpha = 0.6f),
            radius = avatarSize,
            center = Offset(x, y),
            style = Stroke(width = 4f)
        )
        
        // Draw large rooster avatar (simplified sprite)
        // Body
        drawCircle(
            color = FarmColors.birdWhite,
            radius = avatarSize * 0.6f,
            center = Offset(x, y)
        )
        
        // Head
        drawCircle(
            color = FarmColors.birdWhite,
            radius = avatarSize * 0.35f,
            center = Offset(x, y - avatarSize * 0.45f)
        )
        
        // Comb
        drawCircle(
            color = FarmColors.combRed,
            radius = avatarSize * 0.15f,
            center = Offset(x, y - avatarSize * 0.7f)
        )
        
        // Beak
        beakPath.reset()
        beakPath.moveTo(x + avatarSize * 0.2f, y - avatarSize * 0.4f)
        beakPath.lineTo(x + avatarSize * 0.45f, y - avatarSize * 0.35f)
        beakPath.lineTo(x + avatarSize * 0.2f, y - avatarSize * 0.3f)
        beakPath.close()
        drawPath(beakPath, FarmColors.beakOrange)
        
        // Eye
        drawCircle(
            color = Color.Black,
            radius = 3f,
            center = Offset(x + avatarSize * 0.08f, y - avatarSize * 0.45f)
        )
        
        // Quantity badge (top-right)
        val badgeX = x + avatarSize * 0.5f
        val badgeY = y - avatarSize * 0.5f
        
        // Badge background
        drawRoundRect(
            color = Color(0xFF1976D2),
            topLeft = Offset(badgeX - 18f, badgeY - 10f),
            size = Size(36f, 20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
        )
        
        // Draw "x{count}" text simulation with circles (crude but works without text)
        // For actual implementation, this would use drawContext.canvas.nativeCanvas.drawText
        // For now, we show circles proportional to magnitude
        val magnitude = when {
            count >= 100 -> 3
            count >= 50 -> 2
            count >= 10 -> 1
            else -> 0
        }
        repeat(magnitude + 1) { i ->
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(badgeX - 10f + i * 8f, badgeY)
            )
        }
    }

    // ==================== SKY & ATMOSPHERE ====================

    private fun DrawScope.drawSky(width: Float, height: Float, timeOfDay: TimeOfDay) {
        // Select sky colors based on time of day
        val (topColor, bottomColor) = when (timeOfDay) {
            TimeOfDay.MORNING -> FarmColors.skyMorningTop to FarmColors.skyMorningBottom
            TimeOfDay.AFTERNOON -> FarmColors.skyAfternoonTop to FarmColors.skyAfternoonBottom
            TimeOfDay.EVENING -> FarmColors.skyEveningTop to FarmColors.skyEveningBottom
            TimeOfDay.NIGHT -> FarmColors.skyNightTop to FarmColors.skyNightBottom
        }

        val gradient = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor, FarmColors.grassLight),
            startY = 0f,
            endY = height * 0.5f
        )
        drawRect(brush = gradient, size = Size(width, height * 0.5f))

        // Draw stars at night
        if (timeOfDay == TimeOfDay.NIGHT) {
            repeat(15) { i ->
                val starX = (i * 67 + 23) % width.toInt()
                val starY = (i * 37 + 11) % (height * 0.25f).toInt()
                drawCircle(
                    color = Color.White.copy(alpha = 0.6f),
                    radius = 1.5f,
                    center = Offset(starX.toFloat(), starY.toFloat())
                )
            }
        }

        // Draw sun or moon
        val celestialY = height * 0.1f
        when (timeOfDay) {
            TimeOfDay.MORNING, TimeOfDay.AFTERNOON -> {
                // Sun
                drawCircle(
                    color = FarmColors.goldStar,
                    radius = 20f,
                    center = Offset(width * 0.85f, celestialY)
                )
                // Sun glow
                drawCircle(
                    color = FarmColors.goldStar.copy(alpha = 0.3f),
                    radius = 30f,
                    center = Offset(width * 0.85f, celestialY)
                )
            }
            TimeOfDay.EVENING -> {
                // Setting sun (larger, more orange)
                drawCircle(
                    color = FarmColors.hutOrange,
                    radius = 25f,
                    center = Offset(width * 0.9f, height * 0.25f)
                )
            }
            TimeOfDay.NIGHT -> {
                // Moon
                drawCircle(
                    color = Color(0xFFF5F5DC),
                    radius = 15f,
                    center = Offset(width * 0.15f, celestialY)
                )
            }
        }
    }

    // ==================== WEATHER EFFECTS ====================

    /**
     * Rain effect - falling droplets with splash at ground level
     */
    private fun DrawScope.drawRainEffect(width: Float, height: Float, animTime: Float, intensity: Float = 1f) {
        val rainColor = Color(0x80A0C4FF) // Light blue with transparency
        val dropCount = (50 * intensity).toInt()
        
        // Rain droplets
        repeat(dropCount) { i ->
            val seed = i * 73 + 17
            val startX = ((seed * 37) % width.toInt()).toFloat()
            val cycleOffset = (animTime * 800f + seed * 23) % (height * 1.2f)
            val dropY = cycleOffset - height * 0.2f
            
            if (dropY in 0f..height) {
                // Falling droplet
                drawLine(
                    color = rainColor,
                    start = Offset(startX, dropY),
                    end = Offset(startX - 3f, dropY + 15f),
                    strokeWidth = 1.5f
                )
                
                // Splash at ground level
                if (dropY > height * 0.75f) {
                    val splashProgress = (dropY - height * 0.75f) / (height * 0.25f)
                    val splashRadius = 4f * splashProgress
                    drawCircle(
                        color = rainColor.copy(alpha = 0.4f * (1f - splashProgress)),
                        radius = splashRadius,
                        center = Offset(startX, height * 0.85f)
                    )
                }
            }
        }
    }

    /**
     * Wind effect - animates grass and tree sway
     */
    private fun DrawScope.drawWindEffect(width: Float, height: Float, animTime: Float, strength: Float = 1f) {
        val windOffset = sin(animTime * 2f) * 8f * strength
        
        // Wind-blown grass tufts
        repeat(20) { i ->
            val x = (i * width / 20) + (i * 17 % 40) - 20f
            val y = height * (0.75f + (i % 5) * 0.04f)
            val localWind = windOffset * (0.5f + (i % 3) * 0.25f)
            
            if (x > 0 && x < width) {
                grassPath.reset()
                grassPath.moveTo(x, y)
                grassPath.quadraticBezierTo(
                    x + localWind * 0.5f, y - 8f,
                    x + localWind, y - 15f
                )
                drawPath(grassPath, FarmColors.grassDark, style = Stroke(width = 2f))
                
                // Second blade
                grassPath.reset()
                grassPath.moveTo(x + 3f, y)
                grassPath.quadraticBezierTo(
                    x + 3f + localWind * 0.6f, y - 6f,
                    x + 3f + localWind * 1.2f, y - 12f
                )
                drawPath(grassPath, FarmColors.grassMedium, style = Stroke(width = 1.5f))
            }
        }
    }

    /**
     * Overcast overlay for cloudy weather
     */
    private fun DrawScope.drawOvercastEffect(width: Float, height: Float, animTime: Float) {
        // Dark cloud layer
        val cloudY = height * 0.15f
        repeat(8) { i ->
            val cloudX = (width * (0.1f + i * 0.12f) + animTime * 5f) % (width * 1.2f) - width * 0.1f
            val scale = 1.5f + (i % 3) * 0.3f
            
            drawCircle(
                Color(0x60607D8B),
                radius = 35f * scale,
                center = Offset(cloudX, cloudY + (i % 2) * 15f)
            )
            drawCircle(
                Color(0x50607D8B),
                radius = 28f * scale,
                center = Offset(cloudX + 25f, cloudY + 10f + (i % 2) * 10f)
            )
        }
        
        // Overall dimming overlay
        drawRect(
            color = FarmColors.cloudyOverlay,
            size = Size(width, height * 0.5f)
        )
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
            
            // PHASE 3: Enthusiast breeding visualization enhancements
            
            // Hens count badge
            if (unit.hensCount > 0) {
                drawHenCountBadge(hutX - 35f, hutY + 25f, unit.hensCount)
            }
            
            // Fertility/breeding success indicator
            val fertilityRate = if (unit.hensCount > 0) {
                (unit.eggsCollectedToday.toFloat() / unit.hensCount).coerceIn(0f, 1f)
            } else 0f
            
            if (fertilityRate > 0) {
                drawFertilityIndicator(hutX + 35f, hutY + 35f, fertilityRate, animTime)
            }
            
            // Genetic trait badge for premium breeding pairs
            if (unit.rooster != null) {
                drawGeneticTraitBadge(hutX, hutY - 35f, animTime)
            }
        }
    }
    
    /**
     * Hen count badge - shows number of hens in breeding unit
     */
    private fun DrawScope.drawHenCountBadge(x: Float, y: Float, count: Int) {
        // Badge background
        drawRoundRect(
            color = Color(0xFFE91E63).copy(alpha = 0.9f),
            topLeft = Offset(x - 10f, y - 8f),
            size = Size(20f, 16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
        )
        // Count text simulation (draw circle for each hen, max 4 shown)
        val displayCount = minOf(count, 4)
        repeat(displayCount) { i ->
            drawCircle(
                Color.White,
                radius = 2.5f,
                center = Offset(x - 5f + i * 5f, y)
            )
        }
    }
    
    /**
     * Fertility indicator - shows breeding success rate
     */
    private fun DrawScope.drawFertilityIndicator(x: Float, y: Float, rate: Float, animTime: Float) {
        val indicatorColor = when {
            rate > 0.7f -> Color(0xFF4CAF50) // Green - excellent
            rate > 0.4f -> Color(0xFFFFC107) // Yellow - good
            else -> Color(0xFFFF9800)        // Orange - fair
        }
        
        // Pulsing heart icon for fertility
        val pulse = 1f + sin(animTime * 3f) * 0.1f
        val size = 8f * pulse
        
        // Simple heart shape approximation
        drawCircle(indicatorColor, radius = size * 0.6f, center = Offset(x - 2f, y))
        drawCircle(indicatorColor, radius = size * 0.6f, center = Offset(x + 2f, y))
        beakPath.reset()
        beakPath.moveTo(x - 5f, y + 1f)
        beakPath.lineTo(x, y + 8f)
        beakPath.lineTo(x + 5f, y + 1f)
        beakPath.close()
        drawPath(beakPath, indicatorColor)
    }
    
    /**
     * Genetic trait badge - DNA helix icon for premium breeding
     */
    private fun DrawScope.drawGeneticTraitBadge(x: Float, y: Float, animTime: Float) {
        val rotation = animTime * 30f // Slow rotation
        
        // DNA helix icon
        drawCircle(
            Color(0xFF9C27B0).copy(alpha = 0.9f),
            radius = 12f,
            center = Offset(x, y)
        )
        
        // Helix strands (simplified)
        val strand1Y = sin(rotation * 0.05f) * 4f
        val strand2Y = sin((rotation + 180f) * 0.05f) * 4f
        
        drawLine(
            Color.White,
            Offset(x - 5f, y + strand1Y),
            Offset(x + 5f, y - strand1Y),
            strokeWidth = 2f
        )
        drawLine(
            Color.White.copy(alpha = 0.7f),
            Offset(x - 5f, y + strand2Y),
            Offset(x + 5f, y - strand2Y),
            strokeWidth = 2f
        )
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
        // Age-based size modifier (0-2 weeks: tiny, 2-6 weeks: small, 6-16 weeks: medium, 16+ weeks: full size)
        val ageModifier = when {
            bird.ageWeeks < 2 -> 0.5f  // Tiny chick
            bird.ageWeeks < 6 -> 0.65f // Small chick
            bird.ageWeeks < 12 -> 0.8f // Juvenile
            bird.ageWeeks < 20 -> 0.95f // Young adult
            else -> 1.0f // Full adult
        }
        
        val baseSize = when (bird.zone) {
            DigitalFarmZone.NURSERY -> 15f // Chicks are small
            DigitalFarmZone.FREE_RANGE -> 22f
            DigitalFarmZone.GROW_OUT -> 28f
            DigitalFarmZone.READY_DISPLAY -> 35f
            DigitalFarmZone.MARKET_STAND -> 20f
            else -> 25f
        }
        
        val birdSize = baseSize * ageModifier
        
        // Age-based color: chicks are yellow, juveniles are mottled, adults have full plumage
        val birdColor = when {
            bird.ageWeeks < 4 -> FarmColors.chickYellow // All chicks start yellow
            bird.ageWeeks < 8 -> {
                // Transitioning - blend between chick and adult color
                val progress = (bird.ageWeeks - 4) / 4f
                when {
                    bird.color?.lowercase()?.contains("white") == true -> 
                        lerpColor(FarmColors.chickYellow, FarmColors.birdWhite, progress)
                    bird.color?.lowercase()?.contains("brown") == true -> 
                        lerpColor(FarmColors.chickYellow, FarmColors.birdBrown, progress)
                    bird.color?.lowercase()?.contains("black") == true -> 
                        lerpColor(FarmColors.chickYellow, Color(0xFF333333), progress)
                    else -> lerpColor(FarmColors.chickYellow, FarmColors.birdWhite, progress)
                }
            }
            else -> when {
                bird.color?.lowercase()?.contains("white") == true -> FarmColors.birdWhite
                bird.color?.lowercase()?.contains("brown") == true -> FarmColors.birdBrown
                bird.color?.lowercase()?.contains("black") == true -> Color(0xFF333333)
                bird.color?.lowercase()?.contains("yellow") == true -> FarmColors.chickYellow
                else -> FarmColors.birdWhite
            }
        }

        // Idle animation - chicks bounce more
        val bounceMultiplier = if (bird.ageWeeks < 6) 1.5f else 1f
        val bobY = sin(animTime * 2f + x.hashCode() * 0.01f).toFloat() * 2f * bounceMultiplier
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
    
    // ==================== ACHIEVEMENT BADGES ====================
    
    /**
     * Farm achievement milestone badge
     * Used for gamification - shows achievement unlocks on the farm
     */
    fun DrawScope.drawAchievementBadge(
        x: Float,
        y: Float,
        achievementType: String,
        animTime: Float
    ) {
        val badgeColor = when (achievementType.lowercase()) {
            "gold", "champion", "master" -> Color(0xFFFFD700)
            "silver", "expert" -> Color(0xFFC0C0C0)
            "bronze", "starter" -> Color(0xFFCD7F32)
            else -> Color(0xFF9C27B0)
        }
        
        val pulseScale = 1f + sin(animTime * 3f) * 0.05f
        val size = 25f * pulseScale
        
        // Badge background circle
        drawCircle(
            color = badgeColor,
            radius = size,
            center = Offset(x, y)
        )
        
        // Inner ring
        drawCircle(
            color = badgeColor.copy(alpha = 0.6f),
            radius = size * 0.8f,
            center = Offset(x, y),
            style = Stroke(width = 2f)
        )
        
        // Trophy/star center
        starPath.reset()
        val starPoints = 5
        repeat(starPoints * 2) { i ->
            val radius = if (i % 2 == 0) size * 0.4f else size * 0.2f
            val angle = (i * 36f - 90f) * (PI / 180f)
            val sx = x + cos(angle).toFloat() * radius
            val sy = y + sin(angle).toFloat() * radius
            if (i == 0) starPath.moveTo(sx, sy) else starPath.lineTo(sx, sy)
        }
        starPath.close()
        drawPath(starPath, Color.White)
        
        // Ribbon tails
        beakPath.reset()
        beakPath.moveTo(x - size * 0.3f, y + size * 0.8f)
        beakPath.lineTo(x - size * 0.5f, y + size * 1.4f)
        beakPath.lineTo(x - size * 0.1f, y + size * 1.1f)
        beakPath.close()
        drawPath(beakPath, badgeColor.copy(alpha = 0.8f))
        
        beakPath.reset()
        beakPath.moveTo(x + size * 0.3f, y + size * 0.8f)
        beakPath.lineTo(x + size * 0.5f, y + size * 1.4f)
        beakPath.lineTo(x + size * 0.1f, y + size * 1.1f)
        beakPath.close()
        drawPath(beakPath, badgeColor.copy(alpha = 0.8f))
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
    
    // ==================== PHASE 6: PERFORMANCE CHARTS ====================
    
    /**
     * Mini line chart for egg production trends
     */
    fun DrawScope.drawMiniLineChart(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        data: List<Float>,
        color: Color = Color(0xFF4CAF50)
    ) {
        if (data.isEmpty()) return
        
        // Background
        drawRoundRect(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(x, y),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )
        
        val maxValue = data.maxOrNull() ?: 1f
        val stepX = width / (data.size - 1).coerceAtLeast(1)
        val padding = 4f
        
        // Draw line
        val linePath = Path()
        data.forEachIndexed { i, value ->
            val px = x + padding + i * stepX
            val py = y + height - padding - (value / maxValue) * (height - 2 * padding)
            if (i == 0) linePath.moveTo(px, py) else linePath.lineTo(px, py)
        }
        
        drawPath(linePath, color, style = Stroke(width = 2f))
        
        // Draw points
        data.forEachIndexed { i, value ->
            val px = x + padding + i * stepX
            val py = y + height - padding - (value / maxValue) * (height - 2 * padding)
            drawCircle(color, radius = 3f, center = Offset(px, py))
        }
    }
    
    /**
     * Mini bar chart for zone distribution
     */
    fun DrawScope.drawMiniBarChart(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        data: List<Float>,
        colors: List<Color> = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFFFFD700),
            Color(0xFFFF5722)
        )
    ) {
        if (data.isEmpty()) return
        
        // Background
        drawRoundRect(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(x, y),
            size = Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )
        
        val maxValue = data.maxOrNull() ?: 1f
        val barWidth = (width - 8f) / data.size
        val padding = 4f
        
        data.forEachIndexed { i, value ->
            val barHeight = (value / maxValue) * (height - 2 * padding)
            val barX = x + padding + i * barWidth
            val barY = y + height - padding - barHeight
            
            drawRoundRect(
                color = colors[i % colors.size],
                topLeft = Offset(barX, barY),
                size = Size(barWidth * 0.8f, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
            )
        }
    }
    
    /**
     * Mini pie chart for flock composition
     */
    fun DrawScope.drawMiniPieChart(
        x: Float,
        y: Float,
        radius: Float,
        data: List<Float>,
        colors: List<Color> = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFFFFD700),
            Color(0xFFFF5722),
            Color(0xFF9C27B0)
        )
    ) {
        if (data.isEmpty()) return
        
        val total = data.sum()
        if (total == 0f) return
        
        var startAngle = -90f
        
        data.forEachIndexed { i, value ->
            val sweepAngle = (value / total) * 360f
            
            drawArc(
                color = colors[i % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(x - radius, y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
        
        // Center hole for donut effect
        drawCircle(
            color = Color.White,
            radius = radius * 0.4f,
            center = Offset(x, y)
        )
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
     * @param useLiteMode If true, zone hits take priority and individual birds are not checked
     */
    fun hitTest(
        tapX: Float,
        tapY: Float,
        canvasWidth: Float,
        canvasHeight: Float,
        state: DigitalFarmState,
        useLiteMode: Boolean = false
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
        
        // LITE MODE: Check zones first (larger tap targets for batch avatars)
        if (useLiteMode) {
            // Free Range zone (left side)
            if (normalizedX in 0.05f..0.45f && normalizedY in 0.4f..0.7f) {
                return HitTestResult.ZoneHit(DigitalFarmZone.FREE_RANGE)
            }
            // Grow Out zone (right side)
            if (normalizedX in 0.5f..0.85f && normalizedY in 0.4f..0.7f) {
                return HitTestResult.ZoneHit(DigitalFarmZone.GROW_OUT)
            }
            // Ready Display zone (center-top)
            if (normalizedX in 0.3f..0.6f && normalizedY in 0.25f..0.45f) {
                return HitTestResult.ZoneHit(DigitalFarmZone.READY_DISPLAY)
            }
            // In Lite mode, don't check individual birds
            return HitTestResult.Nothing
        }

        // PREMIUM MODE: Check individual birds (all zones)
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
        data class ZoneHit(val zone: DigitalFarmZone) : HitTestResult()  // Lite mode zone tap
        data object MarketStandHit : HitTestResult()
        data object Nothing : HitTestResult()
    }
    
    // ==================== BUILDING PLACEMENT ====================
    
    /**
     * Draw a placeable building at specified position
     */
    fun DrawScope.drawPlaceableBuilding(
        building: PlaceableBuilding,
        width: Float,
        height: Float,
        isPlacementMode: Boolean = false,
        animTime: Float = 0f
    ) {
        val x = building.position.x * width
        val y = building.position.y * height
        
        // Placement mode glows
        if (isPlacementMode) {
            val glowAlpha = 0.3f + sin(animTime * 4f) * 0.1f
            drawCircle(
                Color(0xFF4CAF50).copy(alpha = glowAlpha),
                radius = 40f,
                center = Offset(x, y)
            )
        }
        
        when (building.type) {
            FarmBuildingType.COOP -> drawCoop(x, y, building.level)
            FarmBuildingType.BROODER -> drawBrooder(x, y)
            FarmBuildingType.WATER_FOUNTAIN -> drawWaterFountain(x, y, animTime)
            FarmBuildingType.FEEDER -> drawFeeder(x, y)
            FarmBuildingType.NESTING_BOX -> drawNestingBox(x, y)
            FarmBuildingType.PERCH -> drawPerch(x, y)
            FarmBuildingType.DUST_BATH -> drawDustBath(x, y)
            FarmBuildingType.SHADE_SHELTER -> drawShadeShelter(x, y)
        }
    }
    
    private fun DrawScope.drawCoop(x: Float, y: Float, level: Int) {
        val scale = 1f + (level - 1) * 0.15f
        val size = 40f * scale
        
        // Base
        drawRoundRect(
            FarmColors.hutYellow,
            Offset(x - size/2, y - size/3),
            Size(size, size * 0.7f),
            androidx.compose.ui.geometry.CornerRadius(4f)
        )
        // Roof
        roofPath.reset()
        roofPath.moveTo(x - size * 0.6f, y - size/3)
        roofPath.lineTo(x, y - size * 0.7f)
        roofPath.lineTo(x + size * 0.6f, y - size/3)
        roofPath.close()
        drawPath(roofPath, FarmColors.roofRed)
        
        // Level indicator
        if (level > 1) {
            drawCircle(Color(0xFFFFD700), 8f, Offset(x + size/2 - 5f, y - size/3 - 5f))
        }
    }
    
    private fun DrawScope.drawBrooder(x: Float, y: Float) {
        // Warm lamp
        drawCircle(Color(0xFFFF6B35).copy(alpha = 0.3f), 35f, Offset(x, y - 10f))
        drawCircle(Color(0xFFFFAA00), 12f, Offset(x, y - 25f))
        // Box
        drawRoundRect(Color(0xFF8B4513), Offset(x - 25f, y - 5f), Size(50f, 30f), 
            androidx.compose.ui.geometry.CornerRadius(3f))
    }
    
    private fun DrawScope.drawWaterFountain(x: Float, y: Float, animTime: Float) {
        // Base
        drawCircle(Color(0xFF607D8B), 15f, Offset(x, y))
        // Water shimmer
        val shimmer = sin(animTime * 3f) * 2f
        drawCircle(Color(0xFF2196F3).copy(alpha = 0.7f + shimmer * 0.1f), 10f, Offset(x, y - 3f))
    }
    
    private fun DrawScope.drawFeeder(x: Float, y: Float) {
        // Trough
        drawRoundRect(Color(0xFFD2691E), Offset(x - 20f, y - 5f), Size(40f, 15f),
            androidx.compose.ui.geometry.CornerRadius(3f))
        // Feed
        drawRoundRect(Color(0xFFFFD700), Offset(x - 18f, y - 10f), Size(36f, 8f),
            androidx.compose.ui.geometry.CornerRadius(2f))
    }
    
    private fun DrawScope.drawNestingBox(x: Float, y: Float) {
        drawRoundRect(Color(0xFF8B4513), Offset(x - 18f, y - 15f), Size(36f, 30f),
            androidx.compose.ui.geometry.CornerRadius(3f))
        // Straw
        drawCircle(Color(0xFFD4A84C), 10f, Offset(x, y + 5f))
    }
    
    private fun DrawScope.drawPerch(x: Float, y: Float) {
        // Vertical posts
        drawLine(Color(0xFF8B4513), Offset(x - 20f, y + 15f), Offset(x - 20f, y - 15f), 4f)
        drawLine(Color(0xFF8B4513), Offset(x + 20f, y + 15f), Offset(x + 20f, y - 15f), 4f)
        // Horizontal bar
        drawLine(Color(0xFF654321), Offset(x - 22f, y - 15f), Offset(x + 22f, y - 15f), 5f)
    }
    
    private fun DrawScope.drawDustBath(x: Float, y: Float) {
        // Shallow pit
        drawOval(Color(0xFFB8860B).copy(alpha = 0.6f), Offset(x - 25f, y - 8f), Size(50f, 20f))
        drawOval(Color(0xFFC4A853), Offset(x - 20f, y - 5f), Size(40f, 14f))
    }
    
    private fun DrawScope.drawShadeShelter(x: Float, y: Float) {
        // Posts
        drawLine(Color(0xFF654321), Offset(x - 30f, y + 20f), Offset(x - 30f, y - 10f), 3f)
        drawLine(Color(0xFF654321), Offset(x + 30f, y + 20f), Offset(x + 30f, y - 10f), 3f)
        // Roof
        drawRect(Color(0xFF4A4A4A).copy(alpha = 0.8f), Offset(x - 35f, y - 15f), Size(70f, 8f))
    }
    
    // ==================== RESOURCE BARS ====================
    
    /**
     * Draw a resource bar (feed, water, etc.)
     */
    fun DrawScope.drawResourceBar(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        resource: FarmResource
    ) {
        val color = when (resource.type) {
            ResourceType.FEED -> Color(0xFFFFD700)
            ResourceType.WATER -> Color(0xFF2196F3)
            ResourceType.MEDICINE -> Color(0xFFE91E63)
            ResourceType.BEDDING -> Color(0xFF8D6E63)
            ResourceType.COINS -> Color(0xFFFFD700)
        }
        
        // Background
        drawRoundRect(Color.White.copy(alpha = 0.8f), Offset(x, y), Size(width, height),
            androidx.compose.ui.geometry.CornerRadius(height / 2))
        
        // Border
        drawRoundRect(Color.Gray.copy(alpha = 0.5f), Offset(x, y), Size(width, height),
            androidx.compose.ui.geometry.CornerRadius(height / 2), style = Stroke(1f))
        
        // Fill
        val fillWidth = (width - 4f) * resource.percentage
        if (fillWidth > 0) {
            val fillColor = if (resource.isLow) Color(0xFFFF5722) else color
            drawRoundRect(fillColor, Offset(x + 2f, y + 2f), Size(fillWidth, height - 4f),
                androidx.compose.ui.geometry.CornerRadius((height - 4f) / 2))
        }
        
        // Icon
        drawCircle(color, 6f, Offset(x + 10f, y + height / 2))
    }
    
    // ==================== DAILY TASKS ====================
    
    /**
     * Draw daily task progress indicator
     */
    fun DrawScope.drawDailyTaskProgress(
        x: Float,
        y: Float,
        task: DailyTask,
        animTime: Float
    ) {
        val size = 50f
        
        // Background circle
        drawCircle(Color.White.copy(alpha = 0.9f), size / 2, Offset(x, y))
        
        // Progress arc
        val sweepAngle = task.progress * 360f
        if (sweepAngle > 0) {
            drawArc(
                color = if (task.isCompleted) Color(0xFF4CAF50) else Color(0xFF2196F3),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(x - size/2, y - size/2),
                size = Size(size, size),
                style = Stroke(width = 4f)
            )
        }
        
        // Completion checkmark
        if (task.isCompleted) {
            val pulse = 1f + sin(animTime * 3f) * 0.1f
            drawCircle(Color(0xFF4CAF50), 15f * pulse, Offset(x, y))
            // Simple checkmark
            drawLine(Color.White, Offset(x - 5f, y), Offset(x - 1f, y + 5f), 2f)
            drawLine(Color.White, Offset(x - 1f, y + 5f), Offset(x + 7f, y - 4f), 2f)
        }
    }
    
    // ==================== PEDIGREE TREE ====================
    
    /**
     * Draw pedigree tree node
     */
    fun DrawScope.drawPedigreeNode(
        x: Float,
        y: Float,
        node: PedigreeNode,
        nodeSize: Float = 40f
    ) {
        val color = when (node.parentType) {
            ParentType.FATHER -> Color(0xFF2196F3)
            ParentType.MOTHER -> Color(0xFFE91E63)
            ParentType.NONE -> Color(0xFF4CAF50)
        }
        
        // Node circle
        drawCircle(color, nodeSize / 2, Offset(x, y))
        drawCircle(Color.White, nodeSize / 2 - 3f, Offset(x, y))
        
        // Inner icon
        drawCircle(color.copy(alpha = 0.3f), nodeSize / 3, Offset(x, y))
        
        // Generation indicator
        if (node.generation > 0) {
            drawCircle(Color.Gray.copy(alpha = 0.6f), 8f, Offset(x + nodeSize/2 - 5f, y - nodeSize/2 + 5f))
        }
    }
    
    /**
     * Draw pedigree connection line
     */
    fun DrawScope.drawPedigreeConnection(
        fromX: Float, fromY: Float,
        toX: Float, toY: Float,
        color: Color = Color.Gray
    ) {
        // Bezier curve for elegant connection
        treePath.reset()
        treePath.moveTo(fromX, fromY)
        val midY = (fromY + toY) / 2
        treePath.cubicTo(fromX, midY, toX, midY, toX, toY)
        drawPath(treePath, color.copy(alpha = 0.5f), style = Stroke(width = 2f))
    }
    
    // ==================== INVENTORY ====================
    
    /**
     * Draw inventory item row
     */
    fun DrawScope.drawInventoryItem(
        x: Float,
        y: Float,
        width: Float,
        item: FarmInventoryItem
    ) {
        val color = when (item.category) {
            InventoryCategory.FEED -> Color(0xFFFFD700)
            InventoryCategory.MEDICINE -> Color(0xFFE91E63)
            InventoryCategory.EQUIPMENT -> Color(0xFF607D8B)
            InventoryCategory.BEDDING -> Color(0xFF8D6E63)
            InventoryCategory.SUPPLEMENTS -> Color(0xFF4CAF50)
            InventoryCategory.OTHER -> Color(0xFF9E9E9E)
        }
        
        // Warning background if needs restock
        if (item.needsRestock) {
            drawRoundRect(
                Color(0xFFFF5722).copy(alpha = 0.2f),
                Offset(x, y - 2f), Size(width, 24f),
                androidx.compose.ui.geometry.CornerRadius(4f)
            )
        }
        
        // Category icon
        drawCircle(color, 8f, Offset(x + 12f, y + 10f))
        
        // Quantity bar
        val barWidth = width * 0.4f
        val barX = x + width - barWidth - 10f
        val fillWidth = (barWidth * (item.currentQuantity / (item.reorderLevel * 4)).coerceIn(0f, 1f))
        
        drawRoundRect(Color.Gray.copy(alpha = 0.2f), Offset(barX, y + 6f), Size(barWidth, 8f),
            androidx.compose.ui.geometry.CornerRadius(4f))
        drawRoundRect(color, Offset(barX, y + 6f), Size(fillWidth, 8f),
            androidx.compose.ui.geometry.CornerRadius(4f))
    }
    
    // ==================== LEADERBOARD ====================
    
    /**
     * Draw leaderboard entry row
     */
    fun DrawScope.drawLeaderboardEntry(
        x: Float,
        y: Float,
        width: Float,
        entry: LeaderboardEntry
    ) {
        // Highlight current user
        if (entry.isCurrentUser) {
            drawRoundRect(
                Color(0xFFFFD700).copy(alpha = 0.2f),
                Offset(x, y - 2f), Size(width, 36f),
                androidx.compose.ui.geometry.CornerRadius(8f)
            )
        }
        
        // Rank badge
        val rankColor = when (entry.rank) {
            1 -> Color(0xFFFFD700) // Gold
            2 -> Color(0xFFC0C0C0) // Silver
            3 -> Color(0xFFCD7F32) // Bronze
            else -> Color(0xFF9E9E9E)
        }
        
        drawCircle(rankColor, 14f, Offset(x + 20f, y + 16f))
        
        // Avatar placeholder
        drawCircle(Color(0xFF757575), 12f, Offset(x + 50f, y + 16f))
        
        // Score bar
        val scoreWidth = (width * 0.3f) * (entry.score / 1000f).coerceIn(0f, 1f)
        drawRoundRect(
            Color(0xFF4CAF50).copy(alpha = 0.3f),
            Offset(x + width - scoreWidth - 10f, y + 10f),
            Size(scoreWidth, 12f),
            androidx.compose.ui.geometry.CornerRadius(6f)
        )
    }
    
    // ==================== COMPETITION ====================
    
    /**
     * Draw competition card
     */
    fun DrawScope.drawCompetitionCard(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        competition: FarmCompetition,
        animTime: Float
    ) {
        // Card background
        drawRoundRect(
            Color.White,
            Offset(x, y), Size(width, height),
            androidx.compose.ui.geometry.CornerRadius(12f)
        )
        
        // Active indicator
        if (competition.isActive) {
            val pulse = 0.5f + sin(animTime * 3f) * 0.2f
            drawCircle(
                Color(0xFF4CAF50).copy(alpha = pulse),
                8f, Offset(x + width - 15f, y + 15f)
            )
        }
        
        // Type icon
        val typeColor = when (competition.type) {
            CompetitionType.BEST_BREEDER -> Color(0xFFE91E63)
            CompetitionType.TOP_SELLER -> Color(0xFFFFD700)
            CompetitionType.HEALTHIEST_FLOCK -> Color(0xFF4CAF50)
            CompetitionType.EGG_CHAMPION -> Color(0xFFFFC107)
            CompetitionType.FASTEST_GROWTH -> Color(0xFF2196F3)
            CompetitionType.SHOWCASE -> Color(0xFF9C27B0)
        }
        drawCircle(typeColor, 15f, Offset(x + 25f, y + 30f))
        
        // Prize indicator
        drawCircle(Color(0xFFFFD700), 10f, Offset(x + width - 35f, y + height - 20f))
    }
    
    // ==================== OFFLINE INDICATOR ====================
    
    /**
     * Draw offline mode indicator
     */
    fun DrawScope.drawOfflineIndicator(
        x: Float,
        y: Float,
        snapshot: OfflineFarmSnapshot
    ) {
        val color = when {
            snapshot.isExpired -> Color(0xFFFF5722)
            snapshot.isStale -> Color(0xFFFFC107)
            else -> Color(0xFF4CAF50)
        }
        
        // Badge background
        drawRoundRect(
            color.copy(alpha = 0.9f),
            Offset(x - 35f, y - 10f), Size(70f, 20f),
            androidx.compose.ui.geometry.CornerRadius(10f)
        )
        
        // Cloud icon (simplified)
        drawCircle(Color.White, 5f, Offset(x - 10f, y))
        drawCircle(Color.White, 6f, Offset(x - 3f, y - 2f))
        drawCircle(Color.White, 5f, Offset(x + 4f, y))
    }
}
