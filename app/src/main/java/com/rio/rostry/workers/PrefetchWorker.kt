package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.rio.rostry.utils.network.FeatureToggles
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PrefetchWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val imageLoader: ImageLoader,
    private val toggles: FeatureToggles,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!toggles.prefetchAllowed()) return Result.success()
        // TODO: plug in real URLs/data from repositories to prefetch frequently used images/content
        // Example: prefetch app icon as a placeholder demonstration
        val urls = listOf(
            "https://example.com/static/rostry-placeholder.jpg"
        )
        urls.forEach { url ->
            val req = ImageRequest.Builder(applicationContext)
                .data(url)
                .build()
            imageLoader.enqueue(req)
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "PrefetchWorkerDaily"
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .build()
            val req = PeriodicWorkRequestBuilder<PrefetchWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
    }
}
