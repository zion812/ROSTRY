package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.TaskDao
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.FarmEventType
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@HiltWorker
class VaccinationReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val taskDao: TaskDao,
    private val intelligentNotificationService: IntelligentNotificationService,
    private val analytics: GeneralAnalyticsTracker,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val farmerId = firebaseAuth.currentUser?.uid ?: return@withContext Result.success()
            // Use repository flow then filter by type
            val dueTasks = taskRepository.observeDue(farmerId, now).first().filter { it.taskType == "VACCINATION" }
            dueTasks.forEach { task ->
                // Check if task already exists (it does since from dueTasks), but per requirement
                val existingTasks = taskRepository.findPendingByTypeProduct(farmerId, task.productId ?: "", "VACCINATION")
                val existingTask = existingTasks.firstOrNull()
                if (existingTask != null) {
                    // If due date changed, update
                    if (existingTask.dueAt != task.dueAt) {
                        taskDao.updateDueAt(task.taskId, task.dueAt, now)
                    }
                } else {
                    // If no task exists, create one
                    taskRepository.generateVaccinationTask(task.productId ?: "", farmerId, task.metadata?.let { Gson().fromJson(it, Map::class.java)["vaccineType"] as? String } ?: "", task.dueAt)
                }
                // Add deduplication: check notifiedAt
                val meta = mutableMapOf<String, Any?>()
                task.metadata?.let { existing ->
                    runCatching { Gson().fromJson(existing, Map::class.java) as Map<String, Any?> }.getOrNull()?.let { meta.putAll(it) }
                }
                if (meta["notifiedAt"] != null) return@forEach // already notified
                // Notify
                val productId = task.productId ?: farmerId
                val title = "Vaccination due"
                val message = task.title
                val metadata = mapOf(
                    "vaccineType" to (meta["vaccineType"] ?: ""),
                    "dueAt" to task.dueAt,
                    "taskId" to task.taskId
                )
                intelligentNotificationService.notifyFarmEvent(FarmEventType.VACCINATION_DUE, productId, title, message, metadata)
                // Mark notifiedAt in metadata
                meta["notifiedAt"] = now
                taskDao.updateMetadata(task.taskId, Gson().toJson(meta), now)
            }
            analytics.offlineBannerSeen("vaccination_reminder_worker_dispatched:${dueTasks.size}")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "VaccinationReminderWorkerPeriodic"
        fun schedule(context: Context) {
            val initialDelayMinutes = Random.nextLong(0, 180)
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()
            val request = PeriodicWorkRequestBuilder<VaccinationReminderWorker>(1, TimeUnit.DAYS, 3, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }
}