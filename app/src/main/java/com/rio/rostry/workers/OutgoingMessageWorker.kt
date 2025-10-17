package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.OutgoingMessageDao
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.OutgoingMessageEntity
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.repository.social.MessagingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Deprecated: Use OutboxSyncWorker with unified outbox instead.
 * Migration path: Call migrateToUnifiedOutbox() to move pending messages to outbox,
 * then remove this worker in a future sprint.
 */
@Deprecated("Use OutboxSyncWorker with unified outbox instead", ReplaceWith("OutboxSyncWorker"))
@HiltWorker
class OutgoingMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val queueDao: OutgoingMessageDao,
    private val messagingRepository: MessagingRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pending = queueDao.getByStatus("PENDING", limit = 50)
            for (m in pending) {
                try {
                    when (m.kind) {
                        "DM" -> {
                            if (m.fileUri != null) {
                                messagingRepository.sendDirectFile(
                                    threadId = m.threadOrGroupId,
                                    fromUserId = m.fromUserId,
                                    toUserId = m.toUserId ?: "",
                                    fileUri = android.net.Uri.parse(m.fileUri),
                                    fileName = m.fileName ?: "file"
                                )
                            } else {
                                messagingRepository.sendDirectMessage(
                                    threadId = m.threadOrGroupId,
                                    fromUserId = m.fromUserId,
                                    toUserId = m.toUserId ?: "",
                                    text = m.bodyText ?: ""
                                )
                            }
                        }
                        "GROUP" -> {
                            if (m.fileUri != null) {
                                messagingRepository.sendGroupFile(
                                    groupId = m.threadOrGroupId,
                                    fromUserId = m.fromUserId,
                                    fileUri = android.net.Uri.parse(m.fileUri),
                                    fileName = m.fileName ?: "file"
                                )
                            } else {
                                messagingRepository.sendGroupMessage(
                                    groupId = m.threadOrGroupId,
                                    fromUserId = m.fromUserId,
                                    text = m.bodyText ?: ""
                                )
                            }
                        }
                    }
                    queueDao.updateStatus(m.id, "SENT")
                } catch (t: Throwable) {
                    Timber.w(t, "Failed to send queued message ${m.id}")
                    queueDao.updateStatus(m.id, "FAILED")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "OutgoingMessageWorker fatal error")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "outgoing_message_sender"

        /**
         * Migrates all pending OutgoingMessageEntity entries to the unified OutboxEntity format.
         * Call this before removing the OutgoingMessageWorker.
         */
        suspend fun migrateToUnifiedOutbox(outboxDao: OutboxDao, queueDao: OutgoingMessageDao, gson: Gson) {
            val pending = queueDao.getByStatus("PENDING", limit = Int.MAX_VALUE)
            for (m in pending) {
                val entityType = if (m.kind == "DM") OutboxEntity.TYPE_CHAT_MESSAGE else OutboxEntity.TYPE_GROUP_MESSAGE
                val payload = mapOf(
                    "kind" to m.kind,
                    "threadOrGroupId" to m.threadOrGroupId,
                    "fromUserId" to m.fromUserId,
                    "toUserId" to m.toUserId,
                    "bodyText" to m.bodyText,
                    "fileUri" to m.fileUri,
                    "fileName" to m.fileName
                )
                val payloadJson = gson.toJson(payload)
                val outbox = OutboxEntity(
                    outboxId = UUID.randomUUID().toString(),
                    userId = m.fromUserId,
                    entityType = entityType,
                    entityId = m.id,
                    operation = "SEND",
                    payloadJson = payloadJson,
                    createdAt = System.currentTimeMillis(),
                    priority = "NORMAL",
                    maxRetries = 10
                )
                outboxDao.insert(outbox)
                queueDao.updateStatus(m.id, "MIGRATED")
            }
        }

        fun schedule(context: Context) {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<OutgoingMessageWorker>(15, TimeUnit.MINUTES)
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
