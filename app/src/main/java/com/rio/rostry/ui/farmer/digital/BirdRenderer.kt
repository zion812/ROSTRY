package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

/**
 * BirdRenderer: Renders birds with health indicators and animations on the Digital Farm canvas.
 * 
 * Features:
 * - Health-based color coding (Healthy=Green, Sick=Red, Recovering=Yellow, Quarantined=Orange)
 * - Pulsing animation for sick birds
 * - Directional facing based on velocity
 * - Selection highlight
 */
object BirdRenderer {
    
    // Health status colors
    private val healthColors = mapOf(
        "HEALTHY" to Color(0xFF4CAF50),      // Green
        "SICK" to Color(0xFFF44336),          // Red
        "RECOVERING" to Color(0xFFFFEB3B),    // Yellow
        "QUARANTINED" to Color(0xFFFF6B35),   // Orange
        "UNKNOWN" to Color(0xFF9E9E9E)        // Gray
    )
    
    // Bird size based on lifecycle stage
    private val stageSizes = mapOf(
        "CHICK" to 6f,
        "GROWER" to 8f,
        "ADULT" to 10f,
        "LAYER" to 10f,
        "BREEDER" to 12f
    )
    
    data class BirdRenderState(
        val id: String,
        val position: Offset,
        val healthStatus: String = "HEALTHY",
        val lifecycleStage: String = "ADULT",
        val velocity: Offset = Offset.Zero,
        val isSelected: Boolean = false,
        val isQuarantined: Boolean = false
    )
    
    /**
     * Render all birds on the canvas.
     */
    fun DrawScope.renderBirds(
        birds: List<BirdRenderState>,
        animationTime: Long = System.currentTimeMillis()
    ) {
        birds.forEach { bird ->
            renderBird(bird, animationTime)
        }
    }
    
    /**
     * Render a single bird with health indicators.
     */
    fun DrawScope.renderBird(
        bird: BirdRenderState,
        animationTime: Long = System.currentTimeMillis()
    ) {
        val baseColor = healthColors[bird.healthStatus] ?: healthColors["UNKNOWN"]!!
        val size = stageSizes[bird.lifecycleStage] ?: 8f
        
        // Calculate facing direction from velocity
        val facingAngle = if (bird.velocity != Offset.Zero) {
            kotlin.math.atan2(bird.velocity.y.toDouble(), bird.velocity.x.toDouble()).toFloat()
        } else {
            0f
        }
        
        // Pulsing effect for sick birds
        val pulseAlpha = if (bird.healthStatus == "SICK" || bird.isQuarantined) {
            (sin(animationTime / 300.0) * 0.3 + 0.7).toFloat()
        } else {
            1f
        }
        
        // Selection highlight ring
        if (bird.isSelected) {
            drawCircle(
                color = Color.White,
                center = bird.position,
                radius = size + 6f,
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = Color(0xFF2196F3),
                center = bird.position,
                radius = size + 4f,
                style = Stroke(width = 2f)
            )
        }
        
        // Quarantine border
        if (bird.isQuarantined) {
            drawCircle(
                color = Color.Red.copy(alpha = 0.5f * pulseAlpha),
                center = bird.position,
                radius = size + 4f,
                style = Stroke(width = 2f)
            )
        }
        
        // Pulsing outer ring for sick birds
        if (bird.healthStatus == "SICK") {
            drawCircle(
                color = baseColor.copy(alpha = 0.3f * pulseAlpha),
                center = bird.position,
                radius = size + 4f
            )
        }
        
        // Main bird body (circle)
        drawCircle(
            color = baseColor.copy(alpha = pulseAlpha),
            center = bird.position,
            radius = size
        )
        
        // Bird body outline
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            center = bird.position,
            radius = size,
            style = Stroke(width = 1f)
        )
        
        // Beak (pointing in facing direction)
        val beakLength = size * 0.6f
        val beakPath = Path().apply {
            moveTo(
                bird.position.x + kotlin.math.cos(facingAngle) * size,
                bird.position.y + kotlin.math.sin(facingAngle) * size
            )
            lineTo(
                bird.position.x + kotlin.math.cos(facingAngle) * (size + beakLength),
                bird.position.y + kotlin.math.sin(facingAngle) * (size + beakLength)
            )
            lineTo(
                bird.position.x + kotlin.math.cos(facingAngle + 0.3f) * size,
                bird.position.y + kotlin.math.sin(facingAngle + 0.3f) * size
            )
            close()
        }
        drawPath(
            path = beakPath,
            color = Color(0xFFFF9800) // Orange beak
        )
        
        // Eye
        val eyeOffset = Offset(
            kotlin.math.cos(facingAngle - 0.5f) * size * 0.5f,
            kotlin.math.sin(facingAngle - 0.5f) * size * 0.5f
        )
        drawCircle(
            color = Color.Black,
            center = bird.position + eyeOffset,
            radius = size * 0.15f
        )
        
        // Health indicator icon for sick/quarantined birds
        if (bird.healthStatus == "SICK" || bird.isQuarantined) {
            drawHealthIcon(bird.position, size, bird.healthStatus)
        }
    }
    
    /**
     * Draw a small health status icon above the bird.
     */
    private fun DrawScope.drawHealthIcon(
        position: Offset,
        birdSize: Float,
        healthStatus: String
    ) {
        val iconPosition = position + Offset(0f, -birdSize - 8f)
        val iconSize = 6f
        
        when (healthStatus) {
            "SICK" -> {
                // Red cross
                drawLine(
                    color = Color.Red,
                    start = iconPosition + Offset(-iconSize, -iconSize),
                    end = iconPosition + Offset(iconSize, iconSize),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Red,
                    start = iconPosition + Offset(iconSize, -iconSize),
                    end = iconPosition + Offset(-iconSize, iconSize),
                    strokeWidth = 2f
                )
            }
            "QUARANTINED" -> {
                // Warning triangle
                val trianglePath = Path().apply {
                    moveTo(iconPosition.x, iconPosition.y - iconSize)
                    lineTo(iconPosition.x + iconSize, iconPosition.y + iconSize)
                    lineTo(iconPosition.x - iconSize, iconPosition.y + iconSize)
                    close()
                }
                drawPath(
                    path = trianglePath,
                    color = Color(0xFFFF6B35)
                )
                // Exclamation mark
                drawLine(
                    color = Color.Black,
                    start = iconPosition + Offset(0f, -3f),
                    end = iconPosition + Offset(0f, 2f),
                    strokeWidth = 2f
                )
                drawCircle(
                    color = Color.Black,
                    center = iconPosition + Offset(0f, 5f),
                    radius = 1f
                )
            }
            "RECOVERING" -> {
                // Heart icon
                drawCircle(
                    color = Color(0xFFFFEB3B),
                    center = iconPosition,
                    radius = iconSize
                )
                drawCircle(
                    color = Color.White,
                    center = iconPosition,
                    radius = iconSize - 2f
                )
            }
        }
    }
    
    /**
     * Convert FarmAssetEntity to BirdRenderState.
     */
    fun com.rio.rostry.data.database.entity.FarmAssetEntity.toRenderState(
        position: Offset
    ): BirdRenderState {
        // Derive lifecycle stage from age weeks
        val stage = when {
            ageWeeks == null -> "ADULT"
            ageWeeks!! < 4 -> "CHICK"
            ageWeeks!! < 12 -> "GROWER"
            else -> "ADULT"
        }
        return BirdRenderState(
            id = assetId,
            position = position,
            healthStatus = healthStatus ?: "HEALTHY",
            lifecycleStage = stage,
            isQuarantined = status == "QUARANTINED"
        )
    }
}
