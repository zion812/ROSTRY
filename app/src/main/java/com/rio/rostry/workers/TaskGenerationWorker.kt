package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.workers.processors.TaskGenerationProcessor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskGenerationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val taskGenerationProcessor: TaskGenerationProcessor
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        return try {
            taskGenerationProcessor.process(now)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
