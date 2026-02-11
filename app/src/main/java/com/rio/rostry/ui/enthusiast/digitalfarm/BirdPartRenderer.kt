package com.rio.rostry.ui.enthusiast.digitalfarm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import com.rio.rostry.domain.model.*
import kotlin.math.sin

/**
 * BGMI-Style Bird Part Renderer
 *
 * Renders each body part of a bird independently using low-poly
 * isometric drawing primitives. Each part style maps to a different
 * visual shape drawn on the Canvas.
 *
 * Render order (back to front):
 * 1. Shadow
 * 2. Tail feathers
 * 3. Back / body
 * 4. Wings
 * 5. Chest / front
 * 6. Legs + joints + nails
 * 7. Head + eye + beak
 * 8. Comb + crown
 * 9. Wattle + ear lobe
 * 10. Status indicators
 */
object BirdPartRenderer {

    /**
     * Main entry: draws a complete bird from its appearance at position (x, y).
     * The position is the bird's "feet" position in isometric space.
     */
    fun DrawScope.drawBirdFromAppearance(
        x: Float,
        y: Float,
        appearance: BirdAppearance,
        isSelected: Boolean = false,
        animTime: Float = 0f,
        bobOffset: Float = 0f
    ) {
        val sizeMultiplier = when (appearance.bodySize) {
            BodySize.TINY -> 0.4f
            BodySize.BANTAM -> 0.55f
            BodySize.SMALL -> 0.75f
            BodySize.MEDIUM -> 1.0f
            BodySize.LARGE -> 1.2f
            BodySize.XLARGE -> 1.4f
        }

        val s = sizeMultiplier

        // === STANCE affects vertical offset (posture) ===
        val stanceYShift = when (appearance.stance) {
            Stance.UPRIGHT -> -4f * s      // Taller, head higher
            Stance.LOW -> 3f * s            // Lower to ground
            Stance.GAME_READY -> -2f * s    // Slightly elevated, alert
            Stance.CROUCHING -> 5f * s      // Very low
            Stance.DISPLAY -> -3f * s       // Show pose, puffed up
            Stance.NORMAL -> 0f
        }

        val by = y - bobOffset + stanceYShift // Apply bobbing + stance

        // Selection glow
        if (isSelected) {
            drawCircle(
                color = Color(0x40FFD700),
                radius = 28f * s,
                center = Offset(x, by - 12f * s)
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 24f * s,
                center = Offset(x, by - 12f * s),
                style = Stroke(width = 2f)
            )
        }

        // ==================== 1. SHADOW ====================
        val shadowWidth = when (appearance.stance) {
            Stance.DISPLAY -> 32f * s  // Wider shadow for display pose
            Stance.CROUCHING -> 22f * s
            else -> 28f * s
        }
        drawOval(
            color = Color(0x30000000),
            topLeft = Offset(x - shadowWidth / 2f, y - 2f),
            size = Size(shadowWidth, 8f * s)
        )

        // ==================== 2. TAIL ====================
        drawTail(x, by, s, appearance)

        // ==================== 3. BACK / BODY ====================
        drawBody(x, by, s, appearance)

        // ==================== 4. WINGS ====================
        drawWing(x, by, s, appearance, animTime)

        // ==================== 5. CHEST / FRONT PLUMAGE ====================
        drawChest(x, by, s, appearance)

        // ==================== 5.5 NECK ====================
        drawNeck(x, by, s, appearance)

        // ==================== 6. LEGS + JOINTS + NAILS ====================
        drawLegs(x, by, y, s, appearance)

        // ==================== 7. HEAD + EYE + BEAK ====================
        drawHead(x, by, s, appearance)

        // ==================== 8. COMB + CROWN ====================
        drawComb(x, by, s, appearance, animTime)

        // ==================== 9. WATTLE + EAR LOBE ====================
        drawWattle(x, by, s, appearance)

        // ==================== 10. SHEEN OVERLAY ====================
        drawSheen(x, by, s, appearance)
    }

    // ==================== BODY ====================

    private fun DrawScope.drawBody(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val bodyColor = a.backColor.color

        // Breast shape affects body proportions
        val widthMod = when (a.breast) {
            BreastShape.FLAT -> 0.85f
            BreastShape.BROAD -> 1.15f
            BreastShape.DEEP -> 1.05f
            BreastShape.PUFFED -> 1.2f
            BreastShape.ROUND -> 1.0f
        }
        val heightMod = when (a.breast) {
            BreastShape.DEEP -> 1.15f
            BreastShape.PUFFED -> 1.1f
            else -> 1.0f
        }

        // Main body ellipse
        drawOval(
            color = bodyColor,
            topLeft = Offset(x - bodyW * widthMod, y - bodyH * 1.4f * heightMod),
            size = Size(bodyW * 2f * widthMod, bodyH * 1.6f * heightMod)
        )

        // Skin color tint (visible on face/wattle area when slate or dark)
        if (a.skin == SkinColor.DARK || a.skin == SkinColor.SLATE) {
            val skinTint = when (a.skin) {
                SkinColor.DARK -> Color(0x20000000)
                SkinColor.SLATE -> Color(0x15607D8B)
                else -> Color.Transparent
            }
            drawOval(
                color = skinTint,
                topLeft = Offset(x - bodyW * widthMod, y - bodyH * 1.4f * heightMod),
                size = Size(bodyW * 2f * widthMod, bodyH * 1.6f * heightMod)
            )
        }

        // Back style overlay
        when (a.back) {
            BackStyle.HACKLE -> {
                for (i in 0..3) {
                    val px = x - bodyW * 0.3f + i * bodyW * 0.2f
                    drawLine(
                        color = bodyColor.copy(alpha = 0.5f),
                        start = Offset(px, y - bodyH * 1.2f),
                        end = Offset(px - 3f * s, y - bodyH * 0.3f),
                        strokeWidth = 1.5f * s,
                        cap = StrokeCap.Round
                    )
                }
            }
            BackStyle.SADDLE -> {
                val saddle = Path().apply {
                    moveTo(x - bodyW * 0.5f, y - bodyH * 0.8f)
                    quadraticBezierTo(x - bodyW * 0.8f, y - bodyH * 0.2f, x - bodyW * 0.3f, y - bodyH * 0.1f)
                    lineTo(x + bodyW * 0.2f, y - bodyH * 0.3f)
                    quadraticBezierTo(x - bodyW * 0.1f, y - bodyH * 0.6f, x - bodyW * 0.5f, y - bodyH * 0.8f)
                    close()
                }
                drawPath(saddle, bodyColor.copy(alpha = 0.6f))
            }
            BackStyle.CUSHION -> {
                drawOval(
                    color = bodyColor.copy(alpha = 0.4f),
                    topLeft = Offset(x - bodyW * 0.8f, y - bodyH * 1.6f),
                    size = Size(bodyW * 1.6f, bodyH * 1.2f)
                )
            }
            else -> { /* SMOOTH: no overlay */ }
        }

        // Plumage pattern on body
        drawPlumagePattern(x - bodyW, y - bodyH * 1.4f, bodyW * 2f, bodyH * 1.6f, a.wingPattern, bodyColor, s)
    }

    // ==================== CHEST ====================

    private fun DrawScope.drawChest(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val chestColor = a.chestColor.color

        // Chest area (front of body)
        val chestPath = Path().apply {
            moveTo(x + bodyW * 0.3f, y - bodyH * 1.2f)
            quadraticBezierTo(x + bodyW * 1f, y - bodyH * 0.8f, x + bodyW * 0.5f, y - bodyH * 0.1f)
            lineTo(x + bodyW * 0.1f, y - bodyH * 0.1f)
            quadraticBezierTo(x + bodyW * 0.3f, y - bodyH * 0.7f, x + bodyW * 0.3f, y - bodyH * 1.2f)
            close()
        }
        drawPath(chestPath, chestColor)

        // Pattern overlay on chest
        drawPlumagePattern(x + bodyW * 0.1f, y - bodyH * 1.2f, bodyW * 0.8f, bodyH * 1.1f, a.chest, chestColor, s)
    }

    // ==================== WING ====================

    private fun DrawScope.drawWing(x: Float, y: Float, s: Float, a: BirdAppearance, animTime: Float) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val wingColor = a.wingColor.color

        when (a.wings) {
            WingStyle.FOLDED -> {
                // Standard folded wing arc
                drawArc(
                    color = wingColor.copy(alpha = 0.8f),
                    startAngle = -40f,
                    sweepAngle = 130f,
                    useCenter = true,
                    topLeft = Offset(x - bodyW * 0.4f, y - bodyH * 1.1f),
                    size = Size(bodyW * 1.3f, bodyH * 1f)
                )
                // Wing feather lines
                for (i in 0..2) {
                    drawLine(
                        color = wingColor.copy(alpha = 0.4f),
                        start = Offset(x - bodyW * 0.1f + i * 4f * s, y - bodyH * 0.9f + i * 3f * s),
                        end = Offset(x + bodyW * 0.5f, y - bodyH * 0.4f + i * 2f * s),
                        strokeWidth = 0.8f * s
                    )
                }
            }
            WingStyle.SPREAD -> {
                // Slightly open wing
                val wingPath = Path().apply {
                    moveTo(x - bodyW * 0.3f, y - bodyH * 1f)
                    quadraticBezierTo(x - bodyW * 1.3f, y - bodyH * 1.5f, x - bodyW * 1.5f, y - bodyH * 0.5f)
                    lineTo(x - bodyW * 0.5f, y - bodyH * 0.2f)
                    close()
                }
                drawPath(wingPath, wingColor.copy(alpha = 0.85f))
            }
            WingStyle.ANGEL -> {
                // Drooping angel wings (Serama)
                val wingPath = Path().apply {
                    moveTo(x - bodyW * 0.2f, y - bodyH * 0.8f)
                    quadraticBezierTo(x - bodyW * 1f, y - bodyH * 0.3f, x - bodyW * 0.8f, y + bodyH * 0.2f)
                    lineTo(x - bodyW * 0.3f, y - bodyH * 0.1f)
                    close()
                }
                drawPath(wingPath, wingColor.copy(alpha = 0.85f))
            }
            WingStyle.TIGHT -> {
                // Tight to body (game bird)
                drawArc(
                    color = wingColor.copy(alpha = 0.7f),
                    startAngle = -30f,
                    sweepAngle = 100f,
                    useCenter = true,
                    topLeft = Offset(x - bodyW * 0.3f, y - bodyH * 1f),
                    size = Size(bodyW * 1.0f, bodyH * 0.9f)
                )
            }
            WingStyle.CLIPPED -> {
                // Short, trimmed wing
                drawArc(
                    color = wingColor.copy(alpha = 0.75f),
                    startAngle = -30f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(x - bodyW * 0.2f, y - bodyH * 0.9f),
                    size = Size(bodyW * 0.8f, bodyH * 0.7f)
                )
            }
        }

        // Wing pattern
        drawPlumagePattern(x - bodyW * 0.4f, y - bodyH * 1.1f, bodyW * 1.2f, bodyH * 0.9f, a.wingPattern, wingColor, s)
    }

    // ==================== TAIL ====================

    private fun DrawScope.drawTail(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val tailColor = a.tailColor.color

        when (a.tail) {
            TailStyle.SHORT -> {
                // Minimal tail tuft
                val tail = Path().apply {
                    moveTo(x - bodyW * 0.6f, y - bodyH * 0.8f)
                    lineTo(x - bodyW * 1f, y - bodyH * 1.2f)
                    lineTo(x - bodyW * 0.8f, y - bodyH * 1.3f)
                    lineTo(x - bodyW * 0.4f, y - bodyH * 1f)
                    close()
                }
                drawPath(tail, tailColor)
            }
            TailStyle.SICKLE -> {
                // Standard sickle feathers (rooster)
                for (i in 0..2) {
                    val curvePath = Path().apply {
                        moveTo(x - bodyW * 0.6f, y - bodyH * 0.7f)
                        cubicTo(
                            x - bodyW * (1.2f + i * 0.15f), y - bodyH * (1.8f + i * 0.3f),
                            x - bodyW * (1f + i * 0.1f), y - bodyH * (2.2f + i * 0.4f),
                            x - bodyW * 0.4f, y - bodyH * (1.5f + i * 0.2f)
                        )
                    }
                    drawPath(curvePath, tailColor.copy(alpha = 0.9f - i * 0.1f), style = Stroke(width = (3f - i * 0.5f) * s, cap = StrokeCap.Round))
                }
            }
            TailStyle.LONG_SICKLE -> {
                // Exaggerated long sickles (Phoenix style)
                for (i in 0..4) {
                    val curvePath = Path().apply {
                        moveTo(x - bodyW * 0.5f, y - bodyH * 0.6f)
                        cubicTo(
                            x - bodyW * (1.5f + i * 0.2f), y - bodyH * (2f + i * 0.5f),
                            x - bodyW * (1.8f + i * 0.15f), y - bodyH * (3f + i * 0.4f),
                            x - bodyW * (0.5f + i * 0.1f), y - bodyH * (2.5f + i * 0.3f)
                        )
                    }
                    drawPath(curvePath, tailColor.copy(alpha = 0.85f - i * 0.08f), style = Stroke(width = (2.5f - i * 0.3f) * s, cap = StrokeCap.Round))
                }
            }
            TailStyle.FAN -> {
                // Wide fan tail
                for (angle in -30..30 step 10) {
                    val rad = Math.toRadians(angle.toDouble())
                    val endX = x - bodyW * 0.6f + (bodyW * 1.2f * sin(rad)).toFloat()
                    val endY = y - bodyH * 1.8f - (bodyH * 0.3f * kotlin.math.cos(rad)).toFloat()
                    drawLine(
                        color = tailColor,
                        start = Offset(x - bodyW * 0.5f, y - bodyH * 0.7f),
                        end = Offset(endX, endY),
                        strokeWidth = 2.5f * s,
                        cap = StrokeCap.Round
                    )
                }
            }
            TailStyle.SQUIRREL -> {
                // Forward-curving squirrel tail
                val squirrelPath = Path().apply {
                    moveTo(x - bodyW * 0.5f, y - bodyH * 0.6f)
                    cubicTo(
                        x - bodyW * 0.8f, y - bodyH * 2f,
                        x - bodyW * 0.2f, y - bodyH * 2.5f,
                        x + bodyW * 0.2f, y - bodyH * 1.8f
                    )
                }
                drawPath(squirrelPath, tailColor, style = Stroke(width = 3f * s, cap = StrokeCap.Round))
            }
            TailStyle.WHIP -> {
                // Long thin whip tail
                val whipPath = Path().apply {
                    moveTo(x - bodyW * 0.5f, y - bodyH * 0.6f)
                    cubicTo(
                        x - bodyW * 2f, y - bodyH * 1.5f,
                        x - bodyW * 2.5f, y - bodyH * 2f,
                        x - bodyW * 2.2f, y - bodyH * 2.5f
                    )
                }
                drawPath(whipPath, tailColor, style = Stroke(width = 2f * s, cap = StrokeCap.Round))
            }
            TailStyle.NONE -> { /* Rumpless - no tail */ }
        }
    }

    // ==================== HEAD ====================

    private fun DrawScope.drawHead(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val headY = y - bodyH * 1.8f
        val headR = bodyW * 0.42f
        val headColor = a.chestColor.color // Head matches chest

        // Head shape affects proportions
        when (a.headShape) {
            HeadShape.ROUND -> {
                drawCircle(color = headColor, radius = headR, center = Offset(x + bodyW * 0.4f, headY))
            }
            HeadShape.ELONGATED -> {
                drawOval(
                    color = headColor,
                    topLeft = Offset(x + bodyW * 0.4f - headR * 1.2f, headY - headR * 0.85f),
                    size = Size(headR * 2.4f, headR * 1.7f)
                )
            }
            HeadShape.BROAD -> {
                drawOval(
                    color = headColor,
                    topLeft = Offset(x + bodyW * 0.4f - headR * 1.1f, headY - headR * 0.8f),
                    size = Size(headR * 2.2f, headR * 1.6f)
                )
            }
            HeadShape.SERPENTINE -> {
                drawOval(
                    color = headColor,
                    topLeft = Offset(x + bodyW * 0.4f - headR * 0.7f, headY - headR * 1.1f),
                    size = Size(headR * 1.4f, headR * 2.2f)
                )
            }
            HeadShape.COMPACT -> {
                drawCircle(color = headColor, radius = headR * 0.85f, center = Offset(x + bodyW * 0.4f, headY))
            }
        }

        // Crown style
        when (a.crown) {
            CrownStyle.POLISH -> {
                // Full pompom
                val crownColor = a.crownColor.color
                drawCircle(color = crownColor, radius = headR * 0.8f, center = Offset(x + bodyW * 0.35f, headY - headR * 0.7f))
                drawCircle(color = crownColor.copy(alpha = 0.7f), radius = headR * 0.6f, center = Offset(x + bodyW * 0.5f, headY - headR * 0.9f))
                drawCircle(color = crownColor.copy(alpha = 0.5f), radius = headR * 0.4f, center = Offset(x + bodyW * 0.25f, headY - headR * 0.85f))
            }
            CrownStyle.CREST -> {
                // Small tuft
                val crownColor = a.crownColor.color
                drawCircle(color = crownColor, radius = headR * 0.4f, center = Offset(x + bodyW * 0.4f, headY - headR * 0.8f))
            }
            CrownStyle.MOHAWK -> {
                // Upright crest
                val crownColor = a.crownColor.color
                val mohawk = Path().apply {
                    moveTo(x + bodyW * 0.2f, headY - headR)
                    lineTo(x + bodyW * 0.4f, headY - headR - 10f * s)
                    lineTo(x + bodyW * 0.6f, headY - headR - 8f * s)
                    lineTo(x + bodyW * 0.55f, headY - headR)
                    close()
                }
                drawPath(mohawk, crownColor)
            }
            CrownStyle.MUFF -> {
                // Side muffs/whiskers
                val crownColor = a.crownColor.color
                drawOval(
                    color = crownColor.copy(alpha = 0.7f),
                    topLeft = Offset(x + bodyW * 0.15f, headY - headR * 0.1f),
                    size = Size(headR * 0.6f, headR * 0.8f)
                )
                drawOval(
                    color = crownColor.copy(alpha = 0.7f),
                    topLeft = Offset(x + bodyW * 0.55f, headY - headR * 0.1f),
                    size = Size(headR * 0.6f, headR * 0.8f)
                )
            }
            CrownStyle.CLEAN -> { /* No head feathers */ }
        }

        // Eye
        val eyeColor = when (a.eye) {
            EyeColor.ORANGE -> Color(0xFFFF9800)
            EyeColor.RED -> Color(0xFFE53935)
            EyeColor.PEARL -> Color(0xFFE0E0E0)
            EyeColor.BAY -> Color(0xFF795548)
            EyeColor.DARK -> Color(0xFF212121)
            EyeColor.YELLOW -> Color(0xFFFFEB3B)
        }
        // Iris
        drawCircle(color = eyeColor, radius = 2.5f * s, center = Offset(x + bodyW * 0.55f, headY - headR * 0.1f))
        // Pupil
        drawCircle(color = Color.Black, radius = 1.2f * s, center = Offset(x + bodyW * 0.55f, headY - headR * 0.1f))
        // Eye highlight
        drawCircle(color = Color.White.copy(alpha = 0.7f), radius = 0.7f * s, center = Offset(x + bodyW * 0.53f, headY - headR * 0.2f))

        // Beak
        drawBeak(x, headY, s, bodyW, headR, a)

        // Ear lobe
        val earColor = when (a.earLobe) {
            EarLobeColor.RED -> Color(0xFFE53935)
            EarLobeColor.WHITE -> Color(0xFFFAFAFA)
            EarLobeColor.BLUE -> Color(0xFF5C6BC0)
            EarLobeColor.TURQUOISE -> Color(0xFF26A69A)
        }
        drawCircle(
            color = earColor,
            radius = 2f * s,
            center = Offset(x + bodyW * 0.45f, headY + headR * 0.3f)
        )
    }

    // ==================== BEAK ====================

    private fun DrawScope.drawBeak(x: Float, headY: Float, s: Float, bodyW: Float, headR: Float, a: BirdAppearance) {
        val beakColor = a.beakColor.color
        val beakLen = when (a.beak) {
            BeakStyle.SHORT -> bodyW * 0.35f
            BeakStyle.MEDIUM -> bodyW * 0.5f
            BeakStyle.LONG -> bodyW * 0.7f
            BeakStyle.HOOKED -> bodyW * 0.55f
            BeakStyle.CURVED -> bodyW * 0.5f
        }

        when (a.beak) {
            BeakStyle.HOOKED -> {
                val beakPath = Path().apply {
                    moveTo(x + bodyW * 0.7f, headY + headR * 0.05f)
                    quadraticBezierTo(x + bodyW * 0.7f + beakLen, headY, x + bodyW * 0.7f + beakLen * 0.8f, headY + headR * 0.4f)
                    lineTo(x + bodyW * 0.7f, headY + headR * 0.35f)
                    close()
                }
                drawPath(beakPath, beakColor)
            }
            BeakStyle.CURVED -> {
                val beakPath = Path().apply {
                    moveTo(x + bodyW * 0.7f, headY + headR * 0.05f)
                    quadraticBezierTo(x + bodyW * 0.7f + beakLen * 1.1f, headY + headR * 0.15f, x + bodyW * 0.7f + beakLen * 0.9f, headY + headR * 0.3f)
                    lineTo(x + bodyW * 0.7f, headY + headR * 0.35f)
                    close()
                }
                drawPath(beakPath, beakColor)
            }
            else -> {
                // Standard triangular beak
                val beakPath = Path().apply {
                    moveTo(x + bodyW * 0.7f, headY + headR * 0.05f)
                    lineTo(x + bodyW * 0.7f + beakLen, headY + headR * 0.2f)
                    lineTo(x + bodyW * 0.7f, headY + headR * 0.35f)
                    close()
                }
                drawPath(beakPath, beakColor)
            }
        }

        // Nostril dot
        drawCircle(
            color = Color(0xFF5D4037),
            radius = 0.8f * s,
            center = Offset(x + bodyW * 0.78f, headY + headR * 0.15f)
        )
    }

    // ==================== COMB ====================

    private fun DrawScope.drawComb(x: Float, y: Float, s: Float, a: BirdAppearance, animTime: Float) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val headY = y - bodyH * 1.8f
        val headR = bodyW * 0.42f
        val combColor = a.combColor.color
        val combScale = if (a.isMale) 1.2f else 0.7f

        when (a.comb) {
            CombStyle.SINGLE -> {
                // Upright serrated comb
                val points = if (a.isMale) 5 else 3
                val combH = 8f * s * combScale
                val combPath = Path().apply {
                    moveTo(x + bodyW * 0.15f, headY - headR)
                    for (i in 0 until points) {
                        val px = x + bodyW * 0.15f + (i + 0.5f) * bodyW * 0.12f
                        val peakY = headY - headR - combH * (0.6f + 0.4f * sin((i * 1.5f).toDouble()).toFloat())
                        lineTo(px, peakY)
                        lineTo(px + bodyW * 0.06f, headY - headR - combH * 0.3f)
                    }
                    lineTo(x + bodyW * 0.7f, headY - headR)
                    close()
                }
                drawPath(combPath, combColor)
            }
            CombStyle.ROSE -> {
                // Flat, fleshy, bumpy rose comb
                drawOval(
                    color = combColor,
                    topLeft = Offset(x + bodyW * 0.15f, headY - headR - 5f * s * combScale),
                    size = Size(bodyW * 0.5f, 6f * s * combScale)
                )
                // Bumps
                for (i in 0..3) {
                    drawCircle(
                        color = combColor.copy(alpha = 0.8f),
                        radius = 1.8f * s * combScale,
                        center = Offset(x + bodyW * 0.22f + i * bodyW * 0.1f, headY - headR - 3f * s * combScale)
                    )
                }
                // Spike at back
                drawLine(
                    color = combColor,
                    start = Offset(x + bodyW * 0.55f, headY - headR - 2f * s),
                    end = Offset(x + bodyW * 0.65f, headY - headR + 1f * s),
                    strokeWidth = 2.5f * s * combScale,
                    cap = StrokeCap.Round
                )
            }
            CombStyle.PEA -> {
                // Three parallel ridges
                val peaH = 4f * s * combScale
                for (row in 0..2) {
                    val offsetY = row * 2f * s
                    val offsetX = (row - 1) * 2f * s
                    drawOval(
                        color = combColor.copy(alpha = 0.9f - row * 0.1f),
                        topLeft = Offset(x + bodyW * 0.2f + offsetX, headY - headR - peaH - offsetY),
                        size = Size(bodyW * 0.4f, peaH)
                    )
                }
            }
            CombStyle.WALNUT -> {
                // Round bumpy walnut comb
                drawCircle(
                    color = combColor,
                    radius = 5f * s * combScale,
                    center = Offset(x + bodyW * 0.4f, headY - headR - 2f * s * combScale)
                )
                // Texture bumps
                for (i in 0..4) {
                    val bx = x + bodyW * 0.35f + (i % 3 - 1) * 2.5f * s
                    val bby = headY - headR - (2f + (i % 2) * 2f) * s * combScale
                    drawCircle(
                        color = combColor.copy(alpha = 0.6f),
                        radius = 1.5f * s * combScale,
                        center = Offset(bx, bby)
                    )
                }
            }
            CombStyle.BUTTERCUP -> {
                // Cup-shaped with points around rim
                drawCircle(
                    color = combColor,
                    radius = 4f * s * combScale,
                    center = Offset(x + bodyW * 0.4f, headY - headR - 3f * s)
                )
                // Inner cup
                drawCircle(
                    color = combColor.copy(alpha = 0.5f),
                    radius = 2.5f * s * combScale,
                    center = Offset(x + bodyW * 0.4f, headY - headR - 3.5f * s)
                )
                // Points
                for (i in 0..5) {
                    val angle = Math.toRadians((i * 60).toDouble())
                    val px = x + bodyW * 0.4f + (5f * s * combScale * kotlin.math.cos(angle)).toFloat()
                    val py = headY - headR - 3f * s + (3f * s * combScale * sin(angle)).toFloat()
                    drawCircle(color = combColor, radius = 1.2f * s, center = Offset(px, py))
                }
            }
            CombStyle.V_SHAPED -> {
                // Two horn-like points
                val horn1 = Path().apply {
                    moveTo(x + bodyW * 0.3f, headY - headR)
                    lineTo(x + bodyW * 0.2f, headY - headR - 10f * s * combScale)
                    lineTo(x + bodyW * 0.35f, headY - headR)
                    close()
                }
                val horn2 = Path().apply {
                    moveTo(x + bodyW * 0.5f, headY - headR)
                    lineTo(x + bodyW * 0.6f, headY - headR - 10f * s * combScale)
                    lineTo(x + bodyW * 0.55f, headY - headR)
                    close()
                }
                drawPath(horn1, combColor)
                drawPath(horn2, combColor)
            }
            CombStyle.STRAWBERRY -> {
                // Low rounded
                drawOval(
                    color = combColor,
                    topLeft = Offset(x + bodyW * 0.2f, headY - headR - 4f * s * combScale),
                    size = Size(bodyW * 0.4f, 5f * s * combScale)
                )
            }
            CombStyle.NONE -> { /* Chick - no comb */ }
        }
    }

    // ==================== WATTLE ====================

    private fun DrawScope.drawWattle(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val headY = y - bodyH * 1.8f
        val headR = bodyW * 0.42f
        val wattleColor = a.wattleColor.color

        when (a.wattle) {
            WattleStyle.SMALL -> {
                drawCircle(
                    color = wattleColor,
                    radius = 2f * s,
                    center = Offset(x + bodyW * 0.6f, headY + headR * 0.65f)
                )
            }
            WattleStyle.MEDIUM -> {
                drawOval(
                    color = wattleColor,
                    topLeft = Offset(x + bodyW * 0.52f, headY + headR * 0.5f),
                    size = Size(5f * s, 7f * s)
                )
            }
            WattleStyle.LARGE -> {
                drawOval(
                    color = wattleColor,
                    topLeft = Offset(x + bodyW * 0.48f, headY + headR * 0.45f),
                    size = Size(7f * s, 10f * s)
                )
                // Second lobe
                drawOval(
                    color = wattleColor.copy(alpha = 0.8f),
                    topLeft = Offset(x + bodyW * 0.58f, headY + headR * 0.5f),
                    size = Size(5f * s, 8f * s)
                )
            }
            WattleStyle.PENDULOUS -> {
                // Very long drooping wattle (Spanish)
                val wattlePath = Path().apply {
                    moveTo(x + bodyW * 0.5f, headY + headR * 0.4f)
                    quadraticBezierTo(x + bodyW * 0.55f, headY + headR * 1.5f, x + bodyW * 0.65f, headY + headR * 1.8f)
                    quadraticBezierTo(x + bodyW * 0.7f, headY + headR * 1.5f, x + bodyW * 0.6f, headY + headR * 0.4f)
                    close()
                }
                drawPath(wattlePath, wattleColor)
            }
            WattleStyle.NONE -> { /* No wattle */ }
        }
    }

    // ==================== LEGS + JOINTS + NAILS ====================

    private fun DrawScope.drawLegs(x: Float, bodyY: Float, feetY: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val legColor = a.legColor.color
        val legThickness = when (a.joints) {
            JointStyle.HEAVY -> 3f * s
            JointStyle.SLIM -> 1.5f * s
            JointStyle.LONG -> 2f * s
            JointStyle.SHORT -> 2.5f * s
            JointStyle.STANDARD -> 2f * s
        }

        val legLength = when (a.joints) {
            JointStyle.LONG -> 1.4f
            JointStyle.SHORT -> 0.7f
            else -> 1f
        }

        val legTopY = bodyY - 2f * s
        val legBottomY = feetY + 2f * s * legLength

        // Left leg
        val leftX = x - bodyW * 0.15f
        drawLine(
            color = legColor,
            start = Offset(leftX, legTopY),
            end = Offset(leftX - 2f * s, legBottomY),
            strokeWidth = legThickness,
            cap = StrokeCap.Round
        )
        // Right leg
        val rightX = x + bodyW * 0.15f
        drawLine(
            color = legColor,
            start = Offset(rightX, legTopY),
            end = Offset(rightX + 2f * s, legBottomY),
            strokeWidth = legThickness,
            cap = StrokeCap.Round
        )

        // Joint dots
        if (a.joints == JointStyle.HEAVY) {
            drawCircle(color = legColor, radius = legThickness * 0.8f, center = Offset(leftX - 1f * s, (legTopY + legBottomY) / 2f))
            drawCircle(color = legColor, radius = legThickness * 0.8f, center = Offset(rightX + 1f * s, (legTopY + legBottomY) / 2f))
        }

        // Toes (3 forward + 1 back)
        val toeLen = 4f * s
        for (leg in listOf(leftX - 2f * s, rightX + 2f * s)) {
            // Forward toes
            drawLine(color = legColor, start = Offset(leg, legBottomY), end = Offset(leg + toeLen, legBottomY + toeLen * 0.3f), strokeWidth = 1f * s, cap = StrokeCap.Round)
            drawLine(color = legColor, start = Offset(leg, legBottomY), end = Offset(leg + toeLen * 0.7f, legBottomY + toeLen * 0.5f), strokeWidth = 1f * s, cap = StrokeCap.Round)
            drawLine(color = legColor, start = Offset(leg, legBottomY), end = Offset(leg + toeLen * 0.3f, legBottomY + toeLen * 0.6f), strokeWidth = 1f * s, cap = StrokeCap.Round)
            // Back toe
            drawLine(color = legColor, start = Offset(leg, legBottomY), end = Offset(leg - toeLen * 0.5f, legBottomY + toeLen * 0.4f), strokeWidth = 1f * s, cap = StrokeCap.Round)
        }

        // Nails / Spurs
        drawNails(x, legTopY, legBottomY, s, a)

        // Leg feathering
        when (a.legs) {
            LegStyle.FEATHERED -> {
                // Light feathering on shanks
                val featherColor = a.wingColor.color
                for (leg in listOf(leftX - 2f * s, rightX + 2f * s)) {
                    for (i in 0..2) {
                        val fy = legTopY + (legBottomY - legTopY) * (0.3f + i * 0.2f)
                        drawLine(
                            color = featherColor.copy(alpha = 0.6f),
                            start = Offset(leg, fy),
                            end = Offset(leg - 3f * s, fy + 3f * s),
                            strokeWidth = 1.5f * s, cap = StrokeCap.Round
                        )
                        drawLine(
                            color = featherColor.copy(alpha = 0.6f),
                            start = Offset(leg, fy),
                            end = Offset(leg + 3f * s, fy + 3f * s),
                            strokeWidth = 1.5f * s, cap = StrokeCap.Round
                        )
                    }
                }
            }
            LegStyle.HEAVILY_FEATHERED -> {
                // Full leg muffs
                val featherColor = a.wingColor.color
                for (leg in listOf(leftX - 2f * s, rightX + 2f * s)) {
                    drawOval(
                        color = featherColor.copy(alpha = 0.7f),
                        topLeft = Offset(leg - 5f * s, legTopY + (legBottomY - legTopY) * 0.2f),
                        size = Size(10f * s, (legBottomY - legTopY) * 0.8f)
                    )
                }
            }
            LegStyle.BOOTED -> {
                // Vulture hocks + feet feathers
                val featherColor = a.wingColor.color
                for (leg in listOf(leftX - 2f * s, rightX + 2f * s)) {
                    // Hock feathers
                    for (i in 0..3) {
                        val fy = legTopY + (legBottomY - legTopY) * (0.4f + i * 0.15f)
                        drawLine(
                            color = featherColor,
                            start = Offset(leg, fy),
                            end = Offset(leg - 6f * s, fy + 5f * s),
                            strokeWidth = 2f * s, cap = StrokeCap.Round
                        )
                    }
                    // Foot feathers
                    drawOval(
                        color = featherColor.copy(alpha = 0.6f),
                        topLeft = Offset(leg - 4f * s, legBottomY - 3f * s),
                        size = Size(8f * s, 8f * s)
                    )
                }
            }
            else -> { /* CLEAN, SPURRED: no feathers */ }
        }
    }

    // ==================== NAILS / SPURS ====================

    private fun DrawScope.drawNails(x: Float, legTopY: Float, legBottomY: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val nailColor = a.nailColor.color
        val midY = (legTopY + legBottomY) / 2f

        when (a.nails) {
            NailStyle.LONG_SPUR -> {
                // Single spur on each leg
                val spurLen = 6f * s
                drawLine(
                    color = nailColor,
                    start = Offset(x - bodyW * 0.15f - 2f * s, midY + 2f * s),
                    end = Offset(x - bodyW * 0.15f - 2f * s - spurLen, midY),
                    strokeWidth = 2f * s,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = nailColor,
                    start = Offset(x + bodyW * 0.15f + 2f * s, midY + 2f * s),
                    end = Offset(x + bodyW * 0.15f + 2f * s + spurLen, midY),
                    strokeWidth = 2f * s,
                    cap = StrokeCap.Round
                )
            }
            NailStyle.DOUBLE_SPUR -> {
                // Double spur
                for (offset in listOf(-2f * s, 2f * s)) {
                    val spurLen = 5f * s
                    drawLine(
                        color = nailColor,
                        start = Offset(x - bodyW * 0.15f - 2f * s, midY + offset),
                        end = Offset(x - bodyW * 0.15f - 2f * s - spurLen, midY + offset - 3f * s),
                        strokeWidth = 1.8f * s,
                        cap = StrokeCap.Round
                    )
                }
            }
            NailStyle.CURVED -> {
                // Curved fighting spur (Aseel)
                val spurPath = Path().apply {
                    moveTo(x - bodyW * 0.15f - 3f * s, midY + 2f * s)
                    quadraticBezierTo(
                        x - bodyW * 0.15f - 10f * s, midY - 2f * s,
                        x - bodyW * 0.15f - 8f * s, midY - 6f * s
                    )
                }
                drawPath(spurPath, nailColor, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))

                val spurPath2 = Path().apply {
                    moveTo(x + bodyW * 0.15f + 3f * s, midY + 2f * s)
                    quadraticBezierTo(
                        x + bodyW * 0.15f + 10f * s, midY - 2f * s,
                        x + bodyW * 0.15f + 8f * s, midY - 6f * s
                    )
                }
                drawPath(spurPath2, nailColor, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))
            }
            else -> { /* SHORT or NONE - no visible spurs */ }
        }
    }

    // ==================== PLUMAGE PATTERN ====================

    private fun DrawScope.drawPlumagePattern(
        px: Float, py: Float, pw: Float, ph: Float,
        pattern: PlumagePattern, baseColor: Color, s: Float
    ) {
        when (pattern) {
            PlumagePattern.BARRED -> {
                // Alternating stripes
                val stripeCount = 4
                for (i in 0 until stripeCount) {
                    val stripeY = py + (i + 0.5f) * ph / stripeCount
                    drawLine(
                        color = Color.White.copy(alpha = 0.25f),
                        start = Offset(px + pw * 0.1f, stripeY),
                        end = Offset(px + pw * 0.9f, stripeY),
                        strokeWidth = 1.5f * s
                    )
                }
            }
            PlumagePattern.SPECKLED -> {
                // Random spots
                val random = java.util.Random((px * 100 + py * 10).toLong())
                for (i in 0..6) {
                    val sx = px + random.nextFloat() * pw
                    val sy = py + random.nextFloat() * ph
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = (1.5f + random.nextFloat() * 1.5f) * s,
                        center = Offset(sx, sy)
                    )
                }
            }
            PlumagePattern.LACED -> {
                // Scallop pattern (feather edges)
                val rows = 3
                for (r in 0 until rows) {
                    val rowY = py + ph * (0.2f + r * 0.25f)
                    for (c in 0..3) {
                        val cx = px + pw * (0.15f + c * 0.2f)
                        drawArc(
                            color = Color.Black.copy(alpha = 0.2f),
                            startAngle = 0f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(cx, rowY),
                            size = Size(pw * 0.15f, ph * 0.12f),
                            style = Stroke(width = 1f * s)
                        )
                    }
                }
            }
            PlumagePattern.COLUMBIAN -> {
                // Dark neck/tail contrast
                drawOval(
                    color = Color.Black.copy(alpha = 0.15f),
                    topLeft = Offset(px + pw * 0.3f, py),
                    size = Size(pw * 0.4f, ph * 0.3f)
                )
            }
            PlumagePattern.MOTTLED -> {
                // Irregular dark patches
                val random = java.util.Random((px * 50 + py * 20).toLong())
                for (i in 0..4) {
                    drawOval(
                        color = Color.Black.copy(alpha = 0.2f),
                        topLeft = Offset(px + random.nextFloat() * pw * 0.8f, py + random.nextFloat() * ph * 0.8f),
                        size = Size((3f + random.nextFloat() * 5f) * s, (2f + random.nextFloat() * 3f) * s)
                    )
                }
            }
            PlumagePattern.PENCILED -> {
                // Fine concentric lines
                for (i in 0..3) {
                    drawOval(
                        color = Color.Black.copy(alpha = 0.12f),
                        topLeft = Offset(px + pw * 0.2f + i * 2f, py + ph * 0.2f + i * 2f),
                        size = Size(pw * 0.5f - i * 4f, ph * 0.4f - i * 4f),
                        style = Stroke(width = 0.5f * s)
                    )
                }
            }
            PlumagePattern.DOUBLE_LACED -> {
                // Double ring pattern (like Barnevelder)
                for (r in 0..2) {
                    val rowY = py + ph * (0.2f + r * 0.25f)
                    for (c in 0..2) {
                        val cx = px + pw * (0.2f + c * 0.25f)
                        drawOval(
                            color = Color.Black.copy(alpha = 0.15f),
                            topLeft = Offset(cx, rowY),
                            size = Size(pw * 0.12f, ph * 0.1f),
                            style = Stroke(width = 1f * s)
                        )
                        drawOval(
                            color = Color.Black.copy(alpha = 0.1f),
                            topLeft = Offset(cx + 1f, rowY + 1f),
                            size = Size(pw * 0.1f, ph * 0.08f),
                            style = Stroke(width = 0.5f * s)
                        )
                    }
                }
            }
            PlumagePattern.SPLASH -> {
                // Random blue-gray splashes
                val random = java.util.Random((px * 70 + py * 30).toLong())
                for (i in 0..5) {
                    drawOval(
                        color = Color(0xFF90A4AE).copy(alpha = 0.25f),
                        topLeft = Offset(px + random.nextFloat() * pw * 0.7f, py + random.nextFloat() * ph * 0.7f),
                        size = Size((4f + random.nextFloat() * 6f) * s, (3f + random.nextFloat() * 4f) * s)
                    )
                }
            }
            else -> { /* SOLID, BUFF: no pattern overlay */ }
        }
    }

    // ==================== NECK ====================

    private fun DrawScope.drawNeck(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s
        val headY = y - bodyH * 1.8f
        val neckColor = a.chestColor.color

        val neckThickness = when (a.neck) {
            NeckStyle.SHORT -> 5f * s
            NeckStyle.MEDIUM -> 4f * s
            NeckStyle.LONG -> 3.5f * s
            NeckStyle.ARCHED -> 3.5f * s
            NeckStyle.MUSCULAR -> 6f * s
            NeckStyle.HACKLE_HEAVY -> 5.5f * s
        }
        val neckTopY = headY + bodyW * 0.42f * 0.5f
        val neckBottomY = y - bodyH * 1.0f

        when (a.neck) {
            NeckStyle.ARCHED -> {
                val neckPath = Path().apply {
                    moveTo(x + bodyW * 0.35f - neckThickness / 2f, neckTopY)
                    cubicTo(
                        x + bodyW * 0.5f, neckTopY + (neckBottomY - neckTopY) * 0.3f,
                        x + bodyW * 0.3f, neckTopY + (neckBottomY - neckTopY) * 0.7f,
                        x + bodyW * 0.15f, neckBottomY
                    )
                    lineTo(x + bodyW * 0.15f + neckThickness, neckBottomY)
                    cubicTo(
                        x + bodyW * 0.3f + neckThickness, neckTopY + (neckBottomY - neckTopY) * 0.7f,
                        x + bodyW * 0.5f + neckThickness / 2f, neckTopY + (neckBottomY - neckTopY) * 0.3f,
                        x + bodyW * 0.35f + neckThickness / 2f, neckTopY
                    )
                    close()
                }
                drawPath(neckPath, neckColor.copy(alpha = 0.85f))
            }
            NeckStyle.HACKLE_HEAVY -> {
                drawOval(
                    color = neckColor.copy(alpha = 0.8f),
                    topLeft = Offset(x + bodyW * 0.15f, neckTopY),
                    size = Size(neckThickness * 1.5f, neckBottomY - neckTopY)
                )
                for (i in 0..4) {
                    val lineY = neckTopY + (neckBottomY - neckTopY) * (i * 0.2f)
                    drawLine(
                        color = neckColor.copy(alpha = 0.4f),
                        start = Offset(x + bodyW * 0.25f, lineY),
                        end = Offset(x + bodyW * 0.15f - 2f * s, lineY + 5f * s),
                        strokeWidth = 1f * s,
                        cap = StrokeCap.Round
                    )
                }
            }
            NeckStyle.MUSCULAR -> {
                drawOval(
                    color = neckColor.copy(alpha = 0.85f),
                    topLeft = Offset(x + bodyW * 0.1f, neckTopY),
                    size = Size(neckThickness * 1.8f, neckBottomY - neckTopY)
                )
            }
            else -> {
                val length = when (a.neck) {
                    NeckStyle.LONG -> 1.3f
                    NeckStyle.SHORT -> 0.6f
                    else -> 1.0f
                }
                if (length > 0.3f) {
                    val adjustedTopY = neckBottomY - (neckBottomY - neckTopY) * length
                    drawOval(
                        color = neckColor.copy(alpha = 0.75f),
                        topLeft = Offset(x + bodyW * 0.2f, adjustedTopY),
                        size = Size(neckThickness * 1.2f, neckBottomY - adjustedTopY)
                    )
                }
            }
        }
    }

    // ==================== SHEEN OVERLAY ====================

    private fun DrawScope.drawSheen(x: Float, y: Float, s: Float, a: BirdAppearance) {
        val bodyW = 16f * s
        val bodyH = 12f * s

        when (a.sheen) {
            Sheen.SATIN -> {
                drawOval(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(x + bodyW * 0.1f, y - bodyH * 0.8f),
                        radius = bodyW * 0.8f
                    ),
                    topLeft = Offset(x - bodyW * 0.5f, y - bodyH * 1.6f),
                    size = Size(bodyW * 1.5f, bodyH * 1.8f)
                )
            }
            Sheen.GLOSSY -> {
                drawOval(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                        center = Offset(x - bodyW * 0.1f, y - bodyH * 1.2f),
                        radius = bodyW * 0.5f
                    ),
                    topLeft = Offset(x - bodyW * 0.6f, y - bodyH * 1.5f),
                    size = Size(bodyW * 1f, bodyH * 0.8f)
                )
            }
            Sheen.METALLIC -> {
                drawOval(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset(x - bodyW * 0.2f, y - bodyH * 1.3f),
                        radius = bodyW * 0.4f
                    ),
                    topLeft = Offset(x - bodyW * 0.6f, y - bodyH * 1.5f),
                    size = Size(bodyW * 0.8f, bodyH * 0.6f)
                )
                drawOval(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(x + bodyW * 0.3f, y - bodyH * 0.6f),
                        radius = bodyW * 0.3f
                    ),
                    topLeft = Offset(x + bodyW * 0.1f, y - bodyH * 0.8f),
                    size = Size(bodyW * 0.5f, bodyH * 0.4f)
                )
            }
            Sheen.IRIDESCENT -> {
                drawOval(
                    brush = Brush.radialGradient(
                        listOf(Color(0x251B5E20), Color(0x15673AB7), Color.Transparent),
                        center = Offset(x, y - bodyH * 1f),
                        radius = bodyW * 0.9f
                    ),
                    topLeft = Offset(x - bodyW * 0.8f, y - bodyH * 1.6f),
                    size = Size(bodyW * 1.8f, bodyH * 1.8f)
                )
            }
            Sheen.SILKY -> {
                val random = java.util.Random(42L)
                for (i in 0..8) {
                    val dotX = x - bodyW * 0.5f + random.nextFloat() * bodyW * 1.5f
                    val dotY = y - bodyH * 1.5f + random.nextFloat() * bodyH * 1.5f
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = (2f + random.nextFloat() * 3f) * s,
                        center = Offset(dotX, dotY)
                    )
                }
            }
            Sheen.MATTE -> { /* No sheen overlay */ }
        }
    }
}
