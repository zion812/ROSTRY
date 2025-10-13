package com.rio.rostry.workers

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
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
}
