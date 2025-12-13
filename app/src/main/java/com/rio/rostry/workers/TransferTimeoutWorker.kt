package com.rio.rostry.workers
  
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.TransferEventType
import com.rio.rostry.utils.notif.TransferNotifier
import com.rio.rostry.data.database.dao.PaymentDao
import com.rio.rostry.data.repository.PaymentRepository
import com.rio.rostry.data.repository.monitoring.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID
import java.util.concurrent.TimeUnit
  
@HiltWorker
class TransferTimeoutWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val transferDao: TransferDao,
    private val auditLogDao: AuditLogDao,
    private val notifier: TransferNotifier,
    private val paymentDao: PaymentDao,
    private val paymentRepository: PaymentRepository,
    private val intelligentNotificationService: IntelligentNotificationService,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(appContext, params) {
  
    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            val timedOut = transferDao.getPendingTimedOut(now)
            if (timedOut.isNotEmpty()) {
                transferDao.markTimedOut(timedOut.map { it.transferId }, updatedAt = now)
                timedOut.forEach { t ->
                    auditLogDao.insert(
                        AuditLogEntity(
                            logId = UUID.randomUUID().toString(),
                            type = "TRANSFER",
                            refId = t.transferId,
                            action = "TIMEOUT",
                            actorUserId = null,
                            detailsJson = null,
                            createdAt = now
                        )
                    )
                    // Notify timeout explicitly
                    val message = "Transfer ${t.transferId} has timed out. Order: ${t.orderId ?: "N/A"}, Product: ${t.productId ?: "N/A"}, From: ${t.fromUserId}, To: ${t.toUserId ?: "N/A"}"
                    val transferId = t.transferId ?: return@forEach
                    val fromUserId = t.fromUserId ?: ""
                    intelligentNotificationService.notifyTransferEvent(TransferEventType.TIMED_OUT, transferId, "Transfer Timed Out", message)
                    taskRepository.generateTransferTimeoutTask(transferId, fromUserId, t.orderId, now)
  
                    // Issue refund idempotently if linked to an order with a payment
                    try {
                        val orderId = t.orderId
                        if (orderId != null) {
                            val payment = paymentDao.findLatestByOrder(orderId)
                            if (payment != null) {
                                // refundPayment handles idempotency via totals
                                paymentRepository.refundPayment(payment.paymentId, reason = "Transfer timed out")
                            }
                        }
                    } catch (_: Exception) { /* best-effort refund */ }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
  
    companion object {
        private const val UNIQUE_NAME = "transfer_timeout_worker"
        fun schedule(context: Context) {
            // Daily schedule per requirements
            val req = PeriodicWorkRequestBuilder<TransferTimeoutWorker>(1, TimeUnit.DAYS)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .addTag("session_worker")
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, req
            )
        }
    }
}
