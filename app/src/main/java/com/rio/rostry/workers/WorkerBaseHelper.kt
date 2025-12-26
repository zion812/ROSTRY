package com.rio.rostry.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.rio.rostry.R
import java.util.concurrent.TimeUnit

object WorkerBaseHelper {
    fun buildPeriodic(
        workerClass: Class<out ListenableWorker>,
        repeatIntervalHours: Long,
        constraints: Constraints = Constraints.NONE,
        backoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL,
        backoffDelayMinutes: Long = 10
    ): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            workerClass,
            repeatIntervalHours,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(backoffPolicy, backoffDelayMinutes, TimeUnit.MINUTES)
            .build()
    }

    fun enqueueUniquePeriodic(
        context: android.content.Context,
        workName: String,
        request: PeriodicWorkRequest,
        policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.UPDATE
    ) {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(workName, policy, request)
    }

    fun logStart(tag: String, message: String) { Log.d(tag, message) }
    fun logError(tag: String, t: Throwable, message: String) { Log.e(tag, message, t) }
    
    /**
     * Create a notification for foreground workers.
     */
    fun createNotification(
        context: Context,
        channelId: String,
        channelName: String,
        title: String,
        content: String,
        isOngoing: Boolean = false
    ): Notification {
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background task notifications"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
        
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_rostry_logo)
            .setContentTitle(title)
            .setContentText(content)
            .setOngoing(isOngoing)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}

