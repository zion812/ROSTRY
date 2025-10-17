package com.rio.rostry.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class OutboxSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager,
    private val connectivityManager: ConnectivityManager
) : CoroutineWorker(appContext, workerParams) {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private val notificationId = 1001
    private val channelId = "sync_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sync Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for data synchronization"
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showSyncNotification(title: String, text: String, ongoing: Boolean = false) {
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(ongoing)
            .setProgress(0, 0, ongoing)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun dismissSyncNotification() {
        notificationManager.cancel(notificationId)
    }

    override suspend fun doWork(): Result {
        // Check if online
        if (!connectivityManager.isOnline()) {
            return Result.retry()
        }

        // Show progress notification
        showSyncNotification("Syncing data...", "Processing pending changes", ongoing = true)

        return try {
            // SyncManager.syncAll() handles outbox processing
            val result = syncManager.syncAll()
            when (result) {
                is Resource.Success -> {
                    val stats = result.data
                    showSyncNotification(
                        "Sync complete",
                        "${stats?.pushed ?: 0} pushed, ${stats?.pulled ?: 0} pulled"
                    )
                    Result.success()
                }
                is Resource.Error -> {
                    showSyncNotification(
                        "Sync failed",
                        result.message ?: "Unknown error"
                    )
                    Result.retry()
                }
                is Resource.Loading -> {
                    // Should not occur here; treat as retry
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            showSyncNotification("Sync failed", e.message ?: "Unknown error")
            Result.retry()
        } finally {
            // Dismiss notification after a short delay to allow user to see it
            kotlinx.coroutines.delay(2000)
            dismissSyncNotification()
        }
    }

    companion object {
        private const val WORK_NAME = "OutboxSyncWorker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<OutboxSyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    1, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun scheduleImmediateSync(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<OutboxSyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
