package com.rio.rostry.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import kotlin.random.Random

object MilestoneNotifier {
    private const val CHANNEL_ID = "lifecycle_milestones"
    private const val CHANNEL_NAME = "Lifecycle Milestones"
    private const val CHANNEL_DESC = "Notifications for poultry lifecycle milestones"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_DESC
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun notify(context: Context, productId: String, event: LifecycleEventEntity) {
        val title = when (event.type) {
            "VACCINATION" -> "Vaccination Reminder"
            "GROWTH_UPDATE" -> "Weekly Growth Update"
            "MILESTONE" -> "Lifecycle Milestone"
            else -> "Lifecycle Event"
        }
        val text = "${event.stage} | Week ${event.week} • ${event.notes ?: ""}"

        // Deep link to traceability/{productId}
        val traceUri = Uri.parse("rostry://traceability/$productId")
        val intent = Intent(Intent.ACTION_VIEW, traceUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            productId.hashCode(),
            intent,
            (PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0))
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(1_000_000), builder.build())
        }
    }

    // Generic overload for simple title/message notifications
    fun notify(context: Context, productId: String, title: String, message: String) {
        // Deep link to traceability/{productId}
        val traceUri = Uri.parse("rostry://traceability/$productId")
        val intent = Intent(Intent.ACTION_VIEW, traceUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            productId.hashCode(),
            intent,
            (PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0))
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(1_000_000), builder.build())
        }
    }
}
