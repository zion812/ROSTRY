package com.rio.rostry.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import com.rio.rostry.R
import com.rio.rostry.data.database.dao.NotificationDao
import com.rio.rostry.data.database.entity.NotificationEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.MainActivity
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import com.rio.rostry.utils.preferences.UserPreferencesRepository
import com.rio.rostry.workers.FarmAlertWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import com.rio.rostry.utils.Resource
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmAlertNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationDao: NotificationDao,
    private val userRepository: UserRepository,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val workManager: WorkManager
) {
    private val channelId = "farm_alerts"

    init {
        ensureChannel()
    }

    private suspend fun currentUserId(): String? {
        val res = userRepository.getCurrentUser().first()
        return when (res) {
            is Resource.Success -> res.data?.userId
            else -> null
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(channelId) == null) {
                val ch = NotificationChannel(channelId, "Farm Alerts", NotificationManager.IMPORTANCE_HIGH)
                ch.description = "Important alerts for farm management"
                ch.enableLights(true)
                ch.lightColor = Color.BLUE
                nm.createNotificationChannel(ch)
            }
        }
    }

    private fun makePendingIntent(deeplink: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = android.net.Uri.parse(deeplink)
        }
        return PendingIntent.getActivity(context, deeplink.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private suspend fun postLocalNotification(title: String, body: String, deeplink: String) = withContext(Dispatchers.Main) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(makePendingIntent(deeplink))
            .build()
        nm.notify((title + body).hashCode(), notif)
    }

    private suspend fun saveNotificationToDb(
        userId: String,
        title: String,
        message: String,
        type: String,
        deepLinkUrl: String
    ) = withContext(Dispatchers.IO) {
        val entity = NotificationEntity(
            notificationId = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            message = message,
            type = type,
            deepLinkUrl = deepLinkUrl,
            isRead = false,
            imageUrl = null,
            createdAt = System.currentTimeMillis()
        )
        notificationDao.insertNotification(entity)
    }

    private fun trackNotificationSent(type: String, userId: String) {
        flowAnalyticsTracker.trackEvent("notification_sent", mapOf(
            "type" to type,
            "user_id" to userId,
            "timestamp" to System.currentTimeMillis()
        ))
    }

    private suspend fun isNotificationEnabled(type: String): Boolean {
        return userPreferencesRepository.isFarmNotificationEnabled(type)
    }

    suspend fun sendVaccinationReminder(productId: String, vaccineType: String, dueDate: Long, reminderType: String) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("vaccination")) return

        val title = when (reminderType) {
            "day_before" -> "Vaccination Due Tomorrow"
            "due_today" -> "Vaccination Due Today"
            "overdue" -> "Vaccination Overdue"
            else -> "Vaccination Reminder"
        }
        val body = "Vaccinate $productId with $vaccineType. Due: ${java.util.Date(dueDate)}"
        val deeplink = "rostry://monitoring/vaccination?productId=$productId"

        saveNotificationToDb(userId, title, body, "VACCINATION_REMINDER", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("vaccination_reminder", userId)
    }

    suspend fun sendQuarantineReminder(productId: String, lastUpdate: Long) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("quarantine")) return

        val title = "Quarantine Update Due"
        val body = "Update quarantine status for $productId. Last update: ${java.util.Date(lastUpdate)}"
        val deeplink = "rostry://monitoring/quarantine"

        saveNotificationToDb(userId, title, body, "QUARANTINE_REMINDER", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("quarantine_reminder", userId)
    }

    suspend fun sendBatchSplitReminder(batchId: String, ageWeeks: Int) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("batch_split")) return

        val title = "Batch Ready for Split"
        val body = "Batch $batchId is $ageWeeks weeks old and ready to split into individual birds."
        val deeplink = "rostry://monitoring/batch/$batchId/split"

        saveNotificationToDb(userId, title, body, "BATCH_SPLIT_REMINDER", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("batch_split_reminder", userId)
    }

    suspend fun sendGrowthReminder(productId: String, lastGrowth: Long) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("growth")) return

        val title = "Growth Tracking Due"
        val body = "Record growth for $productId. Last recorded: ${java.util.Date(lastGrowth)}"
        val deeplink = "rostry://monitoring/growth?productId=$productId"

        saveNotificationToDb(userId, title, body, "GROWTH_REMINDER", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("growth_reminder", userId)
    }

    suspend fun sendMortalitySpikeAlert(farmId: String, spikeRate: Double) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("mortality")) return

        val title = "Mortality Spike Alert"
        val body = "Mortality rate has spiked to ${String.format("%.1f", spikeRate)}% in farm $farmId. Investigate immediately."
        val deeplink = "rostry://monitoring/mortality"

        saveNotificationToDb(userId, title, body, "MORTALITY_SPIKE", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("mortality_spike", userId)
    }

    suspend fun sendHatchingReminder(batchId: String, hatchDate: Long, reminderType: String) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("hatching")) return

        val title = when (reminderType) {
            "day_before" -> "Hatching Due Tomorrow"
            else -> "Hatching Reminder"
        }
        val body = "Hatching expected for batch $batchId on ${java.util.Date(hatchDate)}"
        val deeplink = "rostry://monitoring/hatching"

        saveNotificationToDb(userId, title, body, "HATCHING_REMINDER", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("hatching_reminder", userId)
    }

    suspend fun sendTaskOverdueReminder(taskId: String, overdueSince: Long) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("task_overdue")) return

        val title = "Task Overdue"
        val body = "Task $taskId is overdue since ${java.util.Date(overdueSince)}. Complete it now."
        val deeplink = "rostry://monitoring/tasks"

        saveNotificationToDb(userId, title, body, "TASK_OVERDUE", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("task_overdue", userId)
    }

    // Scheduling methods using WorkManager
    fun scheduleVaccinationReminder(productId: String, vaccineType: String, dueDate: Long) {
        val delay = dueDate - System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1) // 1 day before
        if (delay > 0) {
            val data = Data.Builder()
                .putString("productId", productId)
                .putString("vaccineType", vaccineType)
                .putLong("dueDate", dueDate)
                .putString("reminderType", "day_before")
                .build()
            val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            workManager.enqueue(workRequest)
        }
        // Schedule for due day and overdue similarly
    }

    fun scheduleQuarantineReminder(productId: String, lastUpdate: Long) {
        val delay = TimeUnit.HOURS.toMillis(12) // every 12 hours
        val data = Data.Builder()
            .putString("productId", productId)
            .putLong("lastUpdate", lastUpdate)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueue(workRequest)
    }

    // Send a simple task completion notification
    suspend fun sendTaskCompletionNotification(taskId: String) {
        val userId = currentUserId() ?: return
        if (!isNotificationEnabled("task_complete")) return

        val title = "Task Completed"
        val body = "Task $taskId was marked as completed. Good job!"
        val deeplink = "rostry://monitoring/tasks"

        saveNotificationToDb(userId, title, body, "TASK_COMPLETE", deeplink)
        postLocalNotification(title, body, deeplink)
        trackNotificationSent("task_complete", userId)
    }

    fun scheduleBatchSplitReminder(batchId: String, ageWeeks: Int) {
        // Assume ageWeeks is current, schedule when reaches 12
        val weeksTo12 = 12 - ageWeeks
        if (weeksTo12 > 0) {
            val delay = TimeUnit.DAYS.toMillis(weeksTo12 * 7L)
            val data = Data.Builder()
                .putString("batchId", batchId)
                .putInt("ageWeeks", 12)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            workManager.enqueue(workRequest)
        }
    }

    fun scheduleGrowthReminder(productId: String, lastGrowth: Long) {
        val delay = TimeUnit.DAYS.toMillis(7) // weekly
        val data = Data.Builder()
            .putString("productId", productId)
            .putLong("lastGrowth", lastGrowth)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueue(workRequest)
    }

    fun scheduleHatchingReminder(batchId: String, hatchDate: Long) {
        val delay = hatchDate - System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
        if (delay > 0) {
            val data = Data.Builder()
                .putString("batchId", batchId)
                .putLong("hatchDate", hatchDate)
                .putString("reminderType", "day_before")
                .build()
            val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            workManager.enqueue(workRequest)
        }
    }

    // Overload with explicit reminder type for flexibility (e.g., "check")
    fun scheduleHatchingReminder(batchId: String, hatchDate: Long, reminderType: String) {
        val delay = when (reminderType) {
            "day_before" -> hatchDate - System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
            else -> hatchDate - System.currentTimeMillis()
        }
        if (delay > 0) {
            val data = Data.Builder()
                .putString("batchId", batchId)
                .putLong("hatchDate", hatchDate)
                .putString("reminderType", reminderType)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            workManager.enqueue(workRequest)
        }
    }

    fun scheduleTaskOverdueReminder(taskId: String, dueDate: Long) {
        val delay = dueDate - System.currentTimeMillis()
        if (delay <= 0) {
            // Already overdue, send immediately
            kotlinx.coroutines.runBlocking { sendTaskOverdueReminder(taskId, dueDate) }
        } else {
            val data = Data.Builder()
                .putString("taskId", taskId)
                .putLong("overdueSince", dueDate)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<FarmAlertWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
            workManager.enqueue(workRequest)
        }
    }
}