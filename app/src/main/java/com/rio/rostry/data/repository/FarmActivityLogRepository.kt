package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.FarmActivityLogDao
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.entity.FarmActivityLogEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for FarmActivityLogEntity operations.
 * Handles farm-level activities like expenses, sanitation, maintenance, etc.
 * 
 * ENHANCED: Now includes Smart Chore auto-completion logic!
 * When a log is created, matching pending tasks are automatically completed.
 */
interface FarmActivityLogRepository {
    fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLogEntity>>
    fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLogEntity?>
    fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLogEntity>>
    fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLogEntity>>
    fun observeForProduct(productId: String): Flow<List<FarmActivityLogEntity>>
    suspend fun upsert(log: FarmActivityLogEntity)
    suspend fun logActivity(
        farmerId: String,
        productId: String?,
        activityType: String,
        amount: Double? = null,
        quantity: Double? = null,
        category: String? = null,
        description: String? = null,
        notes: String? = null
    ): FarmActivityLogEntity
    suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double
    suspend fun getById(activityId: String): FarmActivityLogEntity?
    suspend fun deleteActivity(activityId: String)
}

@Singleton
class FarmActivityLogRepositoryImpl @Inject constructor(
    private val dao: FarmActivityLogDao,
    private val taskDao: TaskDao
) : FarmActivityLogRepository {

    override fun observeForFarmer(farmerId: String): Flow<List<FarmActivityLogEntity>> =
        dao.observeForFarmer(farmerId)

    override fun observeLatestForFarmer(farmerId: String): Flow<FarmActivityLogEntity?> =
        dao.observeLatestForFarmer(farmerId)

    override fun observeForFarmerByType(farmerId: String, type: String): Flow<List<FarmActivityLogEntity>> =
        dao.observeForFarmerByType(farmerId, type)

    override fun observeForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<List<FarmActivityLogEntity>> =
        dao.observeForFarmerBetween(farmerId, start, end)

    override fun observeForProduct(productId: String): Flow<List<FarmActivityLogEntity>> =
        dao.observeForProduct(productId)

    override suspend fun upsert(log: FarmActivityLogEntity) {
        dao.upsert(log)
    }

    override suspend fun logActivity(
        farmerId: String,
        productId: String?,
        activityType: String,
        amount: Double?,
        quantity: Double?,
        category: String?,
        description: String?,
        notes: String?
    ): FarmActivityLogEntity {
        val now = System.currentTimeMillis()
        val entity = FarmActivityLogEntity(
            activityId = UUID.randomUUID().toString(),
            farmerId = farmerId,
            productId = productId,
            activityType = activityType,
            amountInr = amount,
            quantity = quantity,
            category = category,
            description = description,
            notes = notes,
            createdAt = now,
            updatedAt = now,
            dirty = true
        )
        dao.upsert(entity)
        
        // SMART CHORE: Auto-complete matching tasks!
        autoCompleteMatchingTasks(farmerId, productId, activityType)
        
        return entity
    }

    override suspend fun getTotalExpensesBetween(farmerId: String, start: Long, end: Long): Double =
        dao.getTotalExpensesBetween(farmerId, start, end) ?: 0.0

    override suspend fun getById(activityId: String): FarmActivityLogEntity? =
        dao.getById(activityId)

    override suspend fun deleteActivity(activityId: String) {
        dao.delete(activityId)
    }
    
    /**
     * SMART CHORE ENGINE: Auto-complete pending tasks when a matching log is created.
     * 
     * Mapping logic:
     * - VACCINATION log -> completes "VACCINATION" tasks for the same product
     * - DEWORMING log -> completes "DEWORMING" tasks
     * - MEDICATION log -> completes "MEDICATION" tasks
     * - FEED log -> completes "FEED" tasks (daily feeding reminders)
     * - SANITATION log -> completes "SANITATION" tasks
     * - WEIGHT log -> completes "GROWTH_CHECK" tasks
     */
    private suspend fun autoCompleteMatchingTasks(
        farmerId: String,
        productId: String?,
        activityType: String
    ) {
        val now = System.currentTimeMillis()
        
        // Map activity type to task type
        val taskType = when (activityType.uppercase()) {
            "VACCINATION" -> "VACCINATION"
            "DEWORMING" -> "DEWORMING"
            "MEDICATION" -> "MEDICATION"
            "FEED" -> "FEED"
            "SANITATION" -> "SANITATION"
            "WEIGHT" -> "GROWTH_CHECK"
            "MORTALITY" -> "MORTALITY_CHECK"
            else -> null
        }
        
        if (taskType == null) {
            Timber.d("No task mapping for activity type: $activityType")
            return
        }
        
        try {
            val pendingTasks = if (productId != null) {
                // Find tasks specific to this product
                taskDao.findPendingByTypeProduct(farmerId, productId, taskType)
            } else {
                // Find general tasks of this type
                taskDao.findPendingByType(farmerId, taskType)
            }
            
            if (pendingTasks.isNotEmpty()) {
                // Complete all matching pending tasks
                pendingTasks.forEach { task ->
                    taskDao.markComplete(
                        taskId = task.taskId,
                        completedAt = now,
                        completedBy = farmerId,
                        updatedAt = now
                    )
                    Timber.d("🎯 Smart Chore: Auto-completed task '${task.title}' (${task.taskId}) based on $activityType log")
                }
                
                Timber.i("Smart Chore: Auto-completed ${pendingTasks.size} '$taskType' task(s)")
            } else {
                Timber.d("No pending '$taskType' tasks to auto-complete for product: $productId")
            }
        } catch (e: Exception) {
            Timber.e(e, "Smart Chore: Failed to auto-complete tasks for $activityType")
        }
    }
}
