package com.rio.rostry.domain.usecase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import javax.inject.Inject

class SendMilestoneAlertUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke(milestoneTitle: String, milestoneDescription: String) {
        createNotificationChannel()
        
        val builder = NotificationCompat.Builder(context, "milestone_alerts")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(milestoneTitle)
            .setContentText(milestoneDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            // Use a fixed ID for notifications or generate a proper one
            notify(1, builder.build())
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Milestone Alerts"
            val descriptionText = "Notifications for poultry milestone alerts"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("milestone_alerts", name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}