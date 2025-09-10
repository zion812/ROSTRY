package com.rio.rostry

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit
import com.rio.rostry.data.work.OutboxSyncWorker
import com.rio.rostry.data.work.PullSyncWorker

@HiltAndroidApp
class ROSTRYApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }

        // Schedule periodic outbox sync (every 1 hour, only when connected)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<OutboxSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "outbox_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        // Schedule periodic pull sync (every 2 hours, only when connected)
        val pullConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val pullRequest = PeriodicWorkRequestBuilder<PullSyncWorker>(2, TimeUnit.HOURS)
            .setConstraints(pullConstraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "pull_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            pullRequest
        )
    }
}
