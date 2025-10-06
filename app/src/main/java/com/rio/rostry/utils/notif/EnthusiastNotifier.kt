package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.rio.rostry.R

object EnthusiastNotifier {
    private const val CHANNEL_ID = "enthusiast_events"
    private const val CHANNEL_NAME = "Enthusiast Alerts"
    private const val CHANNEL_DESC = "Alerts for breeding, incubation, verification, and events"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = CHANNEL_DESC
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun pendingIntentForDeepLink(context: Context, deepLink: String): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        val stackBuilder = TaskStackBuilder.create(context).addNextIntentWithParentStack(intent)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        return stackBuilder.getPendingIntent(deepLink.hashCode(), flags)!!
    }

    private fun notify(context: Context, id: Int, title: String, text: String, deepLink: String? = null) {
        ensureChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        if (deepLink != null) {
            builder.setContentIntent(pendingIntentForDeepLink(context, deepLink))
        }
        NotificationManagerCompat.from(context).notify(id, builder.build())
    }

    // Pair to mate
    fun pairToMate(context: Context, pairsCount: Int) = notify(
        context,
        201,
        title = "Pairs to Mate",
        text = "$pairsCount pairs need attention this week"
    )

    // Egg collection due
    fun eggCollectionDue(context: Context, count: Int) = notify(
        context,
        202,
        title = "Egg Collection",
        text = "$count egg collections due"
    )

    // Incubation timers
    fun incubationTimer(context: Context, batchName: String, eta: String) = notify(
        context,
        203,
        title = "Incubation Timer",
        text = "$batchName hatching ETA $eta",
        deepLink = "rostry://hatching"
    )

    // Hatching due
    fun hatchingDue(context: Context, count: Int) = notify(
        context,
        204,
        title = "Hatching Due",
        text = "$count batches due in 7 days",
        deepLink = "rostry://hatching"
    )

    // Breeder eligibility
    fun breederEligible(context: Context, productId: String) = notify(
        context,
        205,
        title = "Breeder Eligible",
        text = "Bird $productId reached breeder eligibility",
        deepLink = "rostry://family-tree/$productId"
    )

    // Transfer verification / dispute updates
    fun transferVerification(context: Context, contextId: String, step: String) = notify(
        context,
        206,
        title = "Transfer Verification",
        text = "$contextId: $step",
        deepLink = "rostry://transfer/$contextId/verify"
    )

    fun transferDispute(context: Context, transferId: String, status: String) = notify(
        context,
        207,
        title = "Transfer Dispute",
        text = "$transferId: $status",
        deepLink = "rostry://transfer/$transferId"
    )

    // Event reminders
    fun eventReminder(context: Context, titleText: String) = notify(
        context,
        208,
        title = "Event Reminder",
        text = titleText
    )

    // New domain-specific alerts
    fun lineageAlert(context: Context, productId: String, message: String) = notify(
        context,
        209,
        title = "Lineage Alert",
        text = message,
        deepLink = "rostry://family-tree/$productId"
    )

    fun traitMilestone(context: Context, productId: String, message: String) = notify(
        context,
        210,
        title = "Trait Milestone",
        text = message,
        deepLink = "rostry://family-tree/$productId"
    )
}
