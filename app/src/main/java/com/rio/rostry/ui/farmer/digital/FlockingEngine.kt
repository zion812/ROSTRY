package com.rio.rostry.ui.farmer.digital

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.rio.rostry.domain.model.DigitalFarmZone
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * FlockingEngine: Implements the Boids algorithm for realistic bird movement.
 * 
 * Three core behaviors:
 * 1. Separation - Avoid crowding neighbors
 * 2. Alignment - Steer towards average heading of neighbors
 * 3. Cohesion - Steer towards center of mass of neighbors
 * 
 * Plus zone constraints to keep birds in their designated areas.
 */
class FlockingEngine(
    private val separationWeight: Float = 1.5f,
    private val alignmentWeight: Float = 1.0f,
    private val cohesionWeight: Float = 1.0f,
    private val maxSpeed: Float = 0.15f,
    private val perceptionRadius: Float = 3f // Grid units
) {
    
    data class Boid(
        val id: String,
        var position: Offset,
        var velocity: Offset = Offset.Zero,
        val zone: DigitalFarmZone = DigitalFarmZone.FREE_RANGE
    )
    
    /**
     * Updates all boids for one frame using the Boids algorithm.
     * Call this in an animation loop (e.g., 60fps).
     */
    fun update(boids: List<Boid>, deltaTime: Float = 1f): List<Boid> {
        return boids.map { boid ->
            val neighbors = getNeighbors(boid, boids)
            
            // Apply flocking rules
            val separation = separate(boid, neighbors) * separationWeight
            val alignment = align(boid, neighbors) * alignmentWeight
            val cohesion = cohere(boid, neighbors) * cohesionWeight
            
            // Zone constraint - keep birds in their zones
            val zoneForce = constrainToZone(boid)
            
            // Random wander to prevent stagnation
            val wander = randomWander() * 0.3f
            
            // Calculate acceleration
            val acceleration = separation + alignment + cohesion + zoneForce + wander
            
            // Update velocity
            var newVelocity = boid.velocity + acceleration * deltaTime
            newVelocity = limitMagnitude(newVelocity, maxSpeed)
            
            // Update position
            val newPosition = boid.position + newVelocity
            
            boid.copy(
                position = Offset(
                    newPosition.x.coerceIn(0f, IsometricGrid.GRID_SIZE.toFloat() - 1),
                    newPosition.y.coerceIn(0f, IsometricGrid.GRID_SIZE.toFloat() - 1)
                ),
                velocity = newVelocity
            )
        }
    }
    
    /**
     * Get neighbors within perception radius.
     */
    private fun getNeighbors(boid: Boid, all: List<Boid>): List<Boid> {
        return all.filter { other ->
            other.id != boid.id && 
            distance(boid.position, other.position) < perceptionRadius
        }
    }
    
    /**
     * Separation: Steer away from nearby boids to avoid crowding.
     */
    private fun separate(boid: Boid, neighbors: List<Boid>): Offset {
        if (neighbors.isEmpty()) return Offset.Zero
        
        var steer = Offset.Zero
        var count = 0
        
        neighbors.forEach { other ->
            val d = distance(boid.position, other.position)
            if (d > 0 && d < perceptionRadius * 0.5f) {
                val diff = (boid.position - other.position).normalized() / d
                steer += diff
                count++
            }
        }
        
        return if (count > 0) steer / count.toFloat() else Offset.Zero
    }
    
    /**
     * Alignment: Steer towards average velocity of neighbors.
     */
    private fun align(boid: Boid, neighbors: List<Boid>): Offset {
        if (neighbors.isEmpty()) return Offset.Zero
        
        var avgVelocity = Offset.Zero
        neighbors.forEach { avgVelocity += it.velocity }
        avgVelocity /= neighbors.size.toFloat()
        
        return (avgVelocity - boid.velocity) * 0.1f
    }
    
    /**
     * Cohesion: Steer towards center of mass of neighbors.
     */
    private fun cohere(boid: Boid, neighbors: List<Boid>): Offset {
        if (neighbors.isEmpty()) return Offset.Zero
        
        var center = Offset.Zero
        neighbors.forEach { center += it.position }
        center /= neighbors.size.toFloat()
        
        return (center - boid.position) * 0.01f
    }
    
    /**
     * Zone constraint: Generate force to keep birds in their zones.
     */
    private fun constrainToZone(boid: Boid): Offset {
        val (xRange, yRange) = getZoneBounds(boid.zone)
        var force = Offset.Zero
        val margin = 1f
        val strength = 0.2f
        
        // Push away from zone boundaries
        if (boid.position.x < xRange.first + margin) {
            force += Offset(strength, 0f)
        }
        if (boid.position.x > xRange.second - margin) {
            force += Offset(-strength, 0f)
        }
        if (boid.position.y < yRange.first + margin) {
            force += Offset(0f, strength)
        }
        if (boid.position.y > yRange.second - margin) {
            force += Offset(0f, -strength)
        }
        
        return force
    }
    
    /**
     * Get the bounds for each zone.
     */
    private fun getZoneBounds(zone: DigitalFarmZone): Pair<Pair<Float, Float>, Pair<Float, Float>> {
        return when (zone) {
            DigitalFarmZone.NURSERY -> (0f to 9f) to (0f to 4f)
            DigitalFarmZone.BREEDING_UNIT -> (10f to 19f) to (0f to 4f)
            DigitalFarmZone.FREE_RANGE -> (0f to 19f) to (5f to 14f)
            DigitalFarmZone.GROW_OUT -> (0f to 19f) to (10f to 14f)
            DigitalFarmZone.READY_DISPLAY -> (3f to 7f) to (8f to 12f)
            DigitalFarmZone.MARKET_STAND -> (5f to 15f) to (15f to 19f)
        }
    }
    
    /**
     * Random wander for more natural movement.
     */
    private fun randomWander(): Offset {
        return Offset(
            Random.nextFloat() * 0.1f - 0.05f,
            Random.nextFloat() * 0.1f - 0.05f
        )
    }
    
    /**
     * Calculate distance between two points.
     */
    private fun distance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }
    
    /**
     * Limit vector magnitude.
     */
    private fun limitMagnitude(v: Offset, max: Float): Offset {
        val mag = sqrt(v.x * v.x + v.y * v.y)
        return if (mag > max) v * (max / mag) else v
    }
    
    /**
     * Normalize an offset vector.
     */
    private fun Offset.normalized(): Offset {
        val mag = sqrt(x * x + y * y)
        return if (mag > 0) this / mag else Offset.Zero
    }
}

/**
 * Composable effect that runs the flocking simulation.
 * @param boids Initial list of boids to simulate
 * @param onUpdate Callback with updated boid positions each frame
 * @param frameRate Target frames per second (default 30)
 */
@Composable
fun FlockingEffect(
    boids: List<FlockingEngine.Boid>,
    engine: FlockingEngine = remember { FlockingEngine() },
    frameRate: Int = 30,
    isPaused: Boolean = false,
    onUpdate: (List<FlockingEngine.Boid>) -> Unit
) {
    var currentBoids by remember(boids) { mutableStateOf(boids) }
    
    LaunchedEffect(isPaused) {
        if (isPaused) return@LaunchedEffect
        
        val frameDelayMs = 1000L / frameRate
        while (true) {
            delay(frameDelayMs)
            currentBoids = engine.update(currentBoids)
            onUpdate(currentBoids)
        }
    }
}

/**
 * Convert VisualBird list to Boid list for flocking simulation.
 */
fun List<com.rio.rostry.domain.model.VisualBird>.toBoids(): List<FlockingEngine.Boid> {
    return map { bird ->
        FlockingEngine.Boid(
            id = bird.productId,
            position = bird.position,
            zone = bird.zone
        )
    }
}
