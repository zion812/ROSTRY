package com.rio.rostry.utils.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.rio.rostry.BuildConfig
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
        val deepLink = ("rostry://${Routes.Builders.monitoringVaccinationWithProductId(productId)}").toUri()
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

    fun notifyBirdAdded(context: Context, birdName: String, productId: String) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.MONITORING_DAILY_LOG).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (productId + "_birdAdded").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle("Bird Added")
            .setContentText("$birdName has been added to your farm. Start logging daily activities!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "birdAdded" + "_" + productId).hashCode(), notification)
    }

    fun notifyBatchAdded(context: Context, batchName: String, productId: String, taskCount: Int) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.MONITORING_TASKS).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (productId + "_batchAdded").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_input_add)
            .setContentTitle("Batch Added")
            .setContentText("$batchName added with $taskCount tasks. Check your task list!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "batchAdded" + "_" + productId).hashCode(), notification)
    }

    fun notifyTransferPending(context: Context, transferId: String, productName: String) {
        ensureChannel(context)
        val deepLink = ("rostry://${Routes.Builders.transferDetails(transferId)}").toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (transferId + "_transferPending").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Transfer Pending")
            .setContentText("Action required for transfer of $productName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "transferPending" + "_" + transferId).hashCode(), notification)
    }

    fun notifyTransferVerificationNeeded(context: Context, transferId: String, productName: String) {
        ensureChannel(context)
        val deepLink = ("rostry://${Routes.Builders.transferVerify(transferId)}").toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (transferId + "_transferVerify").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Transfer Verification Needed")
            .setContentText("Verify transfer for $productName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "transferVerify" + "_" + transferId).hashCode(), notification)
    }

    fun notifyComplianceAlert(context: Context, productId: String, productName: String) {
        ensureChannel(context)
        val deepLink = ("rostry://${Routes.Builders.complianceDetails(productId)}").toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (productId + "_complianceAlert").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Compliance Alert")
            .setContentText("$productName has compliance issues. Review required.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "complianceAlert" + "_" + productId).hashCode(), notification)
    }

    fun notifyDailyGoalProgress(context: Context, goalType: String, progress: Int) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.HOME_FARMER).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (goalType + "_goalProgress").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Goal Progress")
            .setContentText("$goalType goal $progress% complete!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "goalProgress" + "_" + goalType).hashCode(), notification)
    }

    fun notifyKycRequired(context: Context) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.VERIFY_FARMER_LOCATION).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            "kycRequired".hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("KYC Required")
            .setContentText("Complete verification to enable transfers")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "kycRequired").hashCode(), notification)
    }

    fun notifyBackupComplete(context: Context, filePath: String) {
        ensureChannel(context)
        val file = java.io.File(filePath)
        val fileUri = FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, "application/zip")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            "backupComplete".hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle("Data Export Ready")
            .setContentText("Your data backup has been successfully generated.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify("backup_complete".hashCode(), notification)
    }

    /**
     * Notify farmer that a batch is market-ready for harvest/sale.
     * Part of the Farmer-First Sales Pipeline.
     */
    fun harvestReady(context: Context, batchId: String, batchName: String) {
        ensureChannel(context)
        val deepLink = ("rostry://${Routes.Builders.createListingFromAsset(batchId)}").toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            (batchId + "_harvestReady").hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_gallery)
            .setContentTitle("Batch Ready for Sale!")
            .setContentText("$batchName has reached market weight. Create a listing now!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$batchName has reached the target weight and age for sale. Tap to create a marketplace listing with pre-filled details."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setGroup("FARM_ALERTS")
            .setGroupSummary(false)
            .addAction(
                android.R.drawable.ic_menu_send,
                "Sell Now",
                pendingIntent
            )
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("farm_" + "harvestReady" + "_" + batchId).hashCode(), notification)
    }

    fun dailyBriefing(context: Context, count: Int, firstTitle: String) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.FARMER_CALENDAR).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            "dailyBriefing".hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val text = "You have $count events today. Starting with: $firstTitle"
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("Daily Farm Briefing")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify("daily_briefing".hashCode(), notification)
    }

    fun eventReminder(context: Context, eventId: String, title: String, timeString: String) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.FARMER_CALENDAR).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Event Reminder")
            .setContentText("$title at $timeString")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("event_" + eventId).hashCode(), notification)
    }
    fun recommendationAlert(context: Context, eventId: String, title: String, description: String) {
        ensureChannel(context)
        val deepLink = ("rostry://" + Routes.FARMER_CALENDAR).toUri()
        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            Intent(Intent.ACTION_VIEW, deepLink),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback to standard icon 
            .setContentTitle("Recommendation: $title")
            .setContentText(description) // Short description
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setGroup("FARM_ALERTS")
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(("rec_" + eventId).hashCode(), notification)
    }
}
