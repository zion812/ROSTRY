package com.rio.rostry.data.repository.monitoring

import com.google.gson.Gson
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TaskRepository {
    fun observeDue(farmerId: String, now: Long): Flow<List<TaskEntity>>
    fun observeOverdue(farmerId: String, now: Long): Flow<List<TaskEntity>>
    fun observeDueWindow(farmerId: String, now: Long, endOfDay: Long): Flow<List<TaskEntity>>
    fun observeOverdueCount(farmerId: String, now: Long): Flow<Int>
    fun observeRecentCompleted(farmerId: String, limit: Int): Flow<List<TaskEntity>>
    fun observeByFarmer(farmerId: String): Flow<List<TaskEntity>>
    fun observeByBatch(batchId: String): Flow<List<TaskEntity>>
    suspend fun upsert(task: TaskEntity)
    suspend fun markComplete(taskId: String, completedBy: String)
    suspend fun snooze(taskId: String, snoozeUntil: Long)
    suspend fun delete(taskId: String)
    suspend fun generateVaccinationTask(productId: String, farmerId: String, vaccineType: String, dueAt: Long)
    suspend fun generateGrowthTask(productId: String, farmerId: String, week: Int, dueAt: Long)
    suspend fun generateQuarantineCheckTask(productId: String, farmerId: String, dueAt: Long)
    suspend fun findPendingByTypeProduct(farmerId: String, productId: String, taskType: String): List<TaskEntity>
    suspend fun generateIncubationCheckTask(batchId: String, farmerId: String, dueAt: Long)
    suspend fun generateStageTransitionTask(productId: String, farmerId: String, stage: String, dueAt: Long)
    suspend fun generateBatchSplitTask(batchId: String, farmerId: String, dueAt: Long)
    suspend fun generateTransferTimeoutTask(transferId: String, farmerId: String, orderId: String?, dueAt: Long)
    suspend fun generateOrderUpdateTask(orderId: String, userId: String, status: String, dueAt: Long)
    suspend fun generateSocialActivityTask(userId: String, activityType: String, refId: String, dueAt: Long)
    suspend fun findOrCreateTask(farmerId: String, taskType: String, refId: String, taskFactory: () -> TaskEntity): TaskEntity
}

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {
    override fun observeDue(farmerId: String, now: Long): Flow<List<TaskEntity>> = dao.observeDueForFarmer(farmerId, now)

    override fun observeOverdue(farmerId: String, now: Long): Flow<List<TaskEntity>> = dao.observeOverdueForFarmer(farmerId, now)

    override fun observeDueWindow(farmerId: String, now: Long, endOfDay: Long): Flow<List<TaskEntity>> = dao.observeDueWindowForFarmer(farmerId, now, endOfDay)

    override fun observeOverdueCount(farmerId: String, now: Long): Flow<Int> = dao.observeOverdueCountForFarmer(farmerId, now)

    override fun observeRecentCompleted(farmerId: String, limit: Int): Flow<List<TaskEntity>> = dao.observeRecentCompletedForFarmer(farmerId, limit)

    override fun observeByFarmer(farmerId: String): Flow<List<TaskEntity>> = dao.observeByFarmer(farmerId)

    override fun observeByBatch(batchId: String): Flow<List<TaskEntity>> = dao.observeByBatchId(batchId)

    override suspend fun upsert(task: TaskEntity) {
        val now = System.currentTimeMillis()
        dao.upsert(task.copy(dirty = true, updatedAt = now))
    }

    override suspend fun markComplete(taskId: String, completedBy: String) {
        val now = System.currentTimeMillis()
        dao.markComplete(taskId, now, completedBy, now)
    }

    override suspend fun snooze(taskId: String, snoozeUntil: Long) {
        val now = System.currentTimeMillis()
        dao.updateSnoozeUntil(taskId, snoozeUntil, now)
    }

    override suspend fun delete(taskId: String) {
        dao.delete(taskId)
    }

    override suspend fun generateVaccinationTask(productId: String, farmerId: String, vaccineType: String, dueAt: Long) {
        val meta = mapOf("vaccineType" to vaccineType)
        val task = TaskEntity(
            taskId = generateId("task_vax_"),
            farmerId = farmerId,
            productId = productId,
            taskType = "VACCINATION",
            title = "Vaccination: $vaccineType",
            dueAt = dueAt,
            priority = "HIGH",
            metadata = Gson().toJson(meta)
        )
        upsert(task)
    }

    override suspend fun generateIncubationCheckTask(batchId: String, farmerId: String, dueAt: Long) {
        val task = TaskEntity(
            taskId = generateId("task_incubation_"),
            farmerId = farmerId,
            productId = null,
            batchId = batchId,
            taskType = "INCUBATION_CHECK",
            title = "Incubation check",
            dueAt = dueAt,
            priority = "MEDIUM"
        )
        upsert(task)
    }

    override suspend fun generateStageTransitionTask(productId: String, farmerId: String, stage: String, dueAt: Long) {
        val meta = mapOf("stage" to stage)
        val task = TaskEntity(
            taskId = generateId("task_stage_"),
            farmerId = farmerId,
            productId = productId,
            taskType = "STAGE_TRANSITION",
            title = "Transition to $stage",
            dueAt = dueAt,
            priority = "MEDIUM",
            metadata = Gson().toJson(meta)
        )
        upsert(task)
    }

    override suspend fun generateBatchSplitTask(batchId: String, farmerId: String, dueAt: Long) {
        val task = TaskEntity(
            taskId = generateId("task_batch_split_"),
            farmerId = farmerId,
            productId = null,
            batchId = batchId,
            taskType = "BATCH_SPLIT",
            title = "Split batch into individuals",
            dueAt = dueAt,
            priority = "MEDIUM"
        )
        upsert(task)
    }

    override suspend fun findPendingByTypeProduct(farmerId: String, productId: String, taskType: String): List<TaskEntity> =
        dao.findPendingByTypeProduct(farmerId, productId, taskType)

    override suspend fun generateGrowthTask(productId: String, farmerId: String, week: Int, dueAt: Long) {
        val meta = mapOf("week" to week)
        val task = TaskEntity(
            taskId = generateId("task_growth_"),
            farmerId = farmerId,
            productId = productId,
            taskType = "GROWTH_UPDATE",
            title = "Growth update: Week $week",
            dueAt = dueAt,
            priority = "MEDIUM",
            metadata = Gson().toJson(meta)
        )
        upsert(task)
    }

    override suspend fun generateQuarantineCheckTask(productId: String, farmerId: String, dueAt: Long) {
        val task = TaskEntity(
            taskId = generateId("task_quarantine_"),
            farmerId = farmerId,
            productId = productId,
            taskType = "QUARANTINE_CHECK",
            title = "Quarantine check",
            dueAt = dueAt,
            priority = "URGENT"
        )
        upsert(task)
    }

    override suspend fun generateTransferTimeoutTask(transferId: String, farmerId: String, orderId: String?, dueAt: Long) {
        val meta = mapOf(
            "transferId" to transferId,
            "orderId" to orderId,
            "refundStatus" to null
        )
        val task = TaskEntity(
            taskId = generateId("task_transfer_timeout_"),
            farmerId = farmerId,
            productId = null,
            taskType = "TRANSFER_TIMEOUT",
            title = "Resolve Transfer Timeout",
            dueAt = dueAt,
            priority = "HIGH",
            metadata = Gson().toJson(meta)
        )
        upsert(task)
    }

    override suspend fun generateOrderUpdateTask(orderId: String, userId: String, status: String, dueAt: Long) {
        val meta = mapOf(
            "orderId" to orderId,
            "status" to status,
            "statusChangedAt" to dueAt
        )
        val task = TaskEntity(
            taskId = generateId("task_order_update_"),
            farmerId = userId,
            productId = null,
            taskType = "ORDER_UPDATE",
            title = "Order $status",
            dueAt = dueAt,
            priority = "MEDIUM",
            metadata = Gson().toJson(meta)
        )
        upsert(task)
    }

    override suspend fun generateSocialActivityTask(userId: String, activityType: String, refId: String, dueAt: Long) {
        val meta = mapOf(
            "activityType" to activityType,
            "refId" to refId,
            "fromUser" to null
        )
        val task = TaskEntity(
            taskId = generateId("task_social_"),
            farmerId = userId,
            productId = null,
            taskType = "SOCIAL_ACTIVITY",
            title = "New $activityType",
            dueAt = dueAt,
            priority = "LOW",
            metadata = Gson().toJson(meta)
        )
        upsert(task)
    }

    override suspend fun findOrCreateTask(farmerId: String, taskType: String, refId: String, taskFactory: () -> TaskEntity): TaskEntity {
        val existing = dao.findPendingByTypeProduct(farmerId, refId, taskType)
        if (existing.isNotEmpty()) {
            return existing.first()
        } else {
            val task = taskFactory()
            upsert(task)
            return task
        }
    }

    private fun generateId(prefix: String): String = "$prefix${System.currentTimeMillis()}"
}