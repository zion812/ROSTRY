package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

interface TransferNotifier {
    fun notifyInitiated(transferId: String, toUserId: String?)
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

    private fun post(title: String, text: String) {
        ensureChannel()
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(appContext)) {
            notify(Random.nextInt(1_000_000), builder.build())
        }
    }

    override fun notifyInitiated(transferId: String, toUserId: String?) {
        post("Transfer Initiated", "Transfer $transferId started${toUserId?.let { " for $it" } ?: ""}")
    }

    override fun notifyBuyerVerified(transferId: String) {
        post("Buyer Verified", "Transfer $transferId buyer verification received")
    }

    override fun notifyCompleted(transferId: String) {
        post("Transfer Completed", "Transfer $transferId completed")
    }

    override fun notifyCancelled(transferId: String) {
        post("Transfer Cancelled", "Transfer $transferId cancelled")
    }

    override fun notifyDisputeOpened(transferId: String) {
        post("Dispute Opened", "Dispute opened for transfer $transferId")
    }

    override fun notifyTimedOut(transferId: String) {
        post("Transfer Timed Out", "Transfer $transferId timed out")
    }
}
