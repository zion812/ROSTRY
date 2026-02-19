package com.rio.rostry.domain.digitaltwin

import com.rio.rostry.data.database.dao.BirdEventDao
import com.rio.rostry.data.database.dao.DigitalTwinDao
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🧬 AseelLifecycleEngine — 7-Stage Lifecycle State Machine for Aseel Birds
 *
 * Replaces the basic BirdLifecycleManager for Digital Twin birds.
 * Manages stage transitions with capability gates, event logging, 
 * and score impact tracking.
 *
 * State Machine Responsibilities:
 * 1. Calculate current stage from birth date + gender
 * 2. Detect stage transitions and log them as events
 * 3. Enforce capability gates (what traits can be measured at each stage)
 * 4. Predict next transition date
 * 5. Apply decline factors for Senior birds
 *
 * Usage:
 * ```kotlin
 * val engine = AseelLifecycleEngine(twinDao, eventDao)
 * val updatedTwin = engine.updateLifecycle(twin)
 * val nextTransition = engine.getNextTransitionInfo(twin)
 * ```
 */
@Singleton
class AseelLifecycleEngine @Inject constructor(
    private val twinDao: DigitalTwinDao,
    private val eventDao: BirdEventDao
) {

    /**
     * Updates a bird's lifecycle stage based on current age.
     * If a stage transition occurs, logs it as a BirdEvent.
     *
     * @param twin The Digital Twin to evaluate
     * @return Updated entity (caller must decide whether to persist)
     */
    suspend fun updateLifecycle(twin: DigitalTwinEntity): DigitalTwinEntity {
        val birthDate = twin.birthDate ?: return twin

        val now = System.currentTimeMillis()
        val ageDays = TimeUnit.MILLISECONDS.toDays(now - birthDate).toInt()
        val isMale = twin.gender?.uppercase() != "FEMALE"

        val newStage = AseelLifecycleStage.fromAgeDays(ageDays, isMale)
        val oldStage = twin.lifecycleStage
        val stageChanged = oldStage != newStage.name

        // Log transition event
        if (stageChanged) {
            val event = BirdEventEntity(
                birdId = twin.birdId,
                ownerId = twin.ownerId,
                eventType = BirdEventEntity.TYPE_STAGE_TRANSITION,
                eventTitle = "Stage: ${newStage.displayName}",
                eventDescription = "Transitioned from $oldStage to ${newStage.name} at $ageDays days old",
                eventDate = now,
                ageDaysAtEvent = ageDays,
                lifecycleStageAtEvent = newStage.name,
                stringValue = "${oldStage}->${newStage.name}"
            )
            eventDao.insert(event)
        }

        // Apply decline factors for Senior birds
        val healthAdjustment = if (newStage.hasDeclineFactors) {
            // Reduce stamina and performance scores slightly
            val currentStamina = twin.staminaScore ?: 50
            val declinedStamina = (currentStamina * 0.95).toInt().coerceAtLeast(20)
            declinedStamina
        } else {
            twin.staminaScore
        }

        // Calculate maturity score (how close to ideal for current stage)
        val maturityScore = calculateMaturityScore(twin, newStage, ageDays)

        return twin.copy(
            lifecycleStage = newStage.name,
            ageDays = ageDays,
            maturityScore = maturityScore,
            staminaScore = healthAdjustment,
            updatedAt = now
        )
    }

    /**
     * Batch update lifecycle for all birds of an owner.
     */
    suspend fun updateAllLifecycles(ownerId: String): List<DigitalTwinEntity> {
        val twins = twinDao.getByOwnerOnce(ownerId)
        return twins.map { twin ->
            val updated = updateLifecycle(twin)
            if (updated != twin) {
                twinDao.update(updated)
            }
            updated
        }
    }

    /**
     * Get information about the next stage transition.
     */
    fun getNextTransitionInfo(twin: DigitalTwinEntity): TransitionInfo? {
        val birthDate = twin.birthDate ?: return null
        val ageDays = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - birthDate
        ).toInt()
        val isMale = twin.gender?.uppercase() != "FEMALE"

        val currentStage = AseelLifecycleStage.fromAgeDays(ageDays, isMale)
        val nextStage = AseelLifecycleStage.nextStage(currentStage) ?: return null
        val daysRemaining = AseelLifecycleStage.daysUntilNextTransition(ageDays, isMale) ?: return null

        return TransitionInfo(
            currentStage = currentStage,
            nextStage = nextStage,
            daysRemaining = daysRemaining,
            currentAgeDays = ageDays,
            capabilitiesUnlocked = getNewCapabilities(currentStage, nextStage)
        )
    }

    /**
     * Check if a specific capability is available for this bird.
     */
    fun canMeasureMorphology(twin: DigitalTwinEntity): Boolean {
        val stage = AseelLifecycleStage.entries.find { it.name == twin.lifecycleStage }
        return stage?.canMeasureMorphology ?: false
    }

    fun canMeasurePerformance(twin: DigitalTwinEntity): Boolean {
        val stage = AseelLifecycleStage.entries.find { it.name == twin.lifecycleStage }
        return stage?.canMeasurePerformance ?: false
    }

    fun isBreedingEligible(twin: DigitalTwinEntity): Boolean {
        if (twin.currentHealthStatus !in listOf("HEALTHY", "OK")) return false
        val stage = AseelLifecycleStage.entries.find { it.name == twin.lifecycleStage }
        return stage?.isBreedingEligible ?: false
    }

    fun isShowEligible(twin: DigitalTwinEntity): Boolean {
        if (twin.currentHealthStatus !in listOf("HEALTHY", "OK")) return false
        val stage = AseelLifecycleStage.entries.find { it.name == twin.lifecycleStage }
        return stage?.isShowEligible ?: false
    }

    /**
     * Format age in a human-readable way.
     */
    fun formatAge(ageDays: Int): String {
        return when {
            ageDays < 7 -> "$ageDays days"
            ageDays < 30 -> "${ageDays / 7} weeks"
            ageDays < 365 -> {
                val months = ageDays / 30
                val weeks = (ageDays % 30) / 7
                if (weeks > 0) "$months months $weeks weeks"
                else "$months months"
            }
            else -> {
                val years = ageDays / 365
                val months = (ageDays % 365) / 30
                if (months > 0) "$years year${if (years > 1) "s" else ""} $months month${if (months > 1) "s" else ""}"
                else "$years year${if (years > 1) "s" else ""}"
            }
        }
    }

    // ==================== PRIVATE HELPERS ====================

    /**
     * Calculate maturity score — how close to breed standard for current stage.
     * Based on weight, height, and trait development.
     */
    private fun calculateMaturityScore(
        twin: DigitalTwinEntity,
        stage: AseelLifecycleStage,
        ageDays: Int
    ): Int {
        // Base score from age progression within stage
        val stageMinDays = stage.minDays
        val stageMaxDays = stage.maxDays ?: (stageMinDays + 365)
        val progressInStage = ((ageDays - stageMinDays).toFloat() / (stageMaxDays - stageMinDays))
            .coerceIn(0f, 1f)

        var score = (progressInStage * 40).toInt() // 0-40 from age

        // Weight contribution (if available)
        twin.weightKg?.let { weight ->
            val idealWeight = getIdealWeight(stage, twin.gender)
            if (idealWeight > 0) {
                val weightRatio = (weight / idealWeight).coerceIn(0.5, 1.5)
                // Closer to 1.0 = higher score
                val weightScore = ((1.0 - Math.abs(1.0 - weightRatio)) * 30).toInt()
                score += weightScore
            }
        }

        // Morphology contribution (if available)
        twin.morphologyScore?.let {
            score += (it * 0.3).toInt() // Up to 30 points from morphology
        }

        return score.coerceIn(0, 100)
    }

    /**
     * Get ideal weight (in kg) for an Aseel at a given stage.
     */
    private fun getIdealWeight(stage: AseelLifecycleStage, gender: String?): Double {
        val isMale = gender?.uppercase() != "FEMALE"
        return when (stage) {
            AseelLifecycleStage.EGG -> 0.055 // ~55g egg
            AseelLifecycleStage.CHICK -> if (isMale) 0.2 else 0.15
            AseelLifecycleStage.GROWER -> if (isMale) 1.5 else 1.2
            AseelLifecycleStage.PRE_ADULT -> if (isMale) 2.5 else 2.0
            AseelLifecycleStage.ADULT_FIGHTER -> if (isMale) 3.5 else 2.5
            AseelLifecycleStage.BREEDER_PRIME -> if (isMale) 4.0 else 2.8
            AseelLifecycleStage.SENIOR -> if (isMale) 3.8 else 2.6 // slight decline
        }
    }

    /**
     * Get new capabilities unlocked at stage transition.
     */
    private fun getNewCapabilities(
        current: AseelLifecycleStage,
        next: AseelLifecycleStage
    ): List<String> {
        val capabilities = mutableListOf<String>()

        if (!current.canMeasureMorphology && next.canMeasureMorphology) {
            capabilities.add("Morphology Assessment Unlocked")
            capabilities.add("Weight Tracking Active")
        }
        if (!current.canMeasurePerformance && next.canMeasurePerformance) {
            capabilities.add("Performance Metrics Unlocked")
            capabilities.add("Show Eligibility Granted")
        }
        if (!current.isBreedingEligible && next.isBreedingEligible) {
            capabilities.add("Breeding Eligibility Granted")
        }
        if (!current.hasDeclineFactors && next.hasDeclineFactors) {
            capabilities.add("Senior Phase — Decline Factors Active")
        }

        return capabilities
    }

    /**
     * Data class for transition information.
     */
    data class TransitionInfo(
        val currentStage: AseelLifecycleStage,
        val nextStage: AseelLifecycleStage,
        val daysRemaining: Int,
        val currentAgeDays: Int,
        val capabilitiesUnlocked: List<String>
    )
}
