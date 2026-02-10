package com.rio.rostry.domain.lifecycle

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.repository.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * LifecycleUpdateWorker - Background job to update bird lifecycle stages daily.
 * 
 * Runs once per day to:
 * 1. Query all birds with birth dates
 * 2. Recalculate age and lifecycle stage
 * 3. Persist updates to the database
 * 4. Mark birds nearing transitions for notifications
 * 
 * Scheduling:
 * ```kotlin
 * LifecycleUpdateWorker.schedule(context)
 * ```
 */
@HiltWorker
class LifecycleUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productRepository: ProductRepository,
    private val lifecycleManager: BirdLifecycleManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "lifecycle_update_worker"
        private const val REPEAT_INTERVAL_HOURS = 24L

        /**
         * Schedules the daily lifecycle update worker.
         */
        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<LifecycleUpdateWorker>(
                REPEAT_INTERVAL_HOURS, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        /**
         * Cancels the lifecycle update worker.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get all birds with birth dates
            val allBirds = productRepository.getAllBirdsWithBirthDate()
            
            if (allBirds.isEmpty()) {
                return@withContext Result.success()
            }

            // Update lifecycle stages
            val updatedBirds = lifecycleManager.updateStages(allBirds)
            
            // Find birds that actually changed
            val birdsToUpdate = updatedBirds.filter { updated ->
                val original = allBirds.find { it.productId == updated.productId }
                original != null && (
                    original.stage != updated.stage ||
                    original.ageWeeks != updated.ageWeeks
                )
            }

            // Batch update changed birds
            if (birdsToUpdate.isNotEmpty()) {
                productRepository.updateBirdsLifecycle(birdsToUpdate)
            }

            // Log birds nearing transition (for future notification feature)
            val nearingTransition = lifecycleManager.getBirdsNearingTransition(updatedBirds, withinDays = 3)
            if (nearingTransition.isNotEmpty()) {
                // TODO: Trigger notifications for stage transitions
                android.util.Log.d("LifecycleWorker", "${nearingTransition.size} birds nearing stage transition")
            }

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("LifecycleWorker", "Failed to update lifecycle stages", e)
            Result.retry()
        }
    }
}
