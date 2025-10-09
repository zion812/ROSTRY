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
import com.rio.rostry.utils.MilestoneNotifier
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
    private val analytics: GeneralAnalyticsTracker,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            MilestoneNotifier.ensureChannel(applicationContext)
            val now = System.currentTimeMillis()
            val farmerId = firebaseAuth.currentUser?.uid ?: return@withContext Result.success()
            // Use repository flow then filter by type
            val dueTasks = taskRepository.observeDue(farmerId, now).first().filter { it.taskType == "VACCINATION" }
            dueTasks.forEach { task ->
                MilestoneNotifier.notify(
                    applicationContext,
                    task.productId ?: farmerId,
                    title = "Vaccination due",
                    message = task.title
                )
                // Mark notifiedAt in metadata
                val meta = mutableMapOf<String, Any?>()
                task.metadata?.let { existing ->
                    runCatching { Gson().fromJson(existing, Map::class.java) as Map<String, Any?> }.getOrNull()?.let { meta.putAll(it) }
                }
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
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }
}
