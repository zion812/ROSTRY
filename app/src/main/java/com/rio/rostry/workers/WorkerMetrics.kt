package com.rio.rostry.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker.Result
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for unified worker observability.
 * 
 * Provides:
 * - Execution timing and tracking
 * - Success/failure metrics
 * - Run count tracking
 * - Last run timestamp recording
 * 
 * Usage:
 * ```kotlin
 * override suspend fun doWork(): Result = workerMetrics.trackExecution("MyWorker") {
 *     // ... actual work
 *     Result.success()
 * }
 * ```
 */
@Singleton
class WorkerMetrics @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsTracker: FlowAnalyticsTracker
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Track worker execution with timing, success/failure metrics.
     * 
     * @param workerName Name of the worker (e.g., "LifecycleWorker")
     * @param block The actual work to execute
     * @return The Result from the work block
     */
    suspend fun trackExecution(
        workerName: String,
        block: suspend () -> Result
    ): Result {
        val startTime = System.currentTimeMillis()
        incrementRunCount(workerName)
        
        Timber.d("Worker[$workerName] starting execution #${getRunCount(workerName)}")
        
        return try {
            val result = block()
            val duration = System.currentTimeMillis() - startTime
            
            recordLastRun(workerName, startTime)
            
            analyticsTracker.trackEvent("worker_completed", mapOf(
                "worker" to workerName,
                "duration_ms" to duration,
                "result" to result.toString(),
                "run_count" to getRunCount(workerName)
            ))
            
            Timber.d("Worker[$workerName] completed in ${duration}ms with result: $result")
            
            result
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            
            analyticsTracker.trackEvent("worker_failed", mapOf(
                "worker" to workerName,
                "duration_ms" to duration,
                "error" to (e.message ?: "unknown"),
                "error_type" to e.javaClass.simpleName
            ))
            
            Timber.e(e, "Worker[$workerName] failed after ${duration}ms")
            throw e
        }
    }
    
    /**
     * Track worker execution with additional custom metrics.
     */
    suspend fun trackExecutionWithMetrics(
        workerName: String,
        block: suspend () -> Pair<Result, Map<String, Any>>
    ): Result {
        val startTime = System.currentTimeMillis()
        incrementRunCount(workerName)
        
        return try {
            val (result, customMetrics) = block()
            val duration = System.currentTimeMillis() - startTime
            
            recordLastRun(workerName, startTime)
            
            val metrics = mutableMapOf<String, Any>(
                "worker" to workerName,
                "duration_ms" to duration,
                "result" to result.toString(),
                "run_count" to getRunCount(workerName)
            )
            metrics.putAll(customMetrics)
            
            analyticsTracker.trackEvent("worker_completed", metrics)
            
            result
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            
            analyticsTracker.trackEvent("worker_failed", mapOf(
                "worker" to workerName,
                "duration_ms" to duration,
                "error" to (e.message ?: "unknown")
            ))
            
            throw e
        }
    }
    
    /**
     * Get the total run count for a worker.
     */
    fun getRunCount(workerName: String): Long {
        return prefs.getLong("${workerName}_run_count", 0L)
    }
    
    /**
     * Get the last run timestamp for a worker.
     */
    fun getLastRunTime(workerName: String): Long {
        return prefs.getLong("${workerName}_last_run", 0L)
    }
    
    /**
     * Get worker stats for display/debugging.
     */
    fun getWorkerStats(workerName: String): WorkerStats {
        return WorkerStats(
            workerName = workerName,
            runCount = getRunCount(workerName),
            lastRunTime = getLastRunTime(workerName)
        )
    }
    
    /**
     * Get all worker stats.
     */
    fun getAllWorkerStats(): List<WorkerStats> {
        return KNOWN_WORKERS.map { getWorkerStats(it) }
    }
    
    private fun incrementRunCount(workerName: String) {
        val current = getRunCount(workerName)
        prefs.edit().putLong("${workerName}_run_count", current + 1).apply()
    }
    
    private fun recordLastRun(workerName: String, timestamp: Long) {
        prefs.edit().putLong("${workerName}_last_run", timestamp).apply()
    }
    
    data class WorkerStats(
        val workerName: String,
        val runCount: Long,
        val lastRunTime: Long
    )
    
    companion object {
        private const val PREFS_NAME = "worker_metrics"
        
        // Known worker names for stats collection
        private val KNOWN_WORKERS = listOf(
            "LifecycleWorker",
            "FarmAlertWorker",
            "FarmPerformanceWorker",
            "ReportingWorker",
            "AnalyticsAggregationWorker",
            "BatchGraduationWorker",
            "CommunityEngagementWorker",
            "StorageQuotaMonitorWorker",
            "DataCleanupWorker",
            "DataExportWorker",
            "PersonalizationWorker"
        )
    }
}
