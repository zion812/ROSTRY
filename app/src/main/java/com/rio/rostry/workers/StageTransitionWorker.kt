package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.LifecycleEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import com.rio.rostry.data.database.entity.TaskEntity
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.utils.MilestoneNotifier
import com.rio.rostry.utils.Resource
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.UUID

@HiltWorker
class StageTransitionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val productDao: ProductDao,
    private val lifecycleDao: LifecycleEventDao,
    private val taskRepository: TaskRepository,
    private val taskDao: TaskDao,
    private val gson: Gson
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val products = productDao.getActiveWithBirth()
        
        for (p in products) {
            val week = p.birthDate?.let { 
                com.rio.rostry.utils.LifecycleRules.calculateAgeInWeeks(it, now) 
            } ?: continue
            val stage = LifecycleStage.fromWeeks(week)
            
            if (p.stage != stage) {
                processStageTransition(p, stage, week, now)
            }
        }
        return Result.success()
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
                    metadata = gson.toJson(meta)
                )
                taskRepository.upsert(task)
            } else {
                val earliest = existing.minBy { it.dueAt ?: Long.MAX_VALUE }
                val newDue = minOf(earliest.dueAt ?: dueAt, dueAt)
                taskDao.updateDueAt(earliest.taskId, newDue, now)
            }
        }
    }
}
