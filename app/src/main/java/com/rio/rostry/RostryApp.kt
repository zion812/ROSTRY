package com.rio.rostry

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rio.rostry.workers.SyncWorker
import com.rio.rostry.workers.LifecycleWorker
import com.rio.rostry.workers.TransferTimeoutWorker
import com.rio.rostry.workers.ModerationWorker
import com.rio.rostry.workers.OutgoingMessageWorker
import coil.Coil
import coil.ImageLoader
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class RostryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Coil global ImageLoader with caching
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .respectCacheHeaders(false)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
            .build()
        Coil.setImageLoader(imageLoader)

        // Schedule periodic sync (every 6 hours, requires network)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

        // Schedule lifecycle milestone worker (daily)
        LifecycleWorker.schedule(this)

        // Schedule transfer timeout checker (every ~15 minutes)
        TransferTimeoutWorker.schedule(this)

        // Schedule moderation scanner (every 6 hours)
        ModerationWorker.schedule(this)

        // Schedule outgoing message sender (every 15 minutes, requires network)
        OutgoingMessageWorker.schedule(this)
    }
}
