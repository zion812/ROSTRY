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
import com.rio.rostry.data.database.dao.AnalyticsDao
import com.rio.rostry.data.database.dao.ReportsDao
import com.rio.rostry.data.database.entity.ReportEntity
import com.rio.rostry.utils.export.CsvExporter
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.notif.AnalyticsNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import java.util.concurrent.TimeUnit

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
        val now = LocalDate.now()
        val from = now.minusDays(7)

        val rows = analyticsDao.listRange(userId, from.toString(), now.toString()).map { a ->
            listOf(
                a.dateKey,
                a.ordersCount.toString(),
                a.salesRevenue.toString(),
                a.likesCount.toString(),
                a.commentsCount.toString()
            )
        }
        val uri = CsvExporter.writeCsv(
            applicationContext,
            fileName = "weekly_report_${now}.csv",
            headers = listOf("date","orders","revenue","likes","comments"),
            rows = rows
        )
        val report = ReportEntity(
            reportId = UUID.randomUUID().toString(),
            userId = userId,
            type = "WEEKLY",
            periodStart = from.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
            periodEnd = now.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
            format = "CSV",
            uri = uri.toString(),
            createdAt = System.currentTimeMillis()
        )
        reportsDao.upsert(report)
        analyticsNotifier.showInsight(
            title = "Weekly report ready",
            message = "Your weekly analytics report has been generated."
        )
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "ReportingWorkerWeekly"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            val req = PeriodicWorkRequestBuilder<ReportingWorker>(7, TimeUnit.DAYS)
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
