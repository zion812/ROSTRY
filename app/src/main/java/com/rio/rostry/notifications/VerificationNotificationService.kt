package com.rio.rostry.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.rio.rostry.R
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.entity.NotificationEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class VerificationNotificationService(
    private val firebaseMessaging: FirebaseMessaging,
    private val notificationDao: NotificationDao,
    private val appContext: Context,
    private val userRepository: UserRepository,
) {
    private val channelId = "verification_updates"

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(channelId) == null) {
                val ch = NotificationChannel(channelId, "Verification Updates", NotificationManager.IMPORTANCE_HIGH)
                ch.description = "Status updates for account verification"
                ch.enableLights(true)
                ch.lightColor = Color.GREEN
                nm.createNotificationChannel(ch)
            }
        }
    }

    private fun makePendingIntent(deeplink: String): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = android.net.Uri.parse(deeplink)
        }
        return PendingIntent.getActivity(appContext, deeplink.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private suspend fun postLocalNotification(title: String, body: String, deeplink: String) = withContext(Dispatchers.Main) {
        ensureChannel()
        val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(makePendingIntent(deeplink))
            .build()
        nm.notify((title + body).hashCode(), notif)
    }

    suspend fun notifyVerificationApproved(userId: String, userType: UserType?) {
        val title = "Verification Approved!"
        val body = if (userType == UserType.FARMER) "Your farm location has been verified." else "Your KYC has been approved."
        val deeplink = "rostry://profile"
        // Save to local store
        withContext(Dispatchers.IO) {
            val entity = NotificationEntity(
                notificationId = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                message = body,
                type = "VERIFICATION_APPROVED",
                deepLinkUrl = deeplink,
                isRead = false,
                imageUrl = null,
                createdAt = System.currentTimeMillis()
            )
            notificationDao.insertNotification(entity)
        }
        // Local notification
        postLocalNotification(title, body, deeplink)
        // Server push could be implemented via Cloud Function trigger
    }

    suspend fun notifyVerificationRejected(userId: String, reason: String) {
        val title = "Verification Needs Attention"
        val body = "Your verification was rejected: $reason"
        val deeplink = "rostry://verification/resubmit"
        withContext(Dispatchers.IO) {
            val entity = NotificationEntity(
                notificationId = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                message = body,
                type = "VERIFICATION_REJECTED",
                deepLinkUrl = deeplink,
                isRead = false,
                imageUrl = null,
                createdAt = System.currentTimeMillis()
            )
            notificationDao.insertNotification(entity)
        }
        postLocalNotification(title, body, deeplink)
    }

    suspend fun notifyVerificationPending(userId: String) {
        val title = "Verification Submitted"
        val body = "We've received your verification documents. Review typically takes 24-48 hours."
        val deeplink = "rostry://profile"
        withContext(Dispatchers.IO) {
            val entity = NotificationEntity(
                notificationId = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                message = body,
                type = "VERIFICATION_PENDING",
                deepLinkUrl = deeplink,
                isRead = false,
                imageUrl = null,
                createdAt = System.currentTimeMillis()
            )
            notificationDao.insertNotification(entity)
        }
        postLocalNotification(title, body, deeplink)
    }

    suspend fun notifyBatchVerifications(approvals: List<String>, rejections: List<String>) {
        approvals.forEach { runCatching { notifyVerificationApproved(it, null) } }
        rejections.forEach { runCatching { notifyVerificationRejected(it, "") } }
    }
}
