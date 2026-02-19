package com.rio.rostry.domain.digitaltwin

import com.rio.rostry.data.database.dao.BirdEventDao
import com.rio.rostry.data.database.dao.DigitalTwinDao
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.digitaltwin.lifecycle.*
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.deriveAppearanceFromBreed
import com.rio.rostry.domain.model.toAppearanceJson
import com.rio.rostry.ui.enthusiast.digitalfarm.parseAppearanceFromJson
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🧬 DigitalTwinService — Master Orchestrator for the Digital Twin Ecosystem
 *
 * This service ties together ALL Digital Twin engines and DAOs into a single,
 * cohesive API. It is the primary entry point for any code that needs to:
 *
 * 1. Create a Digital Twin from an existing ProductEntity (bird)
 * 2. Evolve a bird's lifecycle (age progression, stage transitions)
 * 3. Compute valuations and scores
 * 4. Log events (weight, fights, health, breeding)
 * 5. Generate growth analytics
 *
 * Architecture:
 * ```
 * DigitalTwinService (this)
 *   ├── AseelLifecycleEngine     → Stage transitions, capability gates
 *   ├── ValuationEngine          → Scoring & market value
 *   ├── LifecycleMorphEngine     → Visual morphology evolution
 *   ├── GrowthCurve              → Weight analytics & prediction
 *   ├── DigitalTwinDao           → Persistence
 *   └── BirdEventDao             → Event sourcing
 * ```
 */
@Singleton
class DigitalTwinService @Inject constructor(
    private val twinDao: DigitalTwinDao,
    private val eventDao: BirdEventDao,
    private val lifecycleEngine: AseelLifecycleEngine,
    private val valuationEngine: ValuationEngine
) {

    // ==================== TWIN CREATION ====================

    /**
     * Create a Digital Twin from an existing ProductEntity.
     * This is the "birth" of a bird's Digital Twin.
     *
     * @param product The source ProductEntity
     * @param ownerId The current owner ID
     * @return The newly created DigitalTwinEntity
     */
    suspend fun createTwinFromProduct(product: ProductEntity, ownerId: String): DigitalTwinEntity {
        // Check if twin already exists
        val existing = twinDao.getByBirdId(product.productId)
        if (existing != null) return existing

        val isMale = product.gender?.equals("Male", true) ?: true
        val ageDays = (product.ageWeeks ?: 0) * 7
        val ageProfile = AgeProfile.fromDays(ageDays, isMale)

        // Generate registry ID
        val registryId = RegistryIdGenerator.generate(
            strainType = null,
            localColorCode = product.colorTag,
            sequenceNumber = System.currentTimeMillis().toInt() % 10000
        )

        // Derive appearance and evolve it
        val baseAppearance = product.metadataJson.let { json ->
            if (json.isNotBlank()) parseAppearanceFromJson(json) else null
        } ?: deriveAppearanceFromBreed(product.breed ?: "Aseel", product.gender ?: "Male", product.ageWeeks ?: 0)

        val evolvedAppearance = LifecycleMorphEngine.evolve(baseAppearance, ageProfile)

        // Build the twin
        val twin = DigitalTwinEntity(
            twinId = UUID.randomUUID().toString(),
            birdId = product.productId,
            registryId = registryId,
            ownerId = ownerId,
            birdName = product.name.ifBlank { null },
            baseBreed = product.breed ?: "Aseel",
            strainType = null,
            gender = if (isMale) "MALE" else "FEMALE",
            birthDate = product.birthDate,
            ageDays = ageDays,
            lifecycleStage = ageProfile.stage.name,
            maturityScore = (ageProfile.maturityIndex * 100).toInt().coerceIn(0, 100),
            weightKg = product.weightGrams?.let { it / 1000.0 },
            heightCm = product.heightCm,
            skinColor = product.color,
            appearanceJson = evolvedAppearance.toAppearanceJson(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Persist
        twinDao.insertOrReplace(twin)

        // Log creation event
        val event = BirdEventEntity(
            eventId = UUID.randomUUID().toString(),
            birdId = product.productId,
            ownerId = ownerId,
            eventType = BirdEventEntity.TYPE_STAGE_TRANSITION,
            eventTitle = "Digital Twin Created",
            eventDescription = "Digital Twin initialized from product record. Stage: ${ageProfile.stage.displayName}",
            eventDate = System.currentTimeMillis(),
            ageDaysAtEvent = ageDays,
            lifecycleStageAtEvent = ageProfile.stage.name
        )
        eventDao.insert(event)

        return twin
    }

    // ==================== LIFECYCLE MANAGEMENT ====================

    /**
     * Update a bird's lifecycle — checks for stage transitions,
     * evolves morphology, logs events, recomputes scores.
     *
     * Call this periodically (e.g., daily via WorkManager) or on demand.
     */
    suspend fun updateLifecycle(birdId: String): DigitalTwinEntity? {
        val twin = twinDao.getByBirdId(birdId) ?: return null

        // 1. Run lifecycle engine (stage transitions + events)
        val afterLifecycle = lifecycleEngine.updateLifecycle(twin)

        // 2. Evolve morphology if age changed
        val ageDays = afterLifecycle.ageDays ?: 0
        val isMale = afterLifecycle.gender?.equals("MALE", true) ?: true
        val ageProfile = AgeProfile.fromDays(ageDays, isMale)

        val currentAppearance = afterLifecycle.appearanceJson?.let {
            parseAppearanceFromJson(it)
        } ?: BirdAppearance(isMale = isMale)

        val evolvedAppearance = LifecycleMorphEngine.evolve(currentAppearance, ageProfile)

        // 3. Re-compute valuation
        val afterValuation = valuationEngine.computeFullValuation(afterLifecycle)

        // 4. Compile final
        val finalTwin = afterValuation.copy(
            appearanceJson = evolvedAppearance.toAppearanceJson(),
            maturityScore = (ageProfile.maturityIndex * 100).toInt().coerceIn(0, 100),
            updatedAt = System.currentTimeMillis()
        )

        twinDao.insertOrReplace(finalTwin)
        return finalTwin
    }

    /**
     * Batch update all twins for an owner (e.g., daily cron job).
     */
    suspend fun updateAllLifecycles(ownerId: String) {
        lifecycleEngine.updateAllLifecycles(ownerId)
    }

    // ==================== EVENT LOGGING ====================

    /**
     * Record a weight measurement event.
     */
    suspend fun recordWeight(birdId: String, ownerId: String, weightGrams: Int) {
        val twin = twinDao.getByBirdId(birdId) ?: return
        val ageDays = twin.ageDays ?: 0
        val isMale = twin.gender?.equals("MALE", true) ?: true

        // Evaluate against growth curve
        val evaluation = GrowthCurve.evaluateWeight(ageDays, weightGrams, isMale)

        val event = BirdEventEntity(
            eventId = UUID.randomUUID().toString(),
            birdId = birdId,
            ownerId = ownerId,
            eventType = BirdEventEntity.TYPE_WEIGHT_RECORDED,
            eventTitle = "Weight: ${weightGrams}g",
            eventDescription = "Weight recorded: ${weightGrams}g. " +
                "Expected: ${evaluation.expected.idealGrams}g. " +
                "Rating: ${evaluation.rating.displayName} (${evaluation.deviationPercent}%)",
            eventDate = System.currentTimeMillis(),
            ageDaysAtEvent = ageDays,
            lifecycleStageAtEvent = twin.lifecycleStage,
            numericValue = weightGrams.toDouble(),
            stringValue = evaluation.rating.name,
            morphologyScoreDelta = when (evaluation.rating) {
                WeightRating.EXCELLENT -> 2
                WeightRating.GOOD -> 1
                WeightRating.FAIR -> 0
                WeightRating.UNDERWEIGHT -> -1
                WeightRating.OVERWEIGHT -> -1
            }
        )
        eventDao.insert(event)

        // Update twin weight
        twinDao.insertOrReplace(twin.copy(
            weightKg = weightGrams / 1000.0,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Record a fight result event.
     */
    suspend fun recordFightResult(
        birdId: String,
        ownerId: String,
        won: Boolean,
        opponentName: String? = null,
        durationMinutes: Int? = null,
        notes: String? = null
    ) {
        val twin = twinDao.getByBirdId(birdId) ?: return

        val title = if (won) "Fight Won" else "Fight Lost"
        val opponent = opponentName?.let { " vs $it" } ?: ""

        val event = BirdEventEntity(
            eventId = UUID.randomUUID().toString(),
            birdId = birdId,
            ownerId = ownerId,
            eventType = if (won) BirdEventEntity.TYPE_FIGHT_WIN else BirdEventEntity.TYPE_FIGHT_LOSS,
            eventTitle = "$title$opponent",
            eventDescription = buildString {
                append(title)
                opponentName?.let { append(" against $it") }
                durationMinutes?.let { append(". Duration: ${it}min") }
                notes?.let { append(". $it") }
            },
            eventDate = System.currentTimeMillis(),
            ageDaysAtEvent = twin.ageDays,
            lifecycleStageAtEvent = twin.lifecycleStage,
            numericValue = durationMinutes?.toDouble(),
            performanceScoreDelta = if (won) 5 else -2,
            marketScoreDelta = if (won) 3 else -1
        )
        eventDao.insert(event)

        // Update twin fight stats
        twinDao.insertOrReplace(twin.copy(
            totalFights = twin.totalFights + 1,
            fightWins = if (won) twin.fightWins + 1 else twin.fightWins,
            updatedAt = System.currentTimeMillis()
        ))
    }

    /**
     * Record a health event (vaccination, injury, recovery).
     */
    suspend fun recordHealthEvent(
        birdId: String,
        ownerId: String,
        eventType: String,
        title: String,
        description: String? = null
    ) {
        val twin = twinDao.getByBirdId(birdId) ?: return

        val scoreDelta = when (eventType) {
            BirdEventEntity.TYPE_VACCINATION -> 2
            BirdEventEntity.TYPE_INJURY -> -5
            BirdEventEntity.TYPE_RECOVERY -> 3
            else -> 0
        }

        val event = BirdEventEntity(
            eventId = UUID.randomUUID().toString(),
            birdId = birdId,
            ownerId = ownerId,
            eventType = eventType,
            eventTitle = title,
            eventDescription = description,
            eventDate = System.currentTimeMillis(),
            ageDaysAtEvent = twin.ageDays,
            lifecycleStageAtEvent = twin.lifecycleStage,
            healthScoreDelta = scoreDelta
        )
        eventDao.insert(event)

        // Update twin health stats
        val updatedTwin = when (eventType) {
            BirdEventEntity.TYPE_VACCINATION -> twin.copy(
                vaccinationCount = twin.vaccinationCount + 1,
                updatedAt = System.currentTimeMillis()
            )
            BirdEventEntity.TYPE_INJURY -> twin.copy(
                injuryCount = twin.injuryCount + 1,
                currentHealthStatus = "INJURED",
                updatedAt = System.currentTimeMillis()
            )
            BirdEventEntity.TYPE_RECOVERY -> twin.copy(
                currentHealthStatus = "HEALTHY",
                updatedAt = System.currentTimeMillis()
            )
            else -> twin.copy(updatedAt = System.currentTimeMillis())
        }
        twinDao.insertOrReplace(updatedTwin)
    }

    // ==================== ANALYTICS ====================



    /**
     * Get the full morph summary for a bird at its current age.
     */
    suspend fun getMorphSummary(birdId: String): MorphSummary? {
        val twin = twinDao.getByBirdId(birdId) ?: return null
        val isMale = twin.gender?.equals("MALE", true) ?: true
        val ageDays = twin.ageDays ?: 0
        val ageProfile = AgeProfile.fromDays(ageDays, isMale)
        val constraints = MorphConstraints.forStage(ageProfile.stage, isMale)

        return MorphSummary(
            stageName = ageProfile.stage.displayName,
            stageEmoji = ageProfile.stage.emoji,
            featherTexture = constraints.defaultFeatherTexture,
            keyFeatures = buildList {
                if (ageProfile.stage.hasHackles && isMale) add("Hackle feathers visible")
                if (ageProfile.stage.hasSpurs && isMale) add("Spurs developing")
                if (ageProfile.stage.hasSickleFeathers && isMale) add("Sickle tail feathers")
                if (ageProfile.stage.hasFullPlumage) add("Full plumage achieved")
                if (ageProfile.stage == BiologicalStage.HATCHLING) add("Down-covered, oversized head")
                if (ageProfile.stage == BiologicalStage.CHICK) add("First feathers emerging")
                if (ageProfile.stage == BiologicalStage.GROWER) add("Rapid leg growth, patchy feathers")
                if (ageProfile.stage == BiologicalStage.SENIOR) add("Peak bone thickness, slight feather dulling")
            },
            maturityPercent = (ageProfile.maturityIndex * 100).toInt().coerceIn(0, 100)
        )
    }

    /**
     * Get a bird's growth timeline (for growth tracker UI).
     */
    suspend fun getGrowthTimeline(birdId: String): List<GrowthSnapshot>? {
        val twin = twinDao.getByBirdId(birdId) ?: return null
        val isMale = twin.gender?.equals("MALE", true) ?: true

        val baseAppearance = twin.appearanceJson?.let {
            parseAppearanceFromJson(it)
        } ?: BirdAppearance(isMale = isMale)

        return LifecycleMorphEngine.generateGrowthTimeline(baseAppearance, isMale)
    }

    /**
     * Get weight analytics for a bird (current vs expected, evaluation, ideal curve).
     */
    suspend fun getWeightAnalytics(birdId: String): WeightAnalytics? {
        val twin = twinDao.getByBirdId(birdId) ?: return null
        val isMale = twin.gender?.equals("MALE", true) ?: true
        val ageDays = twin.ageDays ?: 0

        // Current weight (convert kg to grams)
        val currentWeightGrams = twin.weightKg?.let { (it * 1000).toInt() }

        // Expected weight from breed curve
        val expected = GrowthCurve.expectedWeight(ageDays, isMale)

        // Evaluation (if we have current weight)
        val evaluation = currentWeightGrams?.let {
            GrowthCurve.evaluateWeight(ageDays, it, isMale)
        }

        // Generate ideal curve for display (up to current age + buffer)
        val maxDays = (ageDays * 1.5).toInt().coerceAtLeast(365)
        val idealCurve = GrowthCurve.generateCurve(isMale, maxDays, stepDays = 7)

        return WeightAnalytics(
            currentWeightGrams = currentWeightGrams,
            expectedWeightGrams = expected.idealGrams,
            evaluation = evaluation,
            idealCurve = idealCurve,
            ageDays = ageDays,
            isMale = isMale
        )
    }

    // ==================== MANUAL GRADING ====================

    /**
     * Submit manual morphology grades for a bird (replaces AI analysis).
     * Updates the twin's metadata and re-runs valuation.
     */
    suspend fun submitManualGrading(birdId: String, grades: ManualMorphologyGrades) {
        val twin = twinDao.getByBirdId(birdId) ?: return

        // 1. Serialize grades into metadataJson
        val currentMeta = try {
            org.json.JSONObject(twin.metadataJson)
        } catch (e: Exception) {
            org.json.JSONObject()
        }

        val gradesJson = serializeManualGrades(grades)
        currentMeta.put("manualGrades", gradesJson)

        val updatedTwin = twin.copy(
            metadataJson = currentMeta.toString(),
            updatedAt = System.currentTimeMillis()
        )

        // 2. Re-compute valuation (now using manual grades)
        val gradedTwin = valuationEngine.computeFullValuation(updatedTwin)

        // 3. Persist
        twinDao.insertOrReplace(gradedTwin)

        // 4. Log event
        val event = BirdEventEntity(
            eventId = UUID.randomUUID().toString(),
            birdId = birdId,
            ownerId = twin.ownerId,
            eventType = "MORPHOLOGY_GRADING", // Custom event type
            eventTitle = "Manual Grading Completed",
            eventDescription = "Expert grading submitted. Score: ${gradedTwin.morphologyScore}/100",
            eventDate = System.currentTimeMillis(),
            ageDaysAtEvent = twin.ageDays,
            lifecycleStageAtEvent = twin.lifecycleStage,
            numericValue = gradedTwin.morphologyScore?.toDouble()
        )
        eventDao.insert(event)
    }

    private fun serializeManualGrades(grades: ManualMorphologyGrades): org.json.JSONObject {
        return org.json.JSONObject().apply {
            put("beakType", grades.beakType)
            put("eyeColor", grades.eyeColor)
            put("legColor", grades.legColor)
            put("tailCarry", grades.tailCarry)
            put("bodyStructureScore", grades.bodyStructureScore)
            put("plumageQualityScore", grades.plumageQualityScore)
            put("stanceScore", grades.stanceScore)
            put("hasWryTail", grades.hasWryTail)
            put("hasSplitWing", grades.hasSplitWing)
            put("hasCrookedToes", grades.hasCrookedToes)
        }
    }

    // ==================== QUERIES ====================

    /**
     * Get a twin by bird ID. Null if not created yet.
     */
    suspend fun getTwin(birdId: String): DigitalTwinEntity? {
        return twinDao.getByBirdId(birdId)
    }

    /**
     * Check if a bird has a Digital Twin.
     */
    suspend fun hasTwin(birdId: String): Boolean {
        return twinDao.getByBirdId(birdId) != null
    }

    /**
     * Get the lifecycle transition info for a bird.
     */
    suspend fun getTransitionInfo(birdId: String): AseelLifecycleEngine.TransitionInfo? {
        val twin = twinDao.getByBirdId(birdId) ?: return null
        return lifecycleEngine.getNextTransitionInfo(twin)
    }

    /**
     * Delete a twin (soft delete).
     */
    suspend fun deleteTwin(birdId: String) {
        val twin = twinDao.getByBirdId(birdId) ?: return
        twinDao.insertOrReplace(twin.copy(
            isDeleted = true,
            deletedAt = System.currentTimeMillis()
        ))
    }
}

/**
 * Analytics data for weight tracking.
 */
data class WeightAnalytics(
    val currentWeightGrams: Int?,
    val expectedWeightGrams: Int,
    val evaluation: WeightEvaluation?,
    val idealCurve: List<WeightExpectation>,
    val ageDays: Int,
    val isMale: Boolean
)
