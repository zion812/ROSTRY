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
import com.rio.rostry.data.repository.EvidenceOrderRepository
import com.rio.rostry.domain.model.EvidenceOrderStatus
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.session.CurrentUserProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for handling Evidence-Based Order System expiry and reminders.
 * 
 * Responsibilities:
 * 1. Expire quotes that have passed their expiry date
 * 2. Send payment reminders for orders with pending payments
 * 3. Notify sellers about unverified payments
 * 4. Remind about delivery confirmations
 * 5. Escalate overdue disputes
 */
@HiltWorker
class EvidenceOrderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val evidenceOrderRepository: EvidenceOrderRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val notificationService: IntelligentNotificationService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val userId = currentUserProvider.userIdOrNull() ?: return Result.success()
            
            Timber.d("EvidenceOrderWorker: Processing orders for user $userId")
            
            val now = System.currentTimeMillis()
            var actionsCount = 0
            
            // 1. Expire old quotes (via repository)
            actionsCount += processExpiredQuotes(now)
            
            // 2. Send payment reminders
            actionsCount += sendPaymentReminders(userId, now)
            
            // 3. Notify sellers about unverified payments
            actionsCount += notifyUnverifiedPayments(userId)
            
            // 4. Remind about pending delivery confirmations
            actionsCount += remindPendingDeliveries(userId, now)
            
            // 5. Check and escalate disputes
            actionsCount += checkDisputeEscalation(userId, now)
            
            Timber.d("EvidenceOrderWorker: Completed with $actionsCount actions")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderWorker: Failed")
            Result.retry()
        }
    }
    
    /**
     * Process expired quotes using repository.
     */
    private suspend fun processExpiredQuotes(now: Long): Int {
        return try {
            val result = evidenceOrderRepository.expireOldQuotes(now)
            when (result) {
                is com.rio.rostry.utils.Resource.Success -> {
                    val expiredCount = result.data ?: 0
                    Timber.d("EvidenceOrderWorker: Expired $expiredCount quotes")
                    expiredCount
                }
                else -> 0
            }
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderWorker: Failed to expire quotes")
            0
        }
    }
    
    /**
     * Send payment reminders for orders with pending advance payments.
     */
    private suspend fun sendPaymentReminders(userId: String, now: Long): Int {
        return try {
            val pendingPayments = evidenceOrderRepository.getPendingPaymentsForBuyer(userId).first()
            var remindersCount = 0
            
            pendingPayments.forEach { payment ->
                val ageHours = TimeUnit.MILLISECONDS.toHours(now - payment.createdAt)
                
                // Send reminder at 12h, 24h, 48h
                if (shouldSendReminder(ageHours, listOf(12, 24, 48))) {
                    notificationService.notifyOrderUpdate(
                        orderId = payment.orderId,
                        status = "PAYMENT_REMINDER",
                        title = "Payment Pending",
                        message = "Don't forget to complete your payment of ₹${payment.amount.toInt()} for order #${payment.orderId.takeLast(6)}"
                    )
                    remindersCount++
                }
            }
            
            remindersCount
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderWorker: Failed to send payment reminders")
            0
        }
    }
    
    /**
     * Notify sellers about unverified payments that are waiting.
     */
    private suspend fun notifyUnverifiedPayments(userId: String): Int {
        return try {
            val unverifiedPayments = evidenceOrderRepository.getPaymentsAwaitingVerification(userId).first()
            val now = System.currentTimeMillis()
            var notifiedCount = 0
            
            unverifiedPayments.forEach { payment ->
                val ageHours = TimeUnit.MILLISECONDS.toHours(now - payment.updatedAt)
                
                // Remind at 6h, 12h, 24h
                if (shouldSendReminder(ageHours, listOf(6, 12, 24))) {
                    notificationService.notifyOrderUpdate(
                        orderId = payment.orderId,
                        status = "VERIFICATION_PENDING",
                        title = "Payment Awaiting Verification",
                        message = "A buyer has submitted payment proof (₹${payment.amount.toInt()}). Please verify."
                    )
                    notifiedCount++
                }
            }
            
            notifiedCount
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderWorker: Failed to notify unverified payments")
            0
        }
    }
    
    /**
     * Remind about orders ready for delivery confirmation.
     */
    private suspend fun remindPendingDeliveries(userId: String, now: Long): Int {
        return try {
            val sellerQuotes = evidenceOrderRepository.getSellerActiveQuotes(userId).first()
            
            val readyForDelivery = sellerQuotes.filter { quote ->
                quote.status in listOf(
                    EvidenceOrderStatus.DISPATCHED.value,
                    EvidenceOrderStatus.READY_FOR_PICKUP.value
                )
            }
            
            var remindersCount = 0
            
            readyForDelivery.forEach { quote ->
                // Use updatedAt as proxy for when status changed to DISPATCHED
                val dispatchTime = quote.updatedAt
                val ageHours = TimeUnit.MILLISECONDS.toHours(now - dispatchTime)
                
                // Remind at 24h, 48h, 72h
                if (shouldSendReminder(ageHours, listOf(24, 48, 72))) {
                    notificationService.notifyOrderUpdate(
                        orderId = quote.orderId,
                        status = "DELIVERY_REMINDER",
                        title = "Delivery Confirmation Pending",
                        message = "Order #${quote.orderId.takeLast(6)} is awaiting delivery confirmation"
                    )
                    remindersCount++
                }
            }
            
            remindersCount
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderWorker: Failed to remind pending deliveries")
            0
        }
    }
    
    /**
     * Escalate disputes that have been open for too long.
     */
    private suspend fun checkDisputeEscalation(userId: String, now: Long): Int {
        return try {
            val openDisputes = evidenceOrderRepository.getUserActiveDisputes(userId).first()
            val escalationThreshold = TimeUnit.DAYS.toMillis(3) // Escalate after 3 days
            
            var escalatedCount = 0
            
            openDisputes.filter { it.status == "OPEN" }.forEach { dispute ->
                val ageMillis = now - dispute.createdAt
                
                if (ageMillis >= escalationThreshold) {
                    val result = evidenceOrderRepository.escalateDispute(dispute.disputeId)
                    
                    if (result is com.rio.rostry.utils.Resource.Success) {
                        notificationService.notifyOrderUpdate(
                            orderId = dispute.orderId,
                            status = "DISPUTE_ESCALATED",
                            title = "Dispute Escalated",
                            message = "Your dispute has been escalated for admin review"
                        )
                        escalatedCount++
                        Timber.d("EvidenceOrderWorker: Escalated dispute ${dispute.disputeId}")
                    }
                }
            }
            
            escalatedCount
        } catch (e: Exception) {
            Timber.e(e, "EvidenceOrderWorker: Failed to check dispute escalation")
            0
        }
    }
    
    /**
     * Helper to check if reminder should be sent at specific hour thresholds.
     */
    private fun shouldSendReminder(ageHours: Long, thresholds: List<Int>): Boolean {
        return thresholds.any { threshold -> ageHours >= threshold && ageHours < threshold + 1 }
    }

    companion object {
        private const val WORK_NAME = "EvidenceOrderWorker"
        
        /**
         * Schedule the periodic worker to run every 30 minutes.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<EvidenceOrderWorker>(
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(androidx.work.BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .addTag("session_worker")
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
            
            Timber.d("EvidenceOrderWorker: Scheduled periodic work (every 30 minutes)")
        }
        
        /**
         * Cancel the worker.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Timber.d("EvidenceOrderWorker: Cancelled")
        }
    }
}
