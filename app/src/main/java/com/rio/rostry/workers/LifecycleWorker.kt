package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ProductTraitDao
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.data.database.entity.TaskEntity
import com.rio.rostry.data.repository.TraceabilityRepository
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.ValidationUtils
import com.rio.rostry.utils.MilestoneNotifier
import com.rio.rostry.utils.notif.EnthusiastNotifier
import com.rio.rostry.utils.notif.FarmNotifier
import com.google.gson.Gson
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.ui.navigation.Routes
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class LifecycleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val productDao: ProductDao,
    private val lifecycleDao: LifecycleEventDao,
    private val traitDao: ProductTraitDao,
    private val alertDao: FarmAlertDao,
    private val traceability: TraceabilityRepository,
    private val taskRepository: TaskRepository,
    private val taskDao: TaskDao,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            MilestoneNotifier.ensureChannel(applicationContext)
            // Restrict to active birds with known birth dates to reduce load
            val products = productDao.getActiveWithBirth()
            for (p in products) {
                val week = p.birthDate?.let { com.rio.rostry.utils.LifecycleRules.calculateAgeInWeeks(it, now) } ?: continue
                val stage = LifecycleStage.fromWeeks(week)

                // Batch split detection: recommend split at >= 12 weeks
                if (p.isBatch == true && (p.lifecycleStatus == "ACTIVE" || p.lifecycleStatus == null)) {
                    if (week >= 12 && p.splitAt == null) {
                        alertDao.upsert(
                            FarmAlertEntity(
                                alertId = UUID.randomUUID().toString(),
                                farmerId = p.sellerId,
                                alertType = "BATCH_SPLIT_DUE",
                                severity = "INFO",
                                message = "Batch ${p.name} is ready for individual tracking. Consider splitting for sex/color separation.",
                                actionRoute = Routes.Builders.monitoringGrowthWithProductId(p.productId),
                                createdAt = now
                            )
                        )
                        // Notify farmer about batch split due
                        FarmNotifier.batchSplitDue(applicationContext, p.productId, p.name ?: "Batch")
                        val existing = taskDao.findPendingByTypeProduct(p.sellerId, p.productId, "BATCH_SPLIT")
                        if (existing.isEmpty()) {
                            val meta = mapOf("count" to p.quantity.toInt(), "breed" to (p.breed ?: ""), "ageWeeks" to week)
                            val task = TaskEntity(
                                taskId = "task_batch_split_${UUID.randomUUID()}",
                                farmerId = p.sellerId,
                                productId = null,
                                batchId = p.productId,
                                taskType = "BATCH_SPLIT",
                                title = "Split batch into individuals",
                                dueAt = now,
                                priority = "URGENT",
                                metadata = Gson().toJson(meta)
                            )
                            taskRepository.upsert(task)
                        } else {
                            val earliest = existing.minBy { it.dueAt ?: Long.MAX_VALUE }
                            if ((earliest.dueAt ?: Long.MAX_VALUE) > now) {
                                taskDao.updateDueAt(earliest.taskId, now, now)
                            }
                        }
                    }
                }

                // Persist age cache without bumping updatedAt (avoid breaking LWW merges)
                productDao.updateAgeWeeks(p.productId, week)
                if (p.stage != stage) {
                    productDao.updateStage(p.productId, stage, now, now)
                    val transition = LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage.name,
                        type = "STAGE_TRANSITION",
                        notes = "Stage changed to $stage"
                    )
                    val exists = lifecycleDao.existsEvent(p.productId, transition.type ?: "STAGE_TRANSITION", week)
                    if (!exists) {
                        lifecycleDao.insert(transition)
                        // Notify on stage transition with deep link handled by MilestoneNotifier
                        MilestoneNotifier.notify(applicationContext, p.productId, transition)
                        val oldStage = p.stage ?: LifecycleStage.CHICK.name
                        val newStage = stage.name
                        val dueAt = now + 24 * 60 * 60 * 1000L
                        val existing = taskDao.findPendingByTypeProduct(p.sellerId, p.productId, "STAGE_TRANSITION")
                        if (existing.isEmpty()) {
                            val meta = mapOf("oldStage" to oldStage, "newStage" to newStage)
                            val task = TaskEntity(
                                taskId = "task_stage_${UUID.randomUUID()}",
                                farmerId = p.sellerId,
                                productId = p.productId,
                                taskType = "STAGE_TRANSITION",
                                title = "Transition to $newStage",
                                dueAt = dueAt,
                                priority = "MEDIUM",
                                metadata = Gson().toJson(meta)
                            )
                            taskRepository.upsert(task)
                        } else {
                            val earliest = existing.minBy { it.dueAt ?: Long.MAX_VALUE }
                            val newDue = minOf(earliest.dueAt ?: dueAt, dueAt)
                            taskDao.updateDueAt(earliest.taskId, newDue, now)
                        }
                    }
                }

                // Sample milestone rules
                val milestones = mutableListOf<LifecycleEventEntity>()
                if (stage == LifecycleStage.CHICK && (week == 0 || week == 2 || week == 4)) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage.name,
                        type = "VACCINATION",
                        notes = "Vaccination checkpoint (week $week)"
                    )
                }
                if (stage == LifecycleStage.GROWER) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage.name,
                        type = "GROWTH_UPDATE",
                        notes = "Weekly growth update"
                    )
                }
                if (stage == LifecycleStage.LAYER && (week == 20 || week == 30 || week == 40)) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage.name,
                        type = "MILESTONE",
                        notes = "Layer stage milestone (week $week)"
                    )
                }
                if (stage == LifecycleStage.BREEDER && week == 52) {
                    milestones += LifecycleEventEntity(
                        eventId = UUID.randomUUID().toString(),
                        productId = p.productId,
                        week = week,
                        stage = stage.name,
                        type = "MILESTONE",
                        notes = "Breeder eligibility"
                    )
                    productDao.updateBreederEligibleAt(p.productId, now, now)
                }
                // Persist generated milestones and notify
                milestones.forEach {
                    val mType = it.type ?: return@forEach
                    val mWeek = it.week ?: return@forEach
                    val exists = lifecycleDao.existsEvent(p.productId, mType, mWeek)
                    if (!exists) {
                        lifecycleDao.insert(it)
                        MilestoneNotifier.notify(applicationContext, p.productId, it)
                    }
                    if (it.type == "VACCINATION") {
                        // Deduped vaccination task scheduled at end-of-day
                        val vaxType = "IMMUNIZATION"
                        val dueAt = endOfDay(now)
                        val existing = taskDao.findPendingByTypeProduct(p.sellerId, p.productId, "VACCINATION")
                        if (existing.isNotEmpty()) {
                            val earliest = existing.minBy { t -> t.dueAt ?: Long.MAX_VALUE }
                            val newDue = minOf(earliest.dueAt ?: dueAt, dueAt)
                            taskDao.updateDueAt(earliest.taskId, newDue, now)
                        } else {
                            taskRepository.generateVaccinationTask(p.productId, p.sellerId, vaxType, dueAt)
                        }
                    }
                    if (it.type == "GROWTH_UPDATE") {
                        // Deduped growth update task scheduled at end-of-week
                        val dueAt = endOfWeek(now)
                        val existing = taskDao.findPendingByTypeProduct(p.sellerId, p.productId, "GROWTH_UPDATE")
                        if (existing.isNotEmpty()) {
                            val earliest = existing.minBy { t -> t.dueAt ?: Long.MAX_VALUE }
                            val newDue = minOf(earliest.dueAt ?: dueAt, dueAt)
                            taskDao.updateDueAt(earliest.taskId, newDue, now)
                        } else {
                            taskRepository.generateGrowthTask(p.productId, p.sellerId, week, dueAt)
                        }
                        EnthusiastNotifier.ensureChannel(applicationContext)
                    }
                    if (it.type == "MILESTONE" && it.notes?.contains("Breeder eligibility") == true) {
                        EnthusiastNotifier.breederEligible(applicationContext, p.productId)
                    }
                }
                
                // Lineage audits: verify parent/partner existence and detect cycles
                val ancestors = traceability.ancestors(p.productId, maxDepth = 3)
                when (ancestors) {
                    is Resource.Error -> {
                        // Cycle or inconsistency detected
                        alertDao.upsert(
                            FarmAlertEntity(
                                alertId = UUID.randomUUID().toString(),
                                farmerId = p.sellerId,
                                alertType = "LINEAGE_INCONSISTENCY",
                                severity = "MEDIUM",
                                message = "Lineage inconsistency for ${p.name}: ${ancestors.message}",
                                actionRoute = Routes.Builders.familyTree(p.productId),
                                createdAt = now
                            )
                        )
                        EnthusiastNotifier.lineageAlert(applicationContext, p.productId, "Lineage alert: ${ancestors.message}")
                    }
                    else -> {}
                }
                
                // Trait milestones: high-value trait notifications at 6/12/24 months
                val months = p.birthDate?.let { ((now - it) / (30L * 24 * 60 * 60 * 1000)).toInt() } ?: 0
                if (months in listOf(6, 12, 24)) {
                    val traits = traitDao.traitsForProduct(p.productId)
                    val highValue = traits.filter { t -> t.name in listOf("champion_bloodline", "rare_color", "superior_genetics") }
                    if (highValue.isNotEmpty()) {
                        val milestone = LifecycleEventEntity(
                            eventId = UUID.randomUUID().toString(),
                            productId = p.productId,
                            week = week,
                            stage = stage.name,
                            type = "TRAIT_MILESTONE",
                            notes = "$months-month milestone: ${highValue.joinToString { trait -> trait.name }}"
                        )
                        val exists = lifecycleDao.existsEvent(p.productId, milestone.type ?: "TRAIT_MILESTONE", week)
                        if (!exists) {
                            lifecycleDao.insert(milestone)
                            EnthusiastNotifier.traitMilestone(applicationContext, p.productId, milestone.notes ?: "Trait milestone")
                        }
                    }
                }
            }
            
            // Phase 3: Auto-expire market listings older than 30 days
            val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
            productDao.purgeStaleMarketplace(thirtyDaysAgo)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "LifecycleWorkerPeriodic"
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<LifecycleWorker>(1, TimeUnit.DAYS)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .addTag("session_worker")
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }

    private fun endOfDay(now: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun endOfWeek(now: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.firstDayOfWeek = java.util.Calendar.MONDAY
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        // If current day is already Sunday past time, push to next Sunday
        if (cal.timeInMillis < now) cal.add(java.util.Calendar.WEEK_OF_YEAR, 1)
        return cal.timeInMillis
    }
}
