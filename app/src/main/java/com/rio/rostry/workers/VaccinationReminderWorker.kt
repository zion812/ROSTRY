package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.utils.MilestoneNotifier
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@HiltWorker
class VaccinationReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: VaccinationRepository,
    private val analytics: GeneralAnalyticsTracker
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            MilestoneNotifier.ensureChannel(applicationContext)
            val now = System.currentTimeMillis()
            val due = repo.dueReminders(now)
            due.forEach { rec ->
                MilestoneNotifier.notify(
                    applicationContext,
                    rec.productId,
                    title = "Vaccination due",
                    message = "${rec.vaccineType} scheduled"
                )
            }
            analytics.offlineBannerSeen("vaccination_reminder_worker_dispatched:${due.size}")
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
