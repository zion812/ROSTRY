package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import androidx.work.BackoffPolicy
import androidx.work.NetworkType
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.network.ConnectivityManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationFlushWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val intelligentNotificationService: IntelligentNotificationService,
    private val connectivityManager: ConnectivityManager,
    private val currentUserProvider: CurrentUserProvider
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("NotificationFlushWorker: Starting flush attempt")
            
            if (!connectivityManager.isOnline()) {
                Timber.d("NotificationFlushWorker: Offline, retrying")
                return Result.retry()
            }
            
            val userId = currentUserProvider.userIdOrNull()
            if (userId == null) {
                Timber.d("NotificationFlushWorker: No user ID, skipping")
                return Result.success()
            }
            
            intelligentNotificationService.flushBatchedNotifications()
            
            Timber.d("NotificationFlushWorker: Flush completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "NotificationFlushWorker: Failed to flush notifications")
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "NotificationFlushWorkerPeriodic"
        private const val IMMEDIATE_NAME = "NotificationFlushWorkerImmediate"
        
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val request = PeriodicWorkRequestBuilder<NotificationFlushWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
            
            Timber.d("NotificationFlushWorker: Scheduled periodic flush every 15 minutes")
        }
        
        fun scheduleImmediateFlush(context: Context) {
            val request = OneTimeWorkRequestBuilder<NotificationFlushWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniqueWork(IMMEDIATE_NAME, ExistingWorkPolicy.REPLACE, request)
            
            Timber.d("NotificationFlushWorker: Scheduled immediate flush")
        }
    }
}