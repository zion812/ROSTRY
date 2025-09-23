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
import com.rio.rostry.data.database.dao.CommentsDao
import com.rio.rostry.data.database.dao.LikesDao
import com.rio.rostry.data.database.entity.AnalyticsDailyEntity
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.notif.AnalyticsNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@HiltWorker
class AnalyticsAggregationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val analyticsDao: AnalyticsDao,
    private val likesDao: LikesDao,
    private val commentsDao: CommentsDao,
    private val currentUserProvider: CurrentUserProvider,
    private val analyticsNotifier: AnalyticsNotifier,
) : CoroutineWorker(context.applicationContext, params) {

    override suspend fun doWork(): Result {
        val userId = currentUserProvider.userIdOrNull() ?: return Result.success()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        val likes = likesDao.countByUser(userId)
        val comments = commentsDao.countByUser(userId)
        val entity = AnalyticsDailyEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            role = "GENERAL",
            dateKey = today,
            salesRevenue = 0.0,
            ordersCount = 0,
            productViews = 0,
            likesCount = likes,
            commentsCount = comments,
            transfersCount = 0,
            breedingSuccessRate = 0.0,
            engagementScore = likes + comments.toDouble(),
            createdAt = System.currentTimeMillis()
        )
        analyticsDao.upsertDaily(entity)
        val engagement = likes + comments
        if (engagement >= 10) {
            analyticsNotifier.showInsight(
                title = "Great engagement today!",
                message = "You received $engagement interactions. Keep it up!"
            )
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "AnalyticsAggregationDaily"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            val req = PeriodicWorkRequestBuilder<AnalyticsAggregationWorker>(1, java.util.concurrent.TimeUnit.DAYS)
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
