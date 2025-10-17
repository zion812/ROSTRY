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
import dagger.hilt.android.qualifiers.ApplicationContext

interface AnalyticsNotifier {
    fun showInsight(title: String, message: String, deepLink: String? = null)
}

@Singleton
class AnalyticsNotifierImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AnalyticsNotifier {

    private val channelId = "analytics_insights"

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Analytics Insights",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    override fun showInsight(title: String, message: String, deepLink: String?) {
        ensureChannel()
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setGroup("ANALYTICS_INSIGHTS")
        if (deepLink != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
        }
        val notif = builder.build()
        NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notif)
    }
}
