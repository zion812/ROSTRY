package com.rio.rostry.notifications

import android.util.Log
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.entity.NotificationEntity
import com.rio.rostry.domain.manager.DegradationManager
import com.rio.rostry.domain.manager.DegradedService
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notification types for different event categories
 */
enum class NotificationType(val domain: String) {
    VERIFICATION_COMPLETE("TRANSFER"),
    TRANSFER_RECEIVED("TRANSFER"),
    ORDER_STATUS_CHANGED("ORDER"),
    LIFECYCLE_EVENT("FARM"),
    DISPUTE_OPENED("ORDER"),
    DISPUTE_RESOLVED("ORDER"),
    KYC_STATUS_CHANGED("TRANSFER"),
    SYSTEM_ALERT("SYSTEM")
}

/**
 * User notification preferences
 */
data class NotificationPreferences(
    val verificationsEnabled: Boolean = true,
    val transfersEnabled: Boolean = true,
    val ordersEnabled: Boolean = true,
    val lifecycleEnabled: Boolean = true,
    val batchingEnabled: Boolean = false,
    val quietHoursStart: Int = 22, // 10 PM
    val quietHoursEnd: Int = 7    // 7 AM
)

/**
 * Notification Trigger Service.
 *
 * Implements notification triggers for key events:
 * - Verification completion → notify product owner
 * - Transfer receipt → notify recipient
 * - Order status change → notify buyer
 * - Lifecycle events → notify relevant users
 * - Dispute events → notify affected parties
 * - KYC status changes → notify user
 *
 * Also handles:
 * - User preference checking before sending
 * - Notification batching to avoid spam
 * - Delivery within 60 seconds of event
 * - Queueing when notification service is unavailable
 *
 * Requirements: 19.1-19.8
 */
@Singleton
class NotificationTriggerService @Inject constructor(
    private val notificationDao: NotificationDao,
    private val degradationManager: DegradationManager
) {
    companion object {
        private const val TAG = "NotificationTrigger"
        private const val MAX_BATCH_SIZE = 5
        private const val BATCH_WINDOW_MS = 60_000L // 1 minute
    }

    // In-memory notification preferences (would typically be stored in DB/SharedPrefs)
    private val userPreferences = mutableMapOf<String, NotificationPreferences>()
    private val pendingBatch = mutableMapOf<String, MutableList<NotificationEntity>>()

    // ─── Event Triggers ─────────────────────────────────────────────────

    /**
     * Trigger notification when a product verification is completed.
     */
    suspend fun onVerificationComplete(
        productOwnerId: String,
        productId: String,
        productName: String
    ) {
        sendNotification(
            userId = productOwnerId,
            type = NotificationType.VERIFICATION_COMPLETE,
            title = "Verification Complete",
            message = "Your product '$productName' has been verified successfully.",
            deepLink = "rostry://products/$productId"
        )
    }

    /**
     * Trigger notification when a transfer is received.
     */
    suspend fun onTransferReceived(
        recipientId: String,
        senderId: String,
        productName: String,
        transferId: String
    ) {
        sendNotification(
            userId = recipientId,
            type = NotificationType.TRANSFER_RECEIVED,
            title = "Transfer Received",
            message = "You received '$productName' via transfer.",
            deepLink = "rostry://transfers/$transferId"
        )
    }

    /**
     * Trigger notification when order status changes.
     */
    suspend fun onOrderStatusChanged(
        buyerId: String,
        orderId: String,
        newStatus: String
    ) {
        val statusMessage = when (newStatus) {
            "CONFIRMED" -> "Your order has been confirmed"
            "PROCESSING" -> "Your order is being processed"
            "OUT_FOR_DELIVERY" -> "Your order is out for delivery"
            "DELIVERED" -> "Your order has been delivered"
            "CANCELLED" -> "Your order has been cancelled"
            else -> "Your order status has been updated to $newStatus"
        }

        sendNotification(
            userId = buyerId,
            type = NotificationType.ORDER_STATUS_CHANGED,
            title = "Order Update",
            message = statusMessage,
            deepLink = "rostry://orders/$orderId"
        )
    }

    /**
     * Trigger notification for lifecycle events (stage transitions, health alerts).
     */
    suspend fun onLifecycleEvent(
        userId: String,
        productName: String,
        eventType: String,
        details: String
    ) {
        sendNotification(
            userId = userId,
            type = NotificationType.LIFECYCLE_EVENT,
            title = "Lifecycle Update: $productName",
            message = details,
            deepLink = null
        )
    }

    /**
     * Trigger notification when a dispute is opened.
     */
    suspend fun onDisputeOpened(
        sellerId: String,
        orderId: String,
        reason: String
    ) {
        sendNotification(
            userId = sellerId,
            type = NotificationType.DISPUTE_OPENED,
            title = "Dispute Opened",
            message = "A dispute has been opened for order: $reason",
            deepLink = "rostry://orders/$orderId"
        )
    }

    /**
     * Trigger notification when a dispute is resolved.
     */
    suspend fun onDisputeResolved(
        buyerId: String,
        sellerId: String,
        resolution: String
    ) {
        // Notify both parties
        sendNotification(
            userId = buyerId,
            type = NotificationType.DISPUTE_RESOLVED,
            title = "Dispute Resolved",
            message = "Your dispute has been resolved: $resolution",
            deepLink = null
        )
        sendNotification(
            userId = sellerId,
            type = NotificationType.DISPUTE_RESOLVED,
            title = "Dispute Resolved",
            message = "A dispute has been resolved: $resolution",
            deepLink = null
        )
    }

    /**
     * Trigger notification for KYC status changes.
     */
    suspend fun onKycStatusChanged(
        userId: String,
        status: String,
        reason: String? = null
    ) {
        val message = when (status) {
            "APPROVED" -> "Your KYC verification has been approved! You can now access all features."
            "REJECTED" -> "Your KYC verification was not approved. ${reason ?: "Please resubmit with valid documents."}"
            "REQUIRES_REVIEW" -> "Your KYC verification requires additional review."
            else -> "Your KYC status has been updated to $status."
        }

        sendNotification(
            userId = userId,
            type = NotificationType.KYC_STATUS_CHANGED,
            title = "KYC Verification Update",
            message = message,
            deepLink = "rostry://kyc"
        )
    }

    // ─── Preferences Management ─────────────────────────────────────────

    /**
     * Update notification preferences for a user.
     */
    fun updatePreferences(userId: String, preferences: NotificationPreferences) {
        userPreferences[userId] = preferences
        Log.i(TAG, "Updated notification preferences for user $userId")
    }

    /**
     * Get notification preferences for a user.
     */
    fun getPreferences(userId: String): NotificationPreferences {
        return userPreferences[userId] ?: NotificationPreferences()
    }

    // ─── Core Send Logic ────────────────────────────────────────────────

    /**
     * Core notification send function with preference checking, batching, and degradation handling.
     */
    private suspend fun sendNotification(
        userId: String,
        type: NotificationType,
        title: String,
        message: String,
        deepLink: String?
    ) {
        // 1. Check user preferences
        val prefs = getPreferences(userId)
        if (!isNotificationEnabled(prefs, type)) {
            Log.d(TAG, "Notification of type $type disabled for user $userId")
            return
        }

        // 2. Create notification entity
        val notification = NotificationEntity(
            notificationId = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            createdAt = System.currentTimeMillis(),
            isBatched = prefs.batchingEnabled,
            batchedAt = if (prefs.batchingEnabled) System.currentTimeMillis() else null,
            domain = type.domain,
            userPreferenceEnabled = true
        )

        // 3. Check if notification service is degraded
        if (degradationManager.isDegraded(DegradedService.NOTIFICATIONS)) {
            // Queue for later delivery
            notification.let {
                notificationDao.insertNotification(it.copy(isBatched = true, batchedAt = System.currentTimeMillis()))
            }
            Log.d(TAG, "Notification queued (service degraded) for user $userId: $title")
            return
        }

        // 4. Handle batching
        if (prefs.batchingEnabled) {
            addToBatch(userId, notification)
            return
        }

        // 5. Send immediately
        notificationDao.insertNotification(notification)
        Log.i(TAG, "Notification sent to user $userId: $title (${type.name})")
    }

    /**
     * Check if a notification type is enabled for a user.
     */
    private fun isNotificationEnabled(prefs: NotificationPreferences, type: NotificationType): Boolean {
        return when (type) {
            NotificationType.VERIFICATION_COMPLETE -> prefs.verificationsEnabled
            NotificationType.TRANSFER_RECEIVED -> prefs.transfersEnabled
            NotificationType.ORDER_STATUS_CHANGED -> prefs.ordersEnabled
            NotificationType.LIFECYCLE_EVENT -> prefs.lifecycleEnabled
            NotificationType.DISPUTE_OPENED -> prefs.ordersEnabled
            NotificationType.DISPUTE_RESOLVED -> prefs.ordersEnabled
            NotificationType.KYC_STATUS_CHANGED -> prefs.verificationsEnabled
            NotificationType.SYSTEM_ALERT -> true // Always enabled
        }
    }

    /**
     * Add notification to batch for deferred delivery.
     */
    private suspend fun addToBatch(userId: String, notification: NotificationEntity) {
        val batch = pendingBatch.getOrPut(userId) { mutableListOf() }
        batch.add(notification)

        // Flush batch if it reaches max size
        if (batch.size >= MAX_BATCH_SIZE) {
            flushBatch(userId)
        }
    }

    /**
     * Flush batched notifications for a user.
     */
    suspend fun flushBatch(userId: String) {
        val batch = pendingBatch.remove(userId) ?: return
        if (batch.isEmpty()) return

        notificationDao.insertNotifications(batch)
        Log.i(TAG, "Flushed ${batch.size} batched notifications for user $userId")
    }

    /**
     * Flush all pending batches (called periodically or on connectivity restore).
     */
    suspend fun flushAllBatches() {
        val userIds = pendingBatch.keys.toList()
        for (userId in userIds) {
            flushBatch(userId)
        }
    }
}
