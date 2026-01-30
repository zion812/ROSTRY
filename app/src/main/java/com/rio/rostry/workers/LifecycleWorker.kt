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
import com.google.gson.Gson
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.utils.MilestoneNotifier
import com.rio.rostry.utils.notif.EnthusiastNotifier
import com.rio.rostry.utils.notif.FarmNotifier
import com.rio.rostry.workers.processors.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Main lifecycle worker that orchestrates lifecycle processing.
 * 
 * Uses modular processors for delegated responsibilities:
 * - DashboardCacheProcessor: Pre-compute dashboard stats
 * - TaskGenerationProcessor: Generate daily farmer tasks
 * - MarketReadyProcessor: Detect harvest-ready batches
 * - FarmAssetAgeProcessor: Update farm asset ages
 * - UnderperformingBirdProcessor: Detect bottom 10% birds
 * - DailyBriefingProcessor: Send daily event summaries
 * 
 * Core lifecycle logic (stage transitions, milestones, batch splits) 
 * remains in this worker for cohesion.
 */
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
    // Modular processors
    private val dashboardCacheProcessor: DashboardCacheProcessor,
    private val taskGenerationProcessor: TaskGenerationProcessor,
    private val marketReadyProcessor: MarketReadyProcessor,
    private val farmAssetAgeProcessor: FarmAssetAgeProcessor,
    private val underperformingBirdProcessor: UnderperformingBirdProcessor,
    private val dailyBriefingProcessor: DailyBriefingProcessor
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val startTime = now
            MilestoneNotifier.ensureChannel(applicationContext)
            
            // ============================================================
            // MODULAR PROCESSORS: Delegate to specialized processors
            // ============================================================
            runProcessors(now)
            
            // ============================================================
            // CORE LIFECYCLE: Stage transitions, milestones, batch splits
            // ============================================================
            processProductLifecycles(now)
            
            // Phase 3: Auto-expire market listings older than 30 days
            val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
            productDao.purgeStaleMarketplace(thirtyDaysAgo)
            
            Timber.d("LifecycleWorker completed in ${System.currentTimeMillis() - startTime}ms")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "LifecycleWorker failed")
            Result.retry()
        }
    }
    
    /**
     * Run all modular processors.
     */
    private suspend fun runProcessors(now: Long) {
        val processors = listOf(
            dailyBriefingProcessor,
            farmAssetAgeProcessor,
            marketReadyProcessor,
            underperformingBirdProcessor,
            taskGenerationProcessor,
            dashboardCacheProcessor // Run last as it depends on updated data
        )
        
        for (processor in processors) {
            try {
                processor.process(now)
            } catch (e: Exception) {
                Timber.e(e, "Processor ${processor.processorName} failed")
            }
        }
    }
    
    /**
     * Core lifecycle processing for products.
     * Handles stage transitions, milestones, and batch splits.
     */
    private suspend fun processProductLifecycles(now: Long) {
        val products = productDao.getActiveWithBirth()
        
        for (p in products) {
            val week = p.birthDate?.let { 
                com.rio.rostry.utils.LifecycleRules.calculateAgeInWeeks(it, now) 
            } ?: continue
            val stage = LifecycleStage.fromWeeks(week)

            // Batch split detection: recommend split at >= 12 weeks
            if (p.isBatch == true && (p.lifecycleStatus == "ACTIVE" || p.lifecycleStatus == null)) {
                if (week >= 12 && p.splitAt == null) {
                    processBatchSplit(p, week, now)
                }
            }

            // Persist age cache
            productDao.updateAgeWeeks(p.productId, week)
            
            // Stage transition
            if (p.stage != stage) {
                processStageTransition(p, stage, week, now)
            }

            // Milestone processing
            processMilestones(p, stage, week, now)
            
            // Lineage audits
            processLineageAudit(p, now)
            
            // Trait milestones at 6/12/24 months
            processTraitMilestones(p, stage, week, now)
        }
    }
    
    private suspend fun processBatchSplit(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        week: Int,
        now: Long
    ) {
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
    
    private suspend fun processStageTransition(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        stage: LifecycleStage,
        week: Int,
        now: Long
    ) {
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
    
    private suspend fun processMilestones(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        stage: LifecycleStage,
        week: Int,
        now: Long
    ) {
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
        
        for (it in milestones) {
            val mType = it.type ?: continue
            val mWeek = it.week ?: continue
            val exists = lifecycleDao.existsEvent(p.productId, mType, mWeek)
            if (!exists) {
                lifecycleDao.insert(it)
                MilestoneNotifier.notify(applicationContext, p.productId, it)
            }
            handleMilestoneTask(p, it, now)
        }
    }
    
    private suspend fun handleMilestoneTask(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        milestone: LifecycleEventEntity,
        now: Long
    ) {
        when (milestone.type) {
            "VACCINATION" -> {
                val dueAt = endOfDay(now)
                val existing = taskDao.findPendingByTypeProduct(p.sellerId, p.productId, "VACCINATION")
                if (existing.isNotEmpty()) {
                    val earliest = existing.minBy { t -> t.dueAt ?: Long.MAX_VALUE }
                    val newDue = minOf(earliest.dueAt ?: dueAt, dueAt)
                    taskDao.updateDueAt(earliest.taskId, newDue, now)
                } else {
                    taskRepository.generateVaccinationTask(p.productId, p.sellerId, "IMMUNIZATION", dueAt)
                }
            }
            "GROWTH_UPDATE" -> {
                val dueAt = endOfWeek(now)
                val existing = taskDao.findPendingByTypeProduct(p.sellerId, p.productId, "GROWTH_UPDATE")
                if (existing.isNotEmpty()) {
                    val earliest = existing.minBy { t -> t.dueAt ?: Long.MAX_VALUE }
                    val newDue = minOf(earliest.dueAt ?: dueAt, dueAt)
                    taskDao.updateDueAt(earliest.taskId, newDue, now)
                } else {
                    taskRepository.generateGrowthTask(p.productId, p.sellerId, milestone.week ?: 0, dueAt)
                }
                EnthusiastNotifier.ensureChannel(applicationContext)
            }
            "MILESTONE" -> {
                if (milestone.notes?.contains("Breeder eligibility") == true) {
                    EnthusiastNotifier.breederEligible(applicationContext, p.productId)
                }
            }
        }
    }
    
    private suspend fun processLineageAudit(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        now: Long
    ) {
        val ancestors = traceability.ancestors(p.productId, maxDepth = 3)
        when (ancestors) {
            is com.rio.rostry.utils.Resource.Error -> {
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
    }
    
    private suspend fun processTraitMilestones(
        p: com.rio.rostry.data.database.entity.ProductEntity,
        stage: LifecycleStage,
        week: Int,
        now: Long
    ) {
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
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun endOfWeek(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis < now) cal.add(Calendar.WEEK_OF_YEAR, 1)
        return cal.timeInMillis
    }
}
