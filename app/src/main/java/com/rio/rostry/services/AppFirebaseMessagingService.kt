package com.rio.rostry.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rio.rostry.MainActivity
import com.rio.rostry.R
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.entity.NotificationEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.notifications.FarmEventType
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.SocialEventType
import com.rio.rostry.notifications.TransferEventType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.notif.AnalyticsNotifierImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider

    @Inject
    lateinit var intelligentNotificationService: IntelligentNotificationService

    @Inject
    lateinit var userRepository: UserRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("FCM token: $token")
        // TODO: send token to backend if needed
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val type = data["type"]
        when (type) {
            "message" -> {
                val threadId = data["threadId"] ?: ""
                val from = data["from"] ?: "Someone"
                val text = data["text"] ?: "New message"
                serviceScope.launch {
                    intelligentNotificationService.notifySocialEvent(SocialEventType.NEW_MESSAGE, threadId, "New Message", text, from)
                }
            }
            "comment" -> {
                val postId = data["postId"] ?: ""
                val who = data["who"] ?: "Someone"
                serviceScope.launch {
                    intelligentNotificationService.notifySocialEvent(SocialEventType.NEW_COMMENT, postId, "New Comment", "New comment from $who", who)
                }
            }
            "like" -> {
                val postId = data["postId"] ?: ""
                val who = data["who"] ?: "Someone"
                serviceScope.launch {
                    intelligentNotificationService.notifySocialEvent(SocialEventType.NEW_LIKE, postId, "New Like", "New like from $who", who)
                }
            }
            "follow" -> {
                val userId = data["userId"] ?: ""
                val who = data["who"] ?: "Someone"
                serviceScope.launch {
                    intelligentNotificationService.notifySocialEvent(SocialEventType.NEW_FOLLOW, userId, "New Follow", "$who followed you", who)
                }
            }
            // Marketplace and orders
            "ORDER_UPDATE" -> {
                val orderId = data["orderId"] ?: return
                val title = data["title"] ?: "Order Update"
                val body = data["body"] ?: "Your order has been updated"
                serviceScope.launch { intelligentNotificationService.notifyOrderUpdate(orderId, "updated", title, body) }
            }
            "order_status" -> {
                val orderId = data["orderId"] ?: return
                val status = data["status"] ?: "updated"
                val title = data["title"] ?: "Order $status"
                val body = data["body"] ?: "Your order $orderId is $status"
                // Invoke the same order update handling for notification storage and deep linking
                serviceScope.launch { intelligentNotificationService.notifyOrderUpdate(orderId, status, title, body) }
            }
            "bid_update" -> {
                val productId = data["productId"] ?: return
                val messageText = data["message"] ?: "Bid update"
                val analyticsNotifier = AnalyticsNotifierImpl(applicationContext)
                analyticsNotifier.showInsight("Bid update", messageText, null)
            }
            // Transfers
            "transfer_status" -> {
                val transferId = data["transferId"] ?: return
                val status = data["status"] ?: "updated"
                val title = "Transfer $status"
                val body = "Transfer $transferId is $status"
                val eventType = when (status.uppercase()) {
                    "COMPLETED" -> TransferEventType.COMPLETED
                    "CANCELLED" -> TransferEventType.CANCELLED
                    "VERIFIED", "BUYER_VERIFIED" -> TransferEventType.VERIFIED
                    "DISPUTED", "DISPUTE" -> TransferEventType.DISPUTED
                    "TIMED_OUT" -> TransferEventType.TIMED_OUT
                    else -> TransferEventType.INITIATED
                }
                serviceScope.launch { intelligentNotificationService.notifyTransferEvent(eventType, transferId, title, body) }
            }
            // Farm monitoring
            "vaccination_reminder" -> {
                val productId = data["productId"] ?: ""
                val vaccine = data["vaccine"] ?: "Vaccination due"
                val title = "Vaccination Reminder"
                val body = "$vaccine scheduled"
                serviceScope.launch { intelligentNotificationService.notifyFarmEvent(FarmEventType.VACCINATION_DUE, productId, title, body) }
            }
            // Verification
            "verification_update" -> {
                val status = data["status"] ?: "updated"
                val analyticsNotifier = AnalyticsNotifierImpl(applicationContext)
                analyticsNotifier.showInsight("Verification $status", "Your verification status is $status", null)
            }
            "PROFILE_SYNC" -> {
                Timber.d("Received PROFILE_SYNC, refreshing user...")
                serviceScope.launch {
                    val uid = currentUserProvider.userIdOrNull()
                    if (uid != null) {
                        userRepository.refreshCurrentUser(uid)
                    }
                }
            }
            else -> {
                Timber.d("Unknown social notification type: $type")
            }
        }
    }


}
