package com.rio.rostry.domain.lifecycle

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.LifecycleStage
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BirdLifecycleManager - Central authority for bird lifecycle state management.
 * 
 * Responsibilities:
 * - Calculate current lifecycle stage from birth date
 * - Determine when the next stage transition will occur
 * - Provide breed-specific lifecycle rules (extensible)
 * - Track lifecycle events for analytics
 * 
 * Usage:
 * ```kotlin
 * val manager = BirdLifecycleManager()
 * val updatedBird = manager.updateStage(bird)
 * val nextTransition = manager.getNextTransitionDate(bird)
 * ```
 */
@Singleton
class BirdLifecycleManager @Inject constructor() {

    companion object {
        // Standard lifecycle thresholds (in weeks)
        const val CHICK_MAX_WEEKS = 8
        const val GROWER_MAX_WEEKS = 18
        const val LAYER_MAX_WEEKS = 72 // ~18 months typical production lifespan
        
        // Gender-specific adult stages
        const val MALE_ADULT_STAGE = "ROOSTER"
        const val FEMALE_ADULT_STAGE = "LAYER"
        const val UNKNOWN_ADULT_STAGE = "ADULT"
    }

    /**
     * Calculates and returns the updated ProductEntity with current lifecycle stage.
     * Does NOT persist - caller must save the returned entity.
     * 
     * @param bird The bird entity to evaluate
     * @return Updated entity with stage, ageWeeks, and lastStageTransitionAt set
     */
    fun updateStage(bird: ProductEntity): ProductEntity {
        val birthDate = bird.birthDate ?: return bird // No birth date = can't calculate
        
        val ageWeeks = calculateAgeWeeks(birthDate)
        val newStage = determineStage(ageWeeks, bird.gender)
        
        val stageChanged = bird.stage != newStage
        
        return bird.copy(
            ageWeeks = ageWeeks,
            stage = newStage,
            lifecycleStatus = newStage.displayName,
            lastStageTransitionAt = if (stageChanged) System.currentTimeMillis() else bird.lastStageTransitionAt
        )
    }

    /**
     * Calculates age in weeks from birth date.
     */
    fun calculateAgeWeeks(birthDate: Long): Int {
        val now = System.currentTimeMillis()
        val diffMs = now - birthDate
        val days = TimeUnit.MILLISECONDS.toDays(diffMs).toInt()
        return days / 7
    }

    /**
     * Determines lifecycle stage based on age and gender.
     * 
     * Stages:
     * - CHICK: 0-8 weeks
     * - GROWER: 8-18 weeks
     * - LAYER/BREEDER: 18+ weeks (females)
     * - ROOSTER: 18+ weeks (males, implicit in LifecycleStage.BREEDER for now)
     */
    fun determineStage(ageWeeks: Int, gender: String?): LifecycleStage {
        return when {
            ageWeeks < CHICK_MAX_WEEKS -> LifecycleStage.CHICK
            ageWeeks < GROWER_MAX_WEEKS -> LifecycleStage.GROWER
            ageWeeks < LAYER_MAX_WEEKS -> LifecycleStage.LAYER
            else -> LifecycleStage.BREEDER
        }
    }

    /**
     * Returns the timestamp when the bird will transition to the next stage.
     * Returns null if the bird is at the final stage (BREEDER).
     */
    fun getNextTransitionDate(bird: ProductEntity): Long? {
        val birthDate = bird.birthDate ?: return null
        val ageWeeks = calculateAgeWeeks(birthDate)
        
        val nextStageWeeks = when {
            ageWeeks < CHICK_MAX_WEEKS -> CHICK_MAX_WEEKS
            ageWeeks < GROWER_MAX_WEEKS -> GROWER_MAX_WEEKS
            ageWeeks < LAYER_MAX_WEEKS -> LAYER_MAX_WEEKS
            else -> return null // Already at final stage
        }
        
        // Calculate the date when bird will reach nextStageWeeks
        val daysToNextStage = (nextStageWeeks * 7) - (ageWeeks * 7)
        return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(daysToNextStage.toLong())
    }

    /**
     * Checks if the bird is ready for breeding based on age and health.
     * Standard: 18+ weeks old with healthy status.
     */
    fun isBreedingEligible(bird: ProductEntity): Boolean {
        val ageWeeks = bird.ageWeeks ?: bird.birthDate?.let { calculateAgeWeeks(it) } ?: 0
        val healthOk = bird.healthStatus?.uppercase() in listOf("OK", "HEALTHY", null)
        return ageWeeks >= GROWER_MAX_WEEKS && healthOk
    }

    /**
     * Returns a human-readable age string (e.g., "12 weeks", "3 months", "1 year 2 months").
     */
    fun formatAge(birthDate: Long?): String {
        if (birthDate == null) return "Unknown age"
        
        val ageWeeks = calculateAgeWeeks(birthDate)
        return when {
            ageWeeks < 12 -> "$ageWeeks weeks"
            ageWeeks < 52 -> "${ageWeeks / 4} months"
            else -> {
                val years = ageWeeks / 52
                val months = (ageWeeks % 52) / 4
                if (months > 0) "$years year${if (years > 1) "s" else ""} $months month${if (months > 1) "s" else ""}"
                else "$years year${if (years > 1) "s" else ""}"
            }
        }
    }

    /**
     * Batch updates multiple birds' lifecycle stages.
     * Useful for daily background processing.
     */
    fun updateStages(birds: List<ProductEntity>): List<ProductEntity> {
        return birds.map { updateStage(it) }
    }

    /**
     * Returns birds that are about to transition (within next N days).
     */
    fun getBirdsNearingTransition(birds: List<ProductEntity>, withinDays: Int = 7): List<ProductEntity> {
        val threshold = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(withinDays.toLong())
        return birds.filter { bird ->
            val nextTransition = getNextTransitionDate(bird)
            nextTransition != null && nextTransition <= threshold
        }
    }
}
