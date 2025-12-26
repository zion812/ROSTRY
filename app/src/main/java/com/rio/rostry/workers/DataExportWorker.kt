package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.service.BackupService
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DataExportWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupService: BackupService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("DataExportWorker: Starting manual data export")
        
        return when (val result = backupService.exportUserData()) {
            is Resource.Success -> {
                val file = result.data
                if (file != null) {
                    FarmNotifier.notifyBackupComplete(context, file.absolutePath)
                    Result.success()
                } else {
                    Result.failure()
                }
            }
            is Resource.Error -> {
                Timber.e("DataExportWorker: Export failed - ${result.message}")
                Result.failure()
            }
            is Resource.Loading -> {
                // Should not happen in worker
                Result.retry()
            }
        }
    }

    companion object {
        private const val WORK_NAME = "manual_data_export_worker"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<DataExportWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
