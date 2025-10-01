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
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.notif.SocialNotifierImpl
import com.rio.rostry.utils.notif.TransferNotifierImpl
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
        val notifier = SocialNotifierImpl(applicationContext)
        val transferNotifier = TransferNotifierImpl(applicationContext)
        val analyticsNotifier = AnalyticsNotifierImpl(applicationContext)
        when (type) {
            "message" -> {
                val threadId = data["threadId"] ?: ""
                val from = data["from"] ?: "Someone"
                val text = data["text"] ?: "New message"
                notifier.notifyNewMessage(threadId, from, text)
            }
            "comment" -> {
                val postId = data["postId"] ?: ""
                val who = data["who"] ?: "Someone"
                notifier.notifyNewComment("", postId, who)
            }
            "like" -> {
                val postId = data["postId"] ?: ""
                val who = data["who"] ?: "Someone"
                notifier.notifyNewLike("", postId, who)
            }
            "follow" -> {
                val userId = data["userId"] ?: ""
                val who = data["who"] ?: "Someone"
                notifier.notifyFollow(userId, who)
            }
            // Marketplace and orders
            "ORDER_UPDATE" -> {
                val orderId = data["orderId"] ?: return
                val title = data["title"] ?: "Order Update"
                val body = data["body"] ?: "Your order has been updated"
                handleOrderUpdate(orderId, title, body)
            }
            "order_status" -> {
                val orderId = data["orderId"] ?: return
                val status = data["status"] ?: "updated"
                val title = data["title"] ?: "Order $status"
                val body = data["body"] ?: "Your order $orderId is $status"
                // Track analytics insight if needed
                analyticsNotifier.showInsight(title, body)
                // Invoke the same order update handling for notification storage and deep linking
                handleOrderUpdate(orderId, title, body)
            }
            "bid_update" -> {
                val productId = data["productId"] ?: return
                val messageText = data["message"] ?: "Bid update"
                analyticsNotifier.showInsight("Bid update", messageText)
            }
            // Transfers
            "transfer_status" -> {
                val transferId = data["transferId"] ?: return
                val status = data["status"] ?: "updated"
                when (status.uppercase()) {
                    "COMPLETED" -> transferNotifier.notifyCompleted(transferId)
                    "CANCELLED" -> transferNotifier.notifyCancelled(transferId)
                    "VERIFIED", "BUYER_VERIFIED" -> transferNotifier.notifyBuyerVerified(transferId)
                    "DISPUTED", "DISPUTE" -> transferNotifier.notifyDisputeOpened(transferId)
                    else -> transferNotifier.notifyInitiated(transferId, null)
                }
            }
            // Farm monitoring
            "vaccination_reminder" -> {
                val vaccine = data["vaccine"] ?: "Vaccination due"
                analyticsNotifier.showInsight("Vaccination reminder", "$vaccine scheduled")
            }
            // Verification
            "verification_update" -> {
                val status = data["status"] ?: "updated"
                analyticsNotifier.showInsight("Verification $status", "Your verification status is $status")
            }
            else -> {
                Timber.d("Unknown social notification type: $type")
            }
        }
    }

    private fun handleOrderUpdate(orderId: String, title: String, body: String) {
        val userId = currentUserProvider.userIdOrNull() ?: return

        // Store notification in database
        serviceScope.launch {
            try {
                val notification = NotificationEntity(
                    notificationId = UUID.randomUUID().toString(),
                    userId = userId,
                    title = title,
                    message = body,
                    type = "ORDER_UPDATE",
                    deepLinkUrl = "rostry://general/cart?orderId=$orderId",
                    isRead = false,
                    createdAt = System.currentTimeMillis()
                )
                notificationDao.insertNotification(notification)
                Timber.d("ORDER_UPDATE notification stored for user $userId, order $orderId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to store ORDER_UPDATE notification")
            }
        }

        // Show Android notification
        showOrderNotification(orderId, title, body)
    }

    private fun showOrderNotification(orderId: String, title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ORDER_CHANNEL_ID,
                "Order Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for order status updates"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create deep link intent to General cart/order detail
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("deepLink", "rostry://general/cart?orderId=$orderId")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build high-priority notification
        val notification = NotificationCompat.Builder(this, ORDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with R.drawable.ic_notification in production
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(orderId.hashCode(), notification)
        Timber.d("ORDER_UPDATE notification shown for order $orderId")
    }

    companion object {
        private const val ORDER_CHANNEL_ID = "order_updates"
    }
}
