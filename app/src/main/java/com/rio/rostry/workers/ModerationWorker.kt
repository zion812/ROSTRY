package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.CommentsDao
import com.rio.rostry.data.database.dao.ModerationReportsDao
import com.rio.rostry.data.database.dao.PostsDao
import com.rio.rostry.data.database.entity.ModerationReportEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.UUID

@HiltWorker
class ModerationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val postsDao: PostsDao,
    private val commentsDao: CommentsDao,
    private val reportsDao: ModerationReportsDao,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            scanPosts()
            scanComments()
            Result.success()
        } catch (t: Throwable) {
            Timber.e(t, "ModerationWorker failed")
            Result.retry()
        }
    }

    private suspend fun scanPosts() {
        val now = System.currentTimeMillis()
        val banned = listOf("spam", "scam", "fraud", "hate", "abuse")
        val top = try {
            postsDao.getTrending(50)
        } catch (_: Exception) {
            emptyList()
        }
        top.forEach { post ->
            val text = (post.text ?: "").lowercase()
            val hit = banned.any { bad -> text.contains(bad) }
            if (hit) {
                val report = ModerationReportEntity(
                    reportId = java.util.UUID.randomUUID().toString(),
                    targetType = "POST",
                    targetId = post.postId,
                    reporterId = "system",
                    reason = "Auto-flag: policy keyword match",
                    status = "OPEN",
                    createdAt = now,
                    updatedAt = now
                )
                reportsDao.upsert(report)
            }
        }
    }

    private suspend fun scanComments() {
        val now = System.currentTimeMillis()
        val banned = listOf("spam", "scam", "fraud", "hate", "abuse")
        val top = try {
            postsDao.getTrending(30)
        } catch (_: Exception) { emptyList() }
        for (post in top) {
            val comments = try { commentsDao.streamByPost(post.postId).first() } catch (_: Exception) { emptyList() }
            comments.forEach { c ->
                val hit = banned.any { bad -> c.text.lowercase().contains(bad) }
                if (hit) {
                    val report = ModerationReportEntity(
                        reportId = java.util.UUID.randomUUID().toString(),
                        targetType = "COMMENT",
                        targetId = c.commentId,
                        reporterId = "system",
                        reason = "Auto-flag: policy keyword match",
                        status = "OPEN",
                        createdAt = now,
                        updatedAt = now
                    )
                    reportsDao.upsert(report)
                }
            }
        }
    }

    companion object {
        const val WORK_NAME = "moderation_scanner"
        fun schedule(context: Context) {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            val request = androidx.work.PeriodicWorkRequestBuilder<ModerationWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
