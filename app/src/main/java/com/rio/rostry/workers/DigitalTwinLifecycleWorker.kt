package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.DigitalTwinDao
import com.rio.rostry.data.database.dao.BirdEventDao
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import com.rio.rostry.domain.digitaltwin.AseelLifecycleEngine
import com.rio.rostry.domain.digitaltwin.ValuationEngine
import com.rio.rostry.domain.digitaltwin.lifecycle.*
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.toAppearanceJson
import com.rio.rostry.ui.enthusiast.digitalfarm.parseAppearanceFromJson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 🧬 DigitalTwinLifecycleWorker — Daily Twin Evolution Engine
 *
 * Runs once every 24 hours to:
 * 1. Recalculate bird ages from birthDate→now
 * 2. Detect biological stage transitions (Chick→Grower→Sub-Adult→Adult...)
 * 3. Evolve visual morphology via LifecycleMorphEngine
 * 4. Recompute valuation scores
 * 5. Check weight anomalies
 * 6. Log all transitions as BirdEvents
 *
 * This ensures every bird's Digital Twin stays up-to-date even if the
 * user hasn't opened the app.
 */
@HiltWorker
class DigitalTwinLifecycleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val twinDao: DigitalTwinDao,
    private val eventDao: BirdEventDao,
    private val lifecycleEngine: AseelLifecycleEngine,
    private val valuationEngine: ValuationEngine
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            Timber.d("DigitalTwinLifecycleWorker: Starting daily twin evolution...")

            // Get all active twins
            val allTwins = twinDao.getAllActive()

            var transitionsCount = 0
            var evolvedCount = 0
            var errorCount = 0

            for (twin in allTwins) {
                try {
                    val result = processTwin(twin)
                    if (result.stageChanged) transitionsCount++
                    if (result.evolved) evolvedCount++
                } catch (e: Exception) {
                    errorCount++
                    Timber.e(e, "DigitalTwinLifecycleWorker: Error processing twin ${twin.twinId}")
                }
            }

            val duration = System.currentTimeMillis() - startTime
            Timber.d(
                "DigitalTwinLifecycleWorker: Complete in ${duration}ms. " +
                "Processed: ${allTwins.size}, Transitions: $transitionsCount, " +
                "Evolved: $evolvedCount, Errors: $errorCount"
            )

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "DigitalTwinLifecycleWorker: Fatal error")
            Result.retry()
        }
    }

    private suspend fun processTwin(twin: DigitalTwinEntity): ProcessResult {
        val birthDate = twin.birthDate ?: return ProcessResult()
        if (twin.isDeleted) return ProcessResult()

        val now = System.currentTimeMillis()
        val ageDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(now - birthDate).toInt()
        val isMale = twin.gender?.equals("MALE", true) ?: true
        val oldStageName = twin.lifecycleStage

        // 1. Run lifecycle engine (stage detection + event logging)
        val afterLifecycle = lifecycleEngine.updateLifecycle(twin)
        val stageChanged = afterLifecycle.lifecycleStage != oldStageName

        // 2. Evolve morphology
        val ageProfile = AgeProfile.fromDays(ageDays, isMale)
        val currentAppearance = afterLifecycle.appearanceJson?.let { json ->
            try { parseAppearanceFromJson(json) } catch (e: Exception) { null }
        } ?: BirdAppearance(isMale = isMale)

        val evolvedAppearance = LifecycleMorphEngine.evolve(currentAppearance, ageProfile)
        val evolved = evolvedAppearance != currentAppearance

        // 3. Re-compute valuation
        val afterValuation = valuationEngine.computeFullValuation(afterLifecycle)

        // 4. Check weight anomaly
        val weightAnomaly = twin.weightKg?.let { weight ->
            val evaluation = GrowthCurve.evaluateWeight(ageDays, (weight * 1000).toInt(), isMale)
            evaluation.rating == WeightRating.UNDERWEIGHT || evaluation.rating == WeightRating.OVERWEIGHT
        } ?: false

        if (weightAnomaly) {
            logWeightAnomaly(twin, ageDays)
        }

        // 5. Persist final state
        val finalTwin = afterValuation.copy(
            ageDays = ageDays,
            maturityScore = (ageProfile.maturityIndex * 100).toInt().coerceIn(0, 100),
            appearanceJson = evolvedAppearance.toAppearanceJson(),
            updatedAt = now
        )

        twinDao.insertOrReplace(finalTwin)

        return ProcessResult(stageChanged = stageChanged, evolved = evolved)
    }

    private suspend fun logWeightAnomaly(twin: DigitalTwinEntity, ageDays: Int) {
        val isMale = twin.gender?.equals("MALE", true) ?: true
        val weightGrams = ((twin.weightKg ?: 0.0) * 1000).toInt()
        val expected = GrowthCurve.expectedWeight(ageDays, isMale)
        val evaluation = GrowthCurve.evaluateWeight(ageDays, weightGrams, isMale)

        // Only log if we haven't logged this recently (within 7 days)
        val recent = eventDao.getLatestEvent(twin.birdId, "WEIGHT_ANOMALY")
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        if (recent != null && recent.eventDate > sevenDaysAgo) return

        val event = BirdEventEntity(
            birdId = twin.birdId,
            ownerId = twin.ownerId,
            eventType = BirdEventEntity.TYPE_TRAIT_RECORDED,
            eventTitle = "Weight ${evaluation.rating.displayName} ${evaluation.rating.emoji}",
            eventDescription = "Current: ${weightGrams}g, Expected: ${expected.idealGrams}g " +
                "(${evaluation.deviationPercent}% deviation)",
            eventDate = System.currentTimeMillis(),
            ageDaysAtEvent = ageDays,
            lifecycleStageAtEvent = twin.lifecycleStage,
            numericValue = weightGrams.toDouble(),
            stringValue = evaluation.rating.name
        )
        eventDao.insert(event)
    }

    private data class ProcessResult(
        val stageChanged: Boolean = false,
        val evolved: Boolean = false
    )

    companion object {
        private const val UNIQUE_NAME = "DigitalTwinLifecycleDaily"

        /**
         * Schedule daily execution. Call once at app startup.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<DigitalTwinLifecycleWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(6, TimeUnit.HOURS) // Run ~6h after scheduling
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    15, TimeUnit.MINUTES
                )
                .addTag("digital_twin")
                .addTag("session_worker")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    UNIQUE_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
