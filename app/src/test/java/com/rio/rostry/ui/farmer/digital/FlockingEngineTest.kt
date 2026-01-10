package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import com.rio.rostry.domain.model.DigitalFarmZone
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/**
 * Unit tests for FlockingEngine testing the Boids algorithm
 * for realistic bird movement simulation.
 */
class FlockingEngineTest {

    private val engine = FlockingEngine()

    @Test
    fun `boids stay within grid boundaries after updates`() {
        // Given
        val boids = listOf(
            FlockingEngine.Boid(
                id = "test_1",
                position = Offset(10f, 10f),
                velocity = Offset(0.1f, 0.1f),
                zone = DigitalFarmZone.FREE_RANGE
            ),
            FlockingEngine.Boid(
                id = "test_2",
                position = Offset(11f, 10f),
                velocity = Offset(-0.1f, 0.1f),
                zone = DigitalFarmZone.FREE_RANGE
            )
        )

        // When - simulate multiple updates
        var currentBoids = boids
        repeat(100) {
            currentBoids = engine.update(currentBoids, deltaTime = 1f)
        }

        // Then - all boids should still be within grid bounds
        currentBoids.forEach { boid ->
            assertThat(boid.position.x).isAtLeast(0f)
            assertThat(boid.position.x).isLessThan(IsometricGrid.GRID_SIZE.toFloat())
            assertThat(boid.position.y).isAtLeast(0f)
            assertThat(boid.position.y).isLessThan(IsometricGrid.GRID_SIZE.toFloat())
        }
    }

    @Test
    fun `boids velocity is limited by max speed`() {
        // Given - boid with high velocity
        val boid = FlockingEngine.Boid(
            id = "fast",
            position = Offset(10f, 10f),
            velocity = Offset(10f, 10f), // Very fast (should be limited)
            zone = DigitalFarmZone.FREE_RANGE
        )

        // When
        val updated = engine.update(listOf(boid), deltaTime = 1f)

        // Then - velocity magnitude should be limited
        val speed = kotlin.math.sqrt(
            updated[0].velocity.x * updated[0].velocity.x +
            updated[0].velocity.y * updated[0].velocity.y
        )
        // Max speed is 0.15f by default
        assertThat(speed).isLessThan(1f)
    }

    @Test
    fun `update returns same number of boids`() {
        // Given
        val boids = (1..10).map { i ->
            FlockingEngine.Boid(
                id = "boid_$i",
                position = Offset(5f + i, 10f),
                velocity = Offset.Zero,
                zone = DigitalFarmZone.FREE_RANGE
            )
        }

        // When
        val updated = engine.update(boids)

        // Then
        assertThat(updated.size).isEqualTo(boids.size)
    }

    @Test
    fun `boids maintain their IDs after update`() {
        // Given
        val boids = listOf(
            FlockingEngine.Boid(
                id = "unique_id_1",
                position = Offset(5f, 5f),
                velocity = Offset.Zero,
                zone = DigitalFarmZone.NURSERY
            ),
            FlockingEngine.Boid(
                id = "unique_id_2",
                position = Offset(6f, 5f),
                velocity = Offset.Zero,
                zone = DigitalFarmZone.NURSERY
            )
        )

        // When
        val updated = engine.update(boids)

        // Then
        assertThat(updated.map { it.id }).containsExactly("unique_id_1", "unique_id_2")
    }

    @Test
    fun `boids maintain their zone after update`() {
        // Given
        val boid = FlockingEngine.Boid(
            id = "zone_test",
            position = Offset(15f, 2f),
            velocity = Offset.Zero,
            zone = DigitalFarmZone.BREEDING_UNIT
        )

        // When
        val updated = engine.update(listOf(boid))

        // Then
        assertThat(updated[0].zone).isEqualTo(DigitalFarmZone.BREEDING_UNIT)
    }

    @Test
    fun `empty boid list returns empty list`() {
        // Given
        val emptyList = emptyList<FlockingEngine.Boid>()

        // When
        val updated = engine.update(emptyList)

        // Then
        assertThat(updated).isEmpty()
    }

    @Test
    fun `single boid still updates position`() {
        // Given - single boid with velocity
        val boid = FlockingEngine.Boid(
            id = "single",
            position = Offset(10f, 10f),
            velocity = Offset(0.1f, 0.05f),
            zone = DigitalFarmZone.FREE_RANGE
        )
        val initialX = boid.position.x
        val initialY = boid.position.y

        // When
        val updated = engine.update(listOf(boid), deltaTime = 1f)

        // Then - position should change (due to velocity and random wander)
        // Note: Position will change due to wander even without neighbors
        assertThat(updated[0].position).isNotEqualTo(Offset(initialX, initialY))
    }
}
