package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import javax.inject.Inject
import javax.inject.Singleton

interface TransferNotifier {
    fun notifyInitiated(transferId: String, toUserId: String?)
    fun notifyPending(transferId: String, title: String)
    fun notifyBuyerVerified(transferId: String)
    fun notifyCompleted(transferId: String)
    fun notifyCancelled(transferId: String)
    fun notifyDisputeOpened(transferId: String)
    fun notifyTimedOut(transferId: String)
}

@Singleton
class TransferNotifierImpl @Inject constructor(
    private val appContext: Context
) : TransferNotifier {

    private val channelId = "transfer_events"

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Transfer Events", NotificationManager.IMPORTANCE_DEFAULT)
            val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun post(title: String, text: String, transferId: String, deepLink: String) {
        ensureChannel()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setGroup("TRANSFER_EVENTS")
        with(NotificationManagerCompat.from(appContext)) {
            notify(("transfer_" + transferId).hashCode(), builder.build())
        }
    }

    override fun notifyInitiated(transferId: String, toUserId: String?) {
        val deepLink = "rostry://transfer/$transferId"
        post("Transfer Initiated", "Transfer $transferId started${toUserId?.let { " for $it" } ?: ""}", transferId, deepLink)
    }

    override fun notifyPending(transferId: String, title: String) {
        val deepLink = "rostry://transfer/$transferId"
        post(title, "Transfer $transferId is pending your action", transferId, deepLink)
    }

    override fun notifyBuyerVerified(transferId: String) {
        val deepLink = "rostry://transfer/$transferId"
        post("Buyer Verified", "Transfer $transferId buyer verification received", transferId, deepLink)
    }

    override fun notifyCompleted(transferId: String) {
        val deepLink = "rostry://transfer/$transferId"
        post("Transfer Completed", "Transfer $transferId completed", transferId, deepLink)
    }

    override fun notifyCancelled(transferId: String) {
        val deepLink = "rostry://transfer/$transferId"
        post("Transfer Cancelled", "Transfer $transferId cancelled", transferId, deepLink)
    }

    override fun notifyDisputeOpened(transferId: String) {
        val deepLink = "rostry://transfer/$transferId"
        post("Dispute Opened", "Dispute opened for transfer $transferId", transferId, deepLink)
    }

    override fun notifyTimedOut(transferId: String) {
        val deepLink = "rostry://transfer/$transferId"
        post("Transfer Timed Out", "Transfer $transferId timed out", transferId, deepLink)
    }
}
