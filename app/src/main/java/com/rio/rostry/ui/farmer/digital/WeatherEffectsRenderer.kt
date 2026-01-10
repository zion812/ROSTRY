package com.rio.rostry.ui.farmer.digital

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * WeatherEffectsRenderer: Renders dynamic weather effects on the Digital Farm canvas.
 * 
 * Supports:
 * - Rain with wind angle
 * - Sun rays (volumetric effect)
 * - Cloud shadows
 * - Fog overlay
 * 
 * Optimized for low-end devices with configurable particle counts.
 */
object WeatherEffectsRenderer {
    
    // Configuration for performance tuning
    private const val MAX_RAIN_PARTICLES = 80
    private const val MAX_CLOUD_SHADOWS = 3
    
    data class RainParticle(
        var x: Float,
        var y: Float,
        val speed: Float,
        val length: Float,
        val alpha: Float = Random.nextFloat() * 0.3f + 0.4f
    )
    
    /**
     * Weather state for rendering.
     */
    data class WeatherState(
        val condition: WeatherCondition = WeatherCondition.CLEAR,
        val intensity: Float = 0.5f, // 0 to 1
        val windAngle: Float = 0.1f, // Radians
        val animationTime: Float = 0f
    )
    
    enum class WeatherCondition {
        CLEAR,
        CLOUDY,
        RAIN,
        HEAVY_RAIN,
        FOG
    }
    
    // Rain particles storage
    private var rainParticles = mutableListOf<RainParticle>()
    private var initialized = false
    
    /**
     * Initialize rain particles for the given canvas size.
     */
    fun initializeRain(canvasWidth: Float, canvasHeight: Float, intensity: Float = 0.5f) {
        val particleCount = (MAX_RAIN_PARTICLES * intensity).toInt().coerceAtLeast(10)
        rainParticles = (0 until particleCount).map {
            RainParticle(
                x = Random.nextFloat() * canvasWidth,
                y = Random.nextFloat() * canvasHeight,
                speed = 8f + Random.nextFloat() * 12f,
                length = 15f + Random.nextFloat() * 25f
            )
        }.toMutableList()
        initialized = true
    }
    
    /**
     * Render all weather effects based on current state.
     */
    fun DrawScope.renderWeather(
        state: WeatherState,
        canvasWidth: Float = size.width,
        canvasHeight: Float = size.height
    ) {
        when (state.condition) {
            WeatherCondition.CLEAR -> {
                renderSunRays(
                    sunPosition = Offset(canvasWidth * 0.85f, canvasHeight * 0.1f),
                    intensity = 0.8f
                )
            }
            WeatherCondition.CLOUDY -> {
                renderCloudShadows(state.animationTime, canvasWidth, canvasHeight)
            }
            WeatherCondition.RAIN -> {
                if (!initialized || rainParticles.isEmpty()) {
                    initializeRain(canvasWidth, canvasHeight, state.intensity)
                }
                renderRain(state.windAngle, canvasWidth, canvasHeight)
                renderCloudOverlay(0.2f * state.intensity)
            }
            WeatherCondition.HEAVY_RAIN -> {
                if (!initialized || rainParticles.isEmpty()) {
                    initializeRain(canvasWidth, canvasHeight, 1f)
                }
                renderRain(state.windAngle, canvasWidth, canvasHeight)
                renderCloudOverlay(0.4f)
            }
            WeatherCondition.FOG -> {
                renderFog(state.intensity, state.animationTime)
            }
        }
    }
    
    /**
     * Render rain particles with wind angle.
     */
    private fun DrawScope.renderRain(
        windAngle: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        val rainColor = Color(0xAAA0C4E8)
        
        rainParticles.forEach { particle ->
            // Update position
            particle.y += particle.speed
            particle.x += particle.speed * windAngle
            
            // Wrap around
            if (particle.y > canvasHeight) {
                particle.y = -particle.length
                particle.x = Random.nextFloat() * canvasWidth
            }
            if (particle.x < -20f) {
                particle.x = canvasWidth + 10f
            }
            if (particle.x > canvasWidth + 20f) {
                particle.x = -10f
            }
            
            // Draw rain drop
            drawLine(
                color = rainColor.copy(alpha = particle.alpha),
                start = Offset(particle.x, particle.y),
                end = Offset(
                    particle.x + particle.length * windAngle * 0.5f,
                    particle.y + particle.length
                ),
                strokeWidth = 2f
            )
        }
    }
    
    /**
     * Render sun rays emanating from a position.
     */
    private fun DrawScope.renderSunRays(
        sunPosition: Offset,
        rayCount: Int = 12,
        intensity: Float = 1f
    ) {
        val rayLength = size.height * 1.2f
        
        repeat(rayCount) { i ->
            val angle = (i.toFloat() / rayCount) * Math.PI.toFloat() * 0.6f - 0.2f
            val endX = sunPosition.x + cos(angle) * rayLength
            val endY = sunPosition.y + sin(angle) * rayLength
            
            // Create gradient for volumetric effect
            val rayBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0x30FFEE00),
                    Color.Transparent
                ),
                start = sunPosition,
                end = Offset(endX, endY)
            )
            
            // Draw ray as wide line
            rotate(degrees = 0f, pivot = sunPosition) {
                drawLine(
                    brush = rayBrush,
                    start = sunPosition,
                    end = Offset(endX, endY),
                    strokeWidth = 40f * intensity
                )
            }
        }
        
        // Draw sun glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x60FFEE00),
                    Color(0x20FFEE00),
                    Color.Transparent
                ),
                center = sunPosition,
                radius = 80f
            ),
            center = sunPosition,
            radius = 100f
        )
    }
    
    /**
     * Render moving cloud shadows.
     */
    private fun DrawScope.renderCloudShadows(
        animationTime: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ) {
        val shadowColor = Color(0x12000000)
        
        repeat(MAX_CLOUD_SHADOWS) { i ->
            val baseX = (animationTime * 0.3f + i * canvasWidth / 3f) % (canvasWidth + 300f) - 150f
            val y = canvasHeight * (0.25f + i * 0.2f)
            val width = 180f + i * 40f
            val height = 50f + i * 15f
            
            // Draw elliptical shadow
            drawOval(
                color = shadowColor,
                topLeft = Offset(baseX, y),
                size = Size(width, height)
            )
        }
    }
    
    /**
     * Render cloud/overcast overlay.
     */
    private fun DrawScope.renderCloudOverlay(darkness: Float) {
        drawRect(
            color = Color.Black.copy(alpha = darkness.coerceIn(0f, 0.5f)),
            topLeft = Offset.Zero,
            size = size
        )
    }
    
    /**
     * Render fog effect.
     */
    private fun DrawScope.renderFog(intensity: Float, animationTime: Float) {
        // Multiple fog layers for depth
        repeat(3) { layer ->
            val layerAlpha = (intensity * 0.15f * (layer + 1)).coerceIn(0f, 0.4f)
            val yOffset = (animationTime * 0.05f * (layer + 1)) % 50f
            
            val fogBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = layerAlpha * 0.5f),
                    Color.White.copy(alpha = layerAlpha),
                    Color.White.copy(alpha = layerAlpha * 0.3f)
                ),
                startY = size.height * 0.4f + yOffset,
                endY = size.height
            )
            
            drawRect(
                brush = fogBrush,
                topLeft = Offset.Zero,
                size = size
            )
        }
    }
    
    /**
     * Reset particles when canvas size changes.
     */
    fun reset() {
        rainParticles.clear()
        initialized = false
    }
}

/**
 * Composable that provides animated weather state.
 */
@Composable
fun rememberWeatherState(
    condition: WeatherEffectsRenderer.WeatherCondition,
    intensity: Float = 0.5f,
    windAngle: Float = 0.1f
): WeatherEffectsRenderer.WeatherState {
    var animationTime by remember { mutableStateOf(0f) }
    
    LaunchedEffect(condition) {
        while (true) {
            delay(16L) // ~60fps
            animationTime += 16f
        }
    }
    
    return remember(condition, intensity, windAngle, animationTime) {
        WeatherEffectsRenderer.WeatherState(
            condition = condition,
            intensity = intensity,
            windAngle = windAngle,
            animationTime = animationTime
        )
    }
}
