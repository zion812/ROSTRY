package com.rio.rostry.notifications
 
import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.NotificationEntity
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.utils.notif.AnalyticsNotifier
import com.rio.rostry.utils.notif.FarmNotifier
import com.rio.rostry.utils.notif.SocialNotifier
import com.rio.rostry.utils.notif.TransferNotifier
import com.rio.rostry.ui.navigation.RouteConstants
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.utils.network.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton
  
enum class FarmEventType {
    VACCINATION_DUE, QUARANTINE_OVERDUE, GROWTH_CHECK, BATCH_SPLIT, HATCHING_DUE, MORTALITY_SPIKE,
    BIRD_ADDED, BATCH_ADDED, COMPLIANCE_ALERT, DAILY_GOAL_MILESTONE, KYC_REQUIRED
}

enum class TransferEventType {
    INITIATED, VERIFIED, COMPLETED, CANCELLED, DISPUTED, TIMED_OUT, ENTHUSIAST_TRANSFER_PROPOSED
}

enum class SocialEventType {
    NEW_MESSAGE, NEW_COMMENT, NEW_LIKE, NEW_FOLLOW
}

enum class VerificationEventType {
    VERIFICATION_APPROVED, VERIFICATION_REJECTED, VERIFICATION_PENDING, VERIFICATION_SUBMITTED
}

enum class OrderEventType {
    STATUS_CHANGED, PAYMENT_RECEIVED, SHIPPED, DELIVERED, DISPUTED
}

enum class LifecycleEventType {
    BIRD_HATCHED, BIRD_GROWN, BIRD_READY_FOR_MARKET, BIRD_DELETED
}
  
@Singleton
class IntelligentNotificationService @Inject constructor(
    private val notificationDao: NotificationDao,
    private val userDao: UserDao,
    private val taskRepository: TaskRepository,
    private val connectivityManager: ConnectivityManager,
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val transferNotifier: TransferNotifier,
    private val socialNotifier: SocialNotifier,
    private val analyticsNotifier: AnalyticsNotifier,
    private val currentUserProvider: CurrentUserProvider
) {
  
    suspend fun notifyFarmEvent(
        type: FarmEventType,
        productId: String,
        title: String,
        message: String,
        metadata: Map<String, Any>? = null
    ) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "FARM")) return
        
        val notificationId = generateId("notif_farm_")
        val deepLink = generateDeepLink("FARM", productId, type.name)
  
        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "FARM",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )
  
        notificationDao.insertNotification(notification)
  
        // Create task card if applicable
        when (type) {
            FarmEventType.VACCINATION_DUE -> {
                val vaccineType = metadata?.get("vaccineType") as? String ?: System.currentTimeMillis().toString().let { _ -> "" }
                val dueAt = metadata?.get("dueAt") as? Long ?: System.currentTimeMillis()
                taskRepository.generateVaccinationTask(productId, userId, vaccineType, dueAt)
            }
            FarmEventType.QUARANTINE_OVERDUE -> {
                val dueAt = metadata?.get("dueAt") as? Long ?: System.currentTimeMillis()
                taskRepository.generateQuarantineCheckTask(productId, userId, dueAt)
            }
            // Add other task creations as needed
            else -> {
                // no-op for other farm event types
            }
        }
  
        if (connectivityManager.isOnline()) {
            displayFarmNotification(type, productId, title, message, metadata)
            markDisplayed(notificationId)
        }
    }
  
    suspend fun notifyTransferEvent(
        type: TransferEventType,
        transferId: String,
        title: String,
        message: String
    ) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "TRANSFER")) return
        
        val notificationId = generateId("notif_transfer_")
        val deepLink = generateDeepLink("TRANSFER", transferId)
  
        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "TRANSFER",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )
  
        notificationDao.insertNotification(notification)
  
        // Create task card if applicable
        when (type) {
            TransferEventType.TIMED_OUT -> {
                taskRepository.generateTransferTimeoutTask(transferId, userId, null, System.currentTimeMillis())
            }
            // Add other task creations as needed
            else -> {
                // no-op
            }
        }
  
        if (connectivityManager.isOnline()) {
            displayTransferNotification(type, transferId, title, message)
            markDisplayed(notificationId)
        }
    }
  
    suspend fun notifyOrderUpdate(orderId: String, status: String, title: String, message: String) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "ORDER")) return
        
        val notificationId = generateId("notif_order_")
        val deepLink = generateDeepLink("ORDER", orderId)
  
        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = "ORDER_UPDATE",
            deepLinkUrl = deepLink,
            domain = "ORDER",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )
  
        notificationDao.insertNotification(notification)
  
        // Create task card
        taskRepository.generateOrderUpdateTask(orderId, userId, status, System.currentTimeMillis())
  
        if (connectivityManager.isOnline()) {
            displayOrderNotification(orderId, title, message)
            markDisplayed(notificationId)
        }
    }
  
    suspend fun notifyOnboardingComplete(productId: String, productName: String, taskCount: Int, isBatch: Boolean) {
        val type = if (isBatch) FarmEventType.BATCH_ADDED else FarmEventType.BIRD_ADDED
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "FARM")) return
        
        val notificationId = generateId("notif_farm_")
        val deepLink = generateDeepLink("FARM", productId, type.name)
        val title = if (isBatch) "Batch Added" else "Bird Added"
        val message = if (isBatch) "$productName added with $taskCount tasks. Check your task list!" else "$productName has been added to your farm. Start logging daily activities!"

        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "FARM",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )

        notificationDao.insertNotification(notification)

        // No task creation for onboarding, as tasks are already created in FarmOnboardingRepository

        if (connectivityManager.isOnline()) {
            displayFarmNotification(type, productId, title, message, mapOf("productName" to productName, "taskCount" to taskCount))
            markDisplayed(notificationId)
        }
    }

    suspend fun notifyComplianceIssue(productId: String, productName: String) {
        val type = FarmEventType.COMPLIANCE_ALERT
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "FARM")) return
        
        val notificationId = generateId("notif_farm_")
        val deepLink = generateDeepLink("FARM", productId, type.name)
        val title = "Compliance Alert"
        val message = "$productName has compliance issues. Review required."

        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "FARM",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )

        notificationDao.insertNotification(notification)

        // Create task card for actionable compliance event
        taskRepository.generateSocialActivityTask(userId, "COMPLIANCE_ALERT", productId, System.currentTimeMillis())

        if (connectivityManager.isOnline()) {
            displayFarmNotification(type, productId, title, message, mapOf("productName" to productName))
            markDisplayed(notificationId)
        }
    }

    suspend fun notifyGoalProgress(goalType: String, progress: Int) {
        val type = FarmEventType.DAILY_GOAL_MILESTONE
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "FARM")) return

        val notificationId = generateId("notif_farm_")
        val deepLink = generateDeepLink("FARM", "", type.name)
        val title = "Daily Goal Progress"
        val message = "$goalType goal $progress% complete!"

        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "FARM",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )

        notificationDao.insertNotification(notification)

        // No task creation for goal progress

        if (connectivityManager.isOnline()) {
            displayFarmNotification(type, "", title, message, mapOf("goalType" to goalType, "progress" to progress))
            markDisplayed(notificationId)
        }
    }

    /**
     * Notify verification event (APPROVED, REJECTED, PENDING)
     */
    suspend fun notifyVerificationEvent(
        type: VerificationEventType,
        userId: String,
        title: String,
        message: String
    ) {
        if (!shouldNotify(userId, "VERIFICATION")) return

        val notificationId = generateId("notif_verification_")
        val deepLink = "rostry://${RouteConstants.VERIFY_FARMER_LOCATION}"

        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "VERIFICATION",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )

        notificationDao.insertNotification(notification)

        if (connectivityManager.isOnline()) {
            // Display verification notification
            when (type) {
                VerificationEventType.VERIFICATION_APPROVED -> {
                    FarmNotifier.notifyKycRequired(context) // Reuse KYC notification
                }
                VerificationEventType.VERIFICATION_REJECTED,
                VerificationEventType.VERIFICATION_PENDING,
                VerificationEventType.VERIFICATION_SUBMITTED -> {
                    // Use generic analytics notifier for verification messages
                    analyticsNotifier.showInsight(title, message, deepLink)
                }
            }
            markDisplayed(notificationId)
        }
    }

    suspend fun notifySocialEvent(
        type: SocialEventType,
        refId: String,
        title: String,
        message: String,
        fromUser: String
    ) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        if (!shouldNotify(userId, "SOCIAL")) return

        val notificationId = generateId("notif_social_")
        val deepLink = generateDeepLink("SOCIAL", refId, type.name)

        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = type.name,
            deepLinkUrl = deepLink,
            domain = "SOCIAL",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )

        notificationDao.insertNotification(notification)

        // No task for social events

        if (connectivityManager.isOnline()) {
            displaySocialNotification(type, refId, title, message, fromUser)
            markDisplayed(notificationId)
        }
    }

    // ─── New Trigger Methods (Comment 5) ─────────────────────────────────

    /**
     * Trigger notification when verification is completed
     */
    suspend fun onVerificationCompleted(
        userId: String,
        verificationType: String,
        status: String,
        title: String,
        message: String
    ) {
        if (!shouldNotify(userId, "VERIFICATION")) return
        
        val eventType = when (status.uppercase()) {
            "APPROVED" -> VerificationEventType.VERIFICATION_APPROVED
            "REJECTED" -> VerificationEventType.VERIFICATION_REJECTED
            else -> VerificationEventType.VERIFICATION_PENDING
        }
        
        notifyVerificationEvent(eventType, userId, title, message)
    }

    /**
     * Trigger notification when transfer is received
     */
    suspend fun onTransferReceived(
        transferId: String,
        userId: String,
        fromUserName: String,
        productName: String,
        title: String,
        message: String
    ) {
        if (!shouldNotify(userId, "TRANSFER")) return
        
        val notificationId = generateId("notif_transfer_recv_")
        val deepLink = generateDeepLink("TRANSFER", transferId)
        
        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = TransferEventType.INITIATED.name,
            deepLinkUrl = deepLink,
            domain = "TRANSFER",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )
        
        notificationDao.insertNotification(notification)
        
        if (connectivityManager.isOnline()) {
            displayTransferNotification(TransferEventType.INITIATED, transferId, title, message)
            markDisplayed(notificationId)
        }
    }

    /**
     * Trigger notification when order status changes
     */
    suspend fun onOrderStatusChanged(
        orderId: String,
        userId: String,
        oldStatus: String,
        newStatus: String,
        title: String,
        message: String
    ) {
        if (!shouldNotify(userId, "ORDER")) return
        
        notifyOrderUpdate(orderId, newStatus, title, message)
    }

    /**
     * Trigger notification for lifecycle events
     */
    suspend fun onLifecycleEvent(
        eventType: LifecycleEventType,
        productId: String,
        userId: String,
        title: String,
        message: String
    ) {
        if (!shouldNotify(userId, "LIFECYCLE")) return
        
        val notificationId = generateId("notif_lifecycle_")
        val deepLink = generateDeepLink("FARM", productId, eventType.name)
        
        val notification = NotificationEntity(
            notificationId = notificationId,
            userId = userId,
            title = title,
            message = message,
            type = eventType.name,
            deepLinkUrl = deepLink,
            domain = "LIFECYCLE",
            isBatched = !connectivityManager.isOnline(),
            batchedAt = if (!connectivityManager.isOnline()) System.currentTimeMillis() else null
        )
        
        notificationDao.insertNotification(notification)
        
        if (connectivityManager.isOnline()) {
            // Display based on event type
            when (eventType) {
                LifecycleEventType.BIRD_HATCHED -> FarmNotifier.notifyBirdAdded(context, message, productId)
                LifecycleEventType.BIRD_GROWN -> FarmNotifier.notifyDailyGoalProgress(context, "Growth", 100)
                LifecycleEventType.BIRD_READY_FOR_MARKET -> FarmNotifier.notifyComplianceAlert(context, productId, message)
                else -> { /* no-op */ }
            }
            markDisplayed(notificationId)
        }
    }

    // ─── Batching Implementation (Comment 5) ─────────────────────────────
  
    suspend fun queueForBatch(notification: NotificationEntity) {
        val batched = notification.copy(isBatched = true, batchedAt = System.currentTimeMillis())
        notificationDao.insertNotification(batched)
    }
  
    suspend fun flushBatchedNotifications() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        val batched = notificationDao.getBatchedNotifications(userId)
        val now = System.currentTimeMillis()

        for (notif in batched) {
            when (notif.domain) {
                "FARM" -> {
                    val type = FarmEventType.valueOf(notif.type)
                    val productId = notif.deepLinkUrl?.substringAfterLast("/")?.substringAfter("?productId=") ?: ""
                    displayFarmNotification(type, productId, notif.title, notif.message, null)
                }
                "TRANSFER" -> {
                    val type = TransferEventType.valueOf(notif.type)
                    displayTransferNotification(type, notif.deepLinkUrl?.substringAfterLast("/") ?: "", notif.title, notif.message)
                }
                "ORDER" -> {
                    val orderId = notif.deepLinkUrl?.substringAfterLast("/") ?: ""
                    displayOrderNotification(orderId, notif.title, notif.message)
                }
                "SOCIAL" -> {
                    val type = SocialEventType.valueOf(notif.type)
                    when (type) {
                        SocialEventType.NEW_MESSAGE -> {
                            val threadId = notif.deepLinkUrl?.substringAfterLast("/") ?: ""
                            displaySocialNotification(type, threadId, notif.title, notif.message, "")
                        }
                        SocialEventType.NEW_FOLLOW -> {
                            val userIdPath = notif.deepLinkUrl?.substringAfterLast("/") ?: ""
                            displaySocialNotification(type, userIdPath, notif.title, notif.message, "")
                        }
                        SocialEventType.NEW_COMMENT, SocialEventType.NEW_LIKE -> {
                            val postId = notif.deepLinkUrl?.let { Uri.parse(it).getQueryParameter("postId") } ?: ""
                            displaySocialNotification(type, postId, notif.title, notif.message, "")
                        }
                    }
                }
            }
            notificationDao.markBatchDisplayed(listOf(notif.notificationId), now)
        }
    }
  
    fun getBatchedCount(): Flow<Int> {
        val userId = currentUserProvider.userIdOrNull() ?: return flowOf(0)
        return notificationDao.observeBatchedCount(userId)
    }
  
    private fun generateDeepLink(domain: String, refId: String, subRoute: String? = null): String {
        return when (domain) {
            "FARM" -> when (subRoute) {
                "VACCINATION_DUE" -> "rostry://${RouteConstants.Builders.monitoringVaccinationWithProductId(refId)}"
                "QUARANTINE_OVERDUE" -> "rostry://${RouteConstants.MONITORING_QUARANTINE}"
                "GROWTH_CHECK" -> "rostry://${RouteConstants.Builders.monitoringGrowthWithProductId(refId)}"
                "BATCH_SPLIT" -> "rostry://${RouteConstants.Builders.monitoringGrowthWithProductId(refId)}"
                "HATCHING_DUE" -> "rostry://${RouteConstants.Builders.monitoringHatching()}"
                "MORTALITY_SPIKE" -> "rostry://${RouteConstants.Builders.monitoringMortality()}"
                "BIRD_ADDED" -> "rostry://${RouteConstants.MONITORING_DAILY_LOG}"
                "BATCH_ADDED" -> "rostry://${RouteConstants.MONITORING_TASKS}"
                "COMPLIANCE_ALERT" -> "rostry://${RouteConstants.COMPLIANCE}"
                "DAILY_GOAL_MILESTONE" -> "rostry://home/farmer"
                "KYC_REQUIRED" -> "rostry://${RouteConstants.VERIFY_FARMER_LOCATION}"
                else -> "rostry://monitoring/dashboard"
            }
            "TRANSFER" -> "rostry://transfer/details/$refId"
            "ORDER" -> "rostry://order/details/$refId"
            "SOCIAL" -> when (subRoute) {
                "NEW_MESSAGE" -> "rostry://messages/thread/$refId"
                "NEW_COMMENT", "NEW_LIKE" -> "rostry://social/post/$refId"
                "NEW_FOLLOW" -> "rostry://profile/$refId"
                else -> "rostry://social/feed"
            }
            else -> ""
        }
    }
  
    private fun displayFarmNotification(type: FarmEventType, productId: String, title: String, message: String, metadata: Map<String, Any>? = null) {
        when (type) {
            FarmEventType.VACCINATION_DUE -> {
                val vaccineType = metadata?.get("vaccineType") as? String ?: title.split(" ")[0]
                FarmNotifier.notifyVaccinationDue(context, productId, vaccineType)
            }
            FarmEventType.QUARANTINE_OVERDUE -> FarmNotifier.notifyQuarantineOverdue(context, productId)
            FarmEventType.BIRD_ADDED -> {
                val productName = metadata?.get("productName") as? String ?: ""
                FarmNotifier.notifyBirdAdded(context, productName, productId)
            }
            FarmEventType.BATCH_ADDED -> {
                val productName = metadata?.get("productName") as? String ?: ""
                val taskCount = metadata?.get("taskCount") as? Int ?: 0
                FarmNotifier.notifyBatchAdded(context, productName, productId, taskCount)
            }
            FarmEventType.COMPLIANCE_ALERT -> {
                val productName = metadata?.get("productName") as? String ?: ""
                FarmNotifier.notifyComplianceAlert(context, productId, productName)
            }
            FarmEventType.DAILY_GOAL_MILESTONE -> {
                val goalType = metadata?.get("goalType") as? String ?: ""
                val progress = metadata?.get("progress") as? Int ?: 0
                FarmNotifier.notifyDailyGoalProgress(context, goalType, progress)
            }
            FarmEventType.KYC_REQUIRED -> FarmNotifier.notifyKycRequired(context)
            // Add other displays as needed
            else -> {
                // no-op
            }
        }
    }
  
    private fun displayTransferNotification(type: TransferEventType, transferId: String, title: String, message: String) {
        when (type) {
            TransferEventType.INITIATED -> {
                // Best-effort product label; FarmNotifier requires a name string
                val productLabel = title.ifBlank { "Product" }
                FarmNotifier.notifyTransferPending(context, transferId, productLabel)
            }
            TransferEventType.VERIFIED -> {
                val productLabel = title.ifBlank { "Product" }
                FarmNotifier.notifyTransferVerificationNeeded(context, transferId, productLabel)
            }
            TransferEventType.COMPLETED -> {
                transferNotifier.notifyCompleted(transferId)
            }
            TransferEventType.CANCELLED -> {
                transferNotifier.notifyCancelled(transferId)
            }
            TransferEventType.TIMED_OUT -> transferNotifier.notifyTimedOut(transferId)
            TransferEventType.ENTHUSIAST_TRANSFER_PROPOSED -> {
                transferNotifier.notifyPending(transferId, title)
            }
            else -> {
                // no-op
            }
        }
    }
  
    private fun displayOrderNotification(orderId: String, title: String, message: String) {
        val deepLink = "rostry://order/details/$orderId"
        analyticsNotifier.showInsight(title, message, deepLink)
    }
  
    private fun displaySocialNotification(type: SocialEventType, refId: String, title: String, message: String, fromUser: String) {
        when (type) {
            SocialEventType.NEW_MESSAGE -> socialNotifier.notifyNewMessage(refId, fromUser, message)
            SocialEventType.NEW_COMMENT -> {
                val postAuthorId = currentUserProvider.userIdOrNull() ?: ""
                socialNotifier.notifyNewComment(postAuthorId, refId, fromUser)
            }
            SocialEventType.NEW_LIKE -> {
                val postAuthorId = currentUserProvider.userIdOrNull() ?: ""
                socialNotifier.notifyNewLike(postAuthorId, refId, fromUser)
            }
            SocialEventType.NEW_FOLLOW -> socialNotifier.notifyFollow(refId, fromUser)
        }
    }

    private fun generateId(prefix: String): String = "$prefix${System.currentTimeMillis()}"

    private suspend fun markDisplayed(notificationId: String) {
        val now = System.currentTimeMillis()
        // Reuse batch method to set displayedAt for a single notification
        // It also ensures isBatched is false (no-op if already false)
        notificationDao.markBatchDisplayed(listOf(notificationId), now)
    }

    private suspend fun shouldNotify(userId: String, category: String): Boolean {
        val user = userDao.findById(userId) ?: return false
        if (!user.notificationsEnabled) return false
        return when (category) {
            "FARM", "ORDER", "LIFECYCLE" -> user.farmAlertsEnabled
            "TRANSFER" -> user.transferAlertsEnabled
            "SOCIAL" -> user.socialAlertsEnabled
            "VERIFICATION" -> true // Always notify for verification events
            else -> true
        }
    }
}
