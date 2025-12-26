package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.data.migration.RoleUpgradeMigrationService
import com.rio.rostry.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for executing role upgrade migrations.
 * 
 * Uses WorkManager for:
 * - Guaranteed execution
 * - Network constraint handling
 * - Retry with backoff
 * - Progress reporting
 * - Cancellation support
 */
@HiltWorker
class RoleUpgradeMigrationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val migrationService: RoleUpgradeMigrationService
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "RoleUpgradeMigrationWorker"
        
        // Input data keys
        const val KEY_MIGRATION_ID = "migration_id"
        const val KEY_USER_ID = "user_id"
        
        // Progress data keys
        const val KEY_PROGRESS_PHASE = "phase"
        const val KEY_PROGRESS_CURRENT = "current"
        const val KEY_PROGRESS_TOTAL = "total"
        const val KEY_PROGRESS_ENTITY = "entity"
        
        // Output data keys
        const val KEY_RESULT_SUCCESS = "success"
        const val KEY_RESULT_ERROR = "error"
        
        // Work name prefix
        const val WORK_NAME_PREFIX = "role_migration_"
        
        /**
         * Create a one-time work request for role migration.
         */
        fun createWorkRequest(
            migrationId: String,
            userId: String
        ): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putString(KEY_MIGRATION_ID, migrationId)
                .putString(KEY_USER_ID, userId)
                .build()
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            return OneTimeWorkRequestBuilder<RoleUpgradeMigrationWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30,
                    TimeUnit.SECONDS
                )
                .addTag("role_migration")
                .addTag(migrationId)
                .build()
        }
        
        /**
         * Get unique work name for a migration.
         */
        fun getWorkName(migrationId: String): String = "$WORK_NAME_PREFIX$migrationId"
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val migrationId = inputData.getString(KEY_MIGRATION_ID)
        val userId = inputData.getString(KEY_USER_ID)
        
        if (migrationId.isNullOrBlank() || userId.isNullOrBlank()) {
            Timber.e("Missing required input data: migrationId=$migrationId, userId=$userId")
            return@withContext Result.failure(
                Data.Builder()
                    .putBoolean(KEY_RESULT_SUCCESS, false)
                    .putString(KEY_RESULT_ERROR, "Missing required input data")
                    .build()
            )
        }
        
        Timber.d("Starting migration work: $migrationId for user $userId")
        
        try {
            // Execute migration with progress updates
            val result = migrationService.executeMigration(migrationId) { phase, current, total ->
                // Report progress to WorkManager
                setProgress(
                    Data.Builder()
                        .putString(KEY_PROGRESS_PHASE, phase)
                        .putInt(KEY_PROGRESS_CURRENT, current)
                        .putInt(KEY_PROGRESS_TOTAL, total)
                        .build()
                )
                
                Timber.d("Migration progress: $phase - $current/$total")
            }
            
            when (result) {
                is Resource.Success -> {
                    Timber.i("Migration $migrationId completed successfully")
                    Result.success(
                        Data.Builder()
                            .putBoolean(KEY_RESULT_SUCCESS, true)
                            .build()
                    )
                }
                is Resource.Error -> {
                    val error = result.message ?: "Unknown error"
                    Timber.e("Migration $migrationId failed: $error")
                    
                    // Check if we should retry
                    val migration = migrationService.getMigrationStatus(migrationId)
                    if (migration?.canRetry == true && runAttemptCount < 3) {
                        Result.retry()
                    } else {
                        Result.failure(
                            Data.Builder()
                                .putBoolean(KEY_RESULT_SUCCESS, false)
                                .putString(KEY_RESULT_ERROR, error)
                                .build()
                        )
                    }
                }
                is Resource.Loading -> {
                    // Should not happen in sync call
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during migration $migrationId")
            
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure(
                    Data.Builder()
                        .putBoolean(KEY_RESULT_SUCCESS, false)
                        .putString(KEY_RESULT_ERROR, e.message ?: "Unknown error")
                        .build()
                )
            }
        }
    }
    
    override suspend fun getForegroundInfo(): ForegroundInfo {
        // Create notification for foreground service
        val notification = WorkerBaseHelper.createNotification(
            context = applicationContext,
            channelId = "migration_channel",
            channelName = "Role Migration",
            title = "Upgrading your account",
            content = "Migrating your farm data...",
            isOngoing = true
        )
        
        return ForegroundInfo(
            inputData.getString(KEY_MIGRATION_ID)?.hashCode() ?: 12345,
            notification
        )
    }
}

/**
 * Extension for WorkManager to enqueue migration work.
 */
fun WorkManager.enqueueMigration(
    migrationId: String,
    userId: String
) {
    val workRequest = RoleUpgradeMigrationWorker.createWorkRequest(migrationId, userId)
    val workName = RoleUpgradeMigrationWorker.getWorkName(migrationId)
    
    enqueueUniqueWork(
        workName,
        ExistingWorkPolicy.KEEP,
        workRequest
    )
}

/**
 * Extension to cancel a migration work.
 */
fun WorkManager.cancelMigration(migrationId: String) {
    cancelUniqueWork(RoleUpgradeMigrationWorker.getWorkName(migrationId))
}

/**
 * Extension to observe migration work progress.
 */
fun WorkManager.getMigrationWorkInfo(migrationId: String) =
    getWorkInfosForUniqueWorkLiveData(RoleUpgradeMigrationWorker.getWorkName(migrationId))
