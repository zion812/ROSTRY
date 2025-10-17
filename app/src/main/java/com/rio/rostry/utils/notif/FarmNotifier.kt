package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.rio.rostry.R
import com.rio.rostry.ui.navigation.Routes

object FarmNotifier {
    private const val CHANNEL_ID = "FARM_ALERTS"
    private const val CHANNEL_NAME = "Farm Alerts"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for farm monitoring alerts and reminders"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun batchSplitDue(context: Context, productId: String, batchName: String) {
        ensureChannel(context)
        val deepLink = ("rostry://monitoring/growth?productId=$productId").toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (productId + "_split").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Batch Split Recommended")
            .setContentText("$batchName is ready for individual tracking. Consider splitting.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "batchSplitDue" + "_" + productId).hashCode(), notification)
    }

    fun notifyBirdOnboarded(context: Context, birdName: String, productId: String) {
        ensureChannel(context)
        val deepLink = ("rostry://" + com.rio.rostry.ui.navigation.Routes.MONITORING_DAILY_LOG).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (productId + "_onboard").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle("Bird Added to Farm")
            .setContentText("$birdName has been successfully onboarded. Start logging daily activities!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "birdOnboarded" + "_" + productId).hashCode(), notification)
    }

    fun notifyVaccinationDue(context: Context, productId: String, vaccineType: String) {
        ensureChannel(context)
        val deepLink = ("rostry://${Routes.Builders.monitoringVaccination(productId)}").toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            productId.hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Vaccination Due")
            .setContentText("$vaccineType scheduled for $productId")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "vaccinationDue" + "_" + productId).hashCode(), notification)
    }

    fun notifyQuarantineOverdue(context: Context, productId: String) {
        ensureChannel(context)
        val deepLink = "rostry://${Routes.MONITORING_QUARANTINE}".toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            productId.hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Quarantine Alert")
            .setContentText("Update required for $productId")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "quarantineOverdue" + "_" + productId).hashCode(), notification)
    }

    fun notifyHatchingDue(context: Context, batchId: String, batchName: String) {
        ensureChannel(context)
        val deepLink = "rostry://${Routes.MONITORING_HATCHING}".toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            batchId.hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hatching Due")
            .setContentText("Batch $batchName due to hatch soon")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "hatchingDue" + "_" + batchId).hashCode(), notification)
    }

    fun notifyMortalitySpike(context: Context, count: Int) {
        ensureChannel(context)
        val deepLink = "rostry://${Routes.MONITORING_MORTALITY}".toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            count,
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Mortality Alert")
            .setContentText("$count deaths in last 7 days")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "mortalitySpike" + "_" + count.toString()).hashCode(), notification)
    }
    
    /**
     * Notify farmer to add purchased product to farm monitoring
     * Called when farmer dismisses the "Add to Farm" dialog without adding
     */
    fun notifyProductPurchased(context: Context, productId: String, productName: String) {
        ensureChannel(context)
        // Deep link to add product to farm monitoring
        val deepLink = "rostry://add-to-farm?productId=$productId".toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            productId.hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Track Your Purchase")
            .setContentText("Add $productName to farm monitoring to track growth and vaccinations")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("You recently purchased $productName. Add it to your farm monitoring system to automatically track growth records and vaccination schedules."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .addAction(
                android.R.drawable.ic_input_add,
                "Add to Farm",
                pendingIntent
            )
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Use a unique notification ID based on productId
        manager.notify(("farm_" + "productPurchased" + "_" + productId).hashCode(), notification)
    }
}
