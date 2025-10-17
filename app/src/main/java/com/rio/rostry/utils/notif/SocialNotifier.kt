package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
        val deepLinkUri = Uri.parse("rostry://messages/$threadId")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, threadId.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New message from $fromUserName")
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setGroup("SOCIAL_EVENTS")
        NotificationManagerCompat.from(context).notify((threadId.hashCode()), builder.build())
    }

    override fun notifyNewComment(postAuthorId: String, postId: String, commenterName: String) {
        val deepLinkUri = Uri.parse("rostry://social/feed?postId=$postId")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, (postId + "c").hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$commenterName commented on your post")
            .setContentText("Post: ${postId.take(8)}...")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setGroup("SOCIAL_EVENTS")
        NotificationManagerCompat.from(context).notify((postId + "c").hashCode(), builder.build())
    }

    override fun notifyNewLike(postAuthorId: String, postId: String, likerName: String) {
        val deepLinkUri = Uri.parse("rostry://social/feed?postId=$postId")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, (postId + "l").hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$likerName liked your post")
            .setContentText("Post: ${postId.take(8)}...")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setGroup("SOCIAL_EVENTS")
        NotificationManagerCompat.from(context).notify((postId + "l").hashCode(), builder.build())
    }

    override fun notifyFollow(userId: String, followerName: String) {
        val deepLinkUri = Uri.parse("rostry://user/$userId")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, (userId + "f").hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$followerName started following you")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setGroup("SOCIAL_EVENTS")
        NotificationManagerCompat.from(context).notify((userId + "f").hashCode(), builder.build())
    }
}