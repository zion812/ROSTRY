package com.rio.rostry.workers

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.ReportsDao
import com.rio.rostry.data.database.entity.ReportEntity
import com.rio.rostry.utils.export.CsvExporter
import com.rio.rostry.utils.export.PdfExporter
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.notif.AnalyticsNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Worker for generating weekly analytics reports.
 * 
 * Supports multiple output formats:
 * - CSV (default): Comma-separated values for spreadsheet import
 * - PDF: Formatted document for viewing/printing
 * 
 * Input Data:
 * - format: "CSV" (default) or "PDF"
 * - days: Number of days to include (default: 7)
 */
@HiltWorker
class ReportingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val analyticsDao: AnalyticsDao,
    private val reportsDao: ReportsDao,
    private val currentUserProvider: CurrentUserProvider,
    private val analyticsNotifier: AnalyticsNotifier,
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val userId = currentUserProvider.userIdOrNull() ?: return Result.success()
        
        // Read parameters from input data
        val format = inputData.getString(KEY_FORMAT) ?: FORMAT_CSV
        val days = inputData.getInt(KEY_DAYS, 7).coerceIn(1, 90)
        
        Timber.d("ReportingWorker: Generating $format report for last $days days")
        
        return try {
            val now = LocalDate.now()
            val from = now.minusDays(days.toLong())
            
            // Fetch analytics data
            val rows = analyticsDao.listRange(userId, from.toString(), now.toString()).map { a ->
                listOf(
                    a.dateKey,
                    a.ordersCount.toString(),
                    formatCurrency(a.salesRevenue),
                    a.likesCount.toString(),
                    a.commentsCount.toString()
                )
            }
            
            val headers = listOf("Date", "Orders", "Revenue", "Likes", "Comments")
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fileName = "report_${from.format(dateFormatter)}_to_${now.format(dateFormatter)}"
            
            // Generate report in requested format
            val uri: Uri = when (format.uppercase()) {
                FORMAT_PDF -> generatePdfReport(fileName, now, from, headers, rows)
                else -> generateCsvReport(fileName, headers, rows)
            }
            
            // Save report record
            val report = ReportEntity(
                reportId = UUID.randomUUID().toString(),
                userId = userId,
                type = if (days == 7) "WEEKLY" else "CUSTOM",
                periodStart = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                periodEnd = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                format = format.uppercase(),
                uri = uri.toString(),
                createdAt = System.currentTimeMillis()
            )
            reportsDao.upsert(report)
            
            // Notify user
            analyticsNotifier.showInsight(
                title = "${format.uppercase()} report ready",
                message = "Your ${if (days == 7) "weekly" else "$days-day"} analytics report has been generated."
            )
            
            Timber.d("ReportingWorker: Report generated successfully - $uri")
            Result.success()
            
        } catch (e: Exception) {
            Timber.e(e, "ReportingWorker: Failed to generate report")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
    
    private fun generateCsvReport(
        fileName: String,
        headers: List<String>,
        rows: List<List<String>>
    ): Uri {
        return CsvExporter.writeCsv(
            applicationContext,
            fileName = "$fileName.csv",
            headers = headers,
            rows = rows
        )
    }
    
    private fun generatePdfReport(
        fileName: String,
        now: LocalDate,
        from: LocalDate,
        headers: List<String>,
        rows: List<List<String>>
    ): Uri {
        val title = "ROSTRY Analytics Report\n${from} to ${now}"
        return PdfExporter.writeSimpleTable(
            applicationContext,
            fileName = "$fileName.pdf",
            title = title,
            headers = headers,
            rows = rows
        )
    }
    
    private fun formatCurrency(amount: Double): String {
        return "₹%.2f".format(amount)
    }

    companion object {
        const val WORK_NAME = "ReportingWorkerWeekly"
        
        // Input data keys
        const val KEY_FORMAT = "format"
        const val KEY_DAYS = "days"
        
        // Format constants
        const val FORMAT_CSV = "CSV"
        const val FORMAT_PDF = "PDF"
        
        /**
         * Schedule weekly report generation (runs every 7 days).
         * Default format: CSV
         */
        fun schedule(context: Context, format: String = FORMAT_CSV) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val inputData = Data.Builder()
                .putString(KEY_FORMAT, format)
                .putInt(KEY_DAYS, 7)
                .build()
            
            val req = PeriodicWorkRequestBuilder<ReportingWorker>(7, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .addTag("reporting_worker")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }
        
        /**
         * Generate a one-time report with custom parameters.
         * 
         * @param context Application context
         * @param format Report format: "CSV" or "PDF"
         * @param days Number of days to include in report
         */
        fun generateNow(context: Context, format: String = FORMAT_CSV, days: Int = 7) {
            val inputData = Data.Builder()
                .putString(KEY_FORMAT, format)
                .putInt(KEY_DAYS, days)
                .build()
            
            val request = OneTimeWorkRequestBuilder<ReportingWorker>()
                .setInputData(inputData)
                .addTag("reporting_worker_onetime")
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                "ReportingWorkerOneTime",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
