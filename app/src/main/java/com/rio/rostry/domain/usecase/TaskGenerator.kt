package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.entity.TaskEntity
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * TaskGenerator generates daily farm tasks based on batch schedules.
 * Called by LifecycleWorker at 4 AM to populate the "Today's Tasks" dashboard.
 * 
 * Task Types Generated:
 * - VACCINATION: Based on VaccinationRecordEntity.scheduledAt
 * - WEIGHT_LOG: Day 7, 14, 21, 28, 35, 42 growth check reminders
 * - FEED_LOG: Daily feed consumption logging reminder
 * - HEALTH_CHECK: Weekly health inspection for batches
 */
class TaskGenerator @Inject constructor(
    private val productDao: ProductDao,
    private val vaccinationDao: VaccinationRecordDao,
    private val growthRecordDao: GrowthRecordDao,
    private val taskDao: TaskDao
) {
    companion object {
        // Growth check days for broilers
        private val GROWTH_CHECK_DAYS = listOf(7, 14, 21, 28, 35, 42)
        
        // Task categories for grouping
        const val CATEGORY_ROUTINE = "ROUTINE"
        const val CATEGORY_HEALTH = "HEALTH"
        const val CATEGORY_SALES = "SALES"
    }

    /**
     * Generate tasks for a specific farmer for the target date (usually today).
     * This is idempotent - existing pending tasks are not duplicated.
     * 
     * @param farmerId The farmer's user ID
     * @param targetDate The date to generate tasks for (midnight timestamp)
     * @return Number of new tasks created
     */
    suspend fun generateDailyTasks(farmerId: String, targetDate: Long): Int {
        val now = System.currentTimeMillis()
        val endOfDay = targetDate + TimeUnit.DAYS.toMillis(1) - 1
        var tasksCreated = 0

        try {
            // 1. Generate Vaccination Tasks
            tasksCreated += generateVaccinationTasks(farmerId, targetDate, endOfDay, now)

            // 2. Generate Growth Weigh-In Tasks
            tasksCreated += generateGrowthTasks(farmerId, targetDate, now)

            // 3. Generate Daily Feed Logging Task (one per farmer)
            tasksCreated += generateFeedLogTask(farmerId, targetDate, now)

            // 4. Generate Weekly Health Check (every Sunday)
            tasksCreated += generateWeeklyHealthCheck(farmerId, targetDate, now)

            Timber.d("TaskGenerator: Created $tasksCreated tasks for farmer $farmerId")
        } catch (e: Exception) {
            Timber.e(e, "TaskGenerator failed for farmer $farmerId")
        }

        return tasksCreated
    }

    private suspend fun generateVaccinationTasks(
        farmerId: String,
        startOfDay: Long,
        endOfDay: Long,
        now: Long
    ): Int {
        var count = 0
        
        // Get overdue vaccinations (highest priority)
        val overdueVax = vaccinationDao.getOverdueForFarmer(farmerId, now)
        for (vax in overdueVax) {
            val existingTask = taskDao.findPendingByTypeProduct(farmerId, vax.productId, "VACCINATION")
            if (existingTask.isEmpty()) {
                val task = TaskEntity(
                    taskId = "task_vax_${UUID.randomUUID()}",
                    farmerId = farmerId,
                    productId = vax.productId,
                    batchId = vax.productId, // For batches, productId == batchId
                    taskType = "VACCINATION",
                    title = "Vaccinate: ${vax.vaccineType ?: "Scheduled"}",
                    description = "OVERDUE: Vaccination was scheduled for ${formatDate(vax.scheduledAt)}",
                    dueAt = now, // Due immediately
                    priority = "URGENT",
                    metadata = """{"vaccinationId":"${vax.vaccinationId}","vaccineType":"${vax.vaccineType}"}""",
                    createdAt = now,
                    updatedAt = now
                )
                taskDao.upsert(task)
                count++
            }
        }

        // Get today's scheduled vaccinations
        val todaysVax = vaccinationDao.dueReminders(endOfDay).filter { 
            it.farmerId == farmerId && it.scheduledAt >= startOfDay 
        }
        for (vax in todaysVax) {
            val existingTask = taskDao.findPendingByTypeProduct(farmerId, vax.productId, "VACCINATION")
            if (existingTask.isEmpty()) {
                val task = TaskEntity(
                    taskId = "task_vax_${UUID.randomUUID()}",
                    farmerId = farmerId,
                    productId = vax.productId,
                    batchId = vax.productId,
                    taskType = "VACCINATION",
                    title = "Vaccinate: ${vax.vaccineType ?: "Scheduled"}",
                    description = "Scheduled vaccination for today",
                    dueAt = vax.scheduledAt,
                    priority = "HIGH",
                    metadata = """{"vaccinationId":"${vax.vaccinationId}","vaccineType":"${vax.vaccineType}"}""",
                    createdAt = now,
                    updatedAt = now
                )
                taskDao.upsert(task)
                count++
            }
        }

        return count
    }

    private suspend fun generateGrowthTasks(
        farmerId: String,
        targetDate: Long,
        now: Long
    ): Int {
        var count = 0

        // Get all active batches for this farmer
        val batches = productDao.getActiveWithBirth().filter { 
            it.sellerId == farmerId && it.isBatch == true 
        }

        for (batch in batches) {
            val birthDate = batch.birthDate ?: continue
            val ageInDays = TimeUnit.MILLISECONDS.toDays(now - birthDate).toInt()

            // Check if today is a growth check day
            if (ageInDays in GROWTH_CHECK_DAYS) {
                val existingTask = taskDao.findPendingByTypeProduct(farmerId, batch.productId, "GROWTH_UPDATE")
                if (existingTask.isEmpty()) {
                    val task = TaskEntity(
                        taskId = "task_growth_${UUID.randomUUID()}",
                        farmerId = farmerId,
                        productId = batch.productId,
                        batchId = batch.productId,
                        taskType = "GROWTH_UPDATE",
                        title = "Weigh: ${batch.name ?: "Batch"} (Day $ageInDays)",
                        description = "Growth check - record average weight for the batch",
                        dueAt = targetDate + TimeUnit.HOURS.toMillis(10), // Due at 10 AM
                        priority = "MEDIUM",
                        metadata = """{"ageInDays":$ageInDays,"expectedWeight":${getExpectedWeight(ageInDays)}}""",
                        createdAt = now,
                        updatedAt = now
                    )
                    taskDao.upsert(task)
                    count++
                }
            }
        }

        return count
    }

    private suspend fun generateFeedLogTask(
        farmerId: String,
        targetDate: Long,
        now: Long
    ): Int {
        // Check if feed log task already exists for today
        val existingTask = taskDao.findPendingByType(farmerId, "FEED_SCHEDULE")
            .filter { it.dueAt >= targetDate && it.dueAt < targetDate + TimeUnit.DAYS.toMillis(1) }

        if (existingTask.isNotEmpty()) {
            return 0
        }

        val task = TaskEntity(
            taskId = "task_feed_${UUID.randomUUID()}",
            farmerId = farmerId,
            productId = null,
            batchId = null,
            taskType = "FEED_SCHEDULE",
            title = "Log Feed Consumption",
            description = "Record today's feed given to all batches",
            dueAt = targetDate + TimeUnit.HOURS.toMillis(18), // Due at 6 PM
            priority = "LOW",
            recurrence = "DAILY",
            createdAt = now,
            updatedAt = now
        )
        taskDao.upsert(task)
        return 1
    }

    private suspend fun generateWeeklyHealthCheck(
        farmerId: String,
        targetDate: Long,
        now: Long
    ): Int {
        // Check if today is Sunday (day of week = 1 in Calendar, 7 in DayOfWeek)
        val dayOfWeek = java.util.Calendar.getInstance().apply {
            timeInMillis = targetDate
        }.get(java.util.Calendar.DAY_OF_WEEK)

        if (dayOfWeek != java.util.Calendar.SUNDAY) {
            return 0
        }

        // Check if health check task already exists for this week
        val existingTask = taskDao.findPendingByType(farmerId, "HEALTH_CHECK")
            .filter { it.dueAt >= targetDate && it.dueAt < targetDate + TimeUnit.DAYS.toMillis(1) }

        if (existingTask.isNotEmpty()) {
            return 0
        }

        val activeBatchCount = productDao.countBatchesByFarmer(farmerId)
        if (activeBatchCount == 0) {
            return 0
        }

        val task = TaskEntity(
            taskId = "task_health_${UUID.randomUUID()}",
            farmerId = farmerId,
            productId = null,
            batchId = null,
            taskType = "HEALTH_CHECK",
            title = "Weekly Health Inspection",
            description = "Check all $activeBatchCount batches for signs of illness",
            dueAt = targetDate + TimeUnit.HOURS.toMillis(9), // Due at 9 AM
            priority = "MEDIUM",
            recurrence = "WEEKLY",
            createdAt = now,
            updatedAt = now
        )
        taskDao.upsert(task)
        return 1
    }

    private fun getExpectedWeight(ageInDays: Int): Int {
        // Approximate expected weight in grams for broiler chickens
        return when (ageInDays) {
            7 -> 150
            14 -> 400
            21 -> 750
            28 -> 1100
            35 -> 1500
            42 -> 1900
            else -> ageInDays * 40
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}
