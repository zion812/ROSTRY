package com.rio.rostry.ui.farmer.digital

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.rio.rostry.domain.model.DigitalFarmZone
import com.rio.rostry.domain.model.VisualBird
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * BirdAnimationController: Manages autonomous pecking/wandering animations for birds.
 *
 * Each bird has an Animatable offset that moves to random positions within its zone
 * every 3-5 seconds, creating a "pecking/wandering" behavior.
 */
class BirdAnimationController {

    // TwoWayConverter for Offset <-> AnimationVector2D
    private val offsetConverter = TwoWayConverter<Offset, AnimationVector2D>(
        convertToVector = { offset -> AnimationVector2D(offset.x, offset.y) },
        convertFromVector = { vector -> Offset(vector.v1, vector.v2) }
    )

    // Map of birdId to their animation state
    private val animationStates = mutableStateMapOf<String, Animatable<Offset, AnimationVector2D>>()

    /**
     * Get or create an Animatable for a bird.
     */
    fun getOrCreateAnimatable(birdId: String, initialPosition: Offset): Animatable<Offset, AnimationVector2D> {
        return animationStates.getOrPut(birdId) {
            Animatable(initialPosition, offsetConverter)
        }
    }

    /**
     * Get current animated position for a bird.
     */
    fun getAnimatedPosition(birdId: String): Offset? {
        return animationStates[birdId]?.value
    }

    /**
     * Animate a bird to a new target position.
     */
    suspend fun animateTo(birdId: String, targetPosition: Offset, durationMs: Int = 2000) {
        animationStates[birdId]?.animateTo(
            targetPosition,
            animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing)
        )
    }

    /**
     * Stop animation for a bird (e.g., when dragging).
     */
    fun stopAnimation(birdId: String) {
        // The animation will naturally pause when no new animateTo is called
    }

    /**
     * Remove a bird's animation state (e.g., on removal from farm).
     */
    fun removeAnimation(birdId: String) {
        animationStates.remove(birdId)
    }

    /**
     * Clear all animations.
     */
    fun clearAll() {
        animationStates.clear()
    }
}

/**
 * Composable effect that triggers pecking animations for a list of birds.
 */
@Composable
fun BirdPeckingEffect(
    birds: List<VisualBird>,
    controller: BirdAnimationController,
    isPaused: Boolean = false
) {
    birds.forEach { bird ->
        val animatable = remember(bird.productId) {
            controller.getOrCreateAnimatable(bird.productId, bird.position)
        }

        LaunchedEffect(bird.productId, bird.zone, isPaused) {
            if (isPaused) return@LaunchedEffect

            while (true) {
                // Random delay between 3-5 seconds
                delay(Random.nextLong(3000, 5000))

                // Get a new target position within the bird's zone
                val targetGridPos = IsometricGrid.getRandomPositionInZone(bird.zone)
                
                // Animate to the new position
                controller.animateTo(bird.productId, targetGridPos, durationMs = Random.nextInt(1500, 2500))
            }
        }
    }
}

/**
 * Helper to calculate idle bobbing offset based on animation time.
 * Used during rendering for a subtle up/down movement.
 */
fun calculateIdleBobbing(animationTime: Float, birdIdHash: Int, amplitude: Float = 2f): Float {
    return kotlin.math.sin(animationTime * 0.002f + birdIdHash) * amplitude
}
