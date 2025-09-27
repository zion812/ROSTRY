package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import javax.inject.Inject
import javax.inject.Singleton

interface SocialNotifier {
    fun notifyNewMessage(threadId: String, fromUserName: String, text: String)
    fun notifyNewComment(postAuthorId: String, postId: String, commenterName: String)
    fun notifyNewLike(postAuthorId: String, postId: String, likerName: String)
    fun notifyFollow(userId: String, followerName: String)
    fun notifyDeepLink(title: String, text: String?, uri: String, idKey: String)
    fun notifyTransferVerify(transferId: String, title: String = "Transfer requires verification", text: String? = null) {
        notifyDeepLink(title, text, "rostry://transfer/verify/$transferId", "verify_$transferId")
    }
    fun notifyTransferDispute(transferId: String, title: String = "Transfer dispute update", text: String? = null) {
        notifyDeepLink(title, text, "rostry://transfer/dispute/$transferId", "dispute_$transferId")
    }
    fun notifyTransferDocs(transferId: String, title: String = "Certificate ready", text: String? = null) {
        notifyDeepLink(title, text, "rostry://transfer/docs/$transferId", "docs_$transferId")
    }
}

@Singleton
class SocialNotifierImpl @Inject constructor(
    private val context: Context
) : SocialNotifier {

    private val channelId = "social_events"

    init {
        ensureChannel()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Social", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Social activity notifications"
                enableLights(true)
                lightColor = Color.BLUE
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    override fun notifyNewMessage(threadId: String, fromUserName: String, text: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New message from $fromUserName")
            .setContentText(text)
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify((threadId.hashCode()), builder.build())
    }

    override fun notifyNewComment(postAuthorId: String, postId: String, commenterName: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$commenterName commented on your post")
            .setContentText("Post: ${postId.take(8)}...")
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify((postId + "c").hashCode(), builder.build())
    }

    override fun notifyNewLike(postAuthorId: String, postId: String, likerName: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$likerName liked your post")
            .setContentText("Post: ${postId.take(8)}...")
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify((postId + "l").hashCode(), builder.build())
    }

    override fun notifyFollow(userId: String, followerName: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$followerName started following you")
            .setAutoCancel(true)
        NotificationManagerCompat.from(context).notify((userId + "f").hashCode(), builder.build())
    }

    override fun notifyDeepLink(title: String, text: String?, uri: String, idKey: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val piFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(context, idKey.hashCode(), intent, piFlags)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text ?: "")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        NotificationManagerCompat.from(context).notify(idKey.hashCode(), builder.build())
    }
}
