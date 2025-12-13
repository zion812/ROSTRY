package com.rio.rostry.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rio.rostry.R
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.FarmAlertEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for Farm Health Alerts (Phase 4.2)
 * Generates smart alerts for vaccination due, quarantine updates, etc.
 */
@Singleton
class FarmHealthAlertService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val quarantineRecordDao: QuarantineRecordDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val farmAlertDao: FarmAlertDao,
) {
    companion object {
        private const val CHANNEL_ID = "farm_health_alerts"
        private const val CHANNEL_NAME = "Farm Health Alerts"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Health alerts for your farm and birds"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Check for vaccination alerts (due tomorrow or overdue)
     */
    suspend fun checkVaccinationAlerts(farmerId: String) {
        val now = System.currentTimeMillis()
        val tomorrow = now + (24 * 60 * 60 * 1000)
        
        // Check overdue vaccinations
        val overdue = vaccinationRecordDao.getOverdueForFarmer(farmerId, now)
        if (overdue.isNotEmpty()) {
            val alert = FarmAlertEntity(
                alertId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                alertType = "VACCINATION_OVERDUE",
                severity = "HIGH",
                message = "${overdue.size} vaccination(s) are overdue!",
                actionRoute = "vaccination_schedule",
                isRead = false,
                createdAt = now,
                expiresAt = now + (7 * 24 * 60 * 60 * 1000), // 7 days
                dirty = true,
                syncedAt = null
            )
            farmAlertDao.upsert(alert)
            showNotification(
                id = NOTIFICATION_ID_BASE + 1,
                title = "Vaccination Overdue!",
                message = alert.message,
                actionRoute = alert.actionRoute
            )
        }

        // Check vaccinations due tomorrow
        val dueTomorrow = vaccinationRecordDao.countScheduledBetweenForFarmer(farmerId, now, tomorrow)
        if (dueTomorrow > 0) {
            val alert = FarmAlertEntity(
                alertId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                alertType = "VACCINATION_DUE",
                severity = "MEDIUM",
                message = "$dueTomorrow vaccination(s) due tomorrow",
                actionRoute = "vaccination_schedule",
                isRead = false,
                createdAt = now,
                expiresAt = tomorrow + (24 * 60 * 60 * 1000),
                dirty = true,
                syncedAt = null
            )
            farmAlertDao.upsert(alert)
            showNotification(
                id = NOTIFICATION_ID_BASE + 2,
                title = "Vaccination Reminder",
                message = alert.message,
                actionRoute = alert.actionRoute
            )
        }
    }

    /**
     * Check for quarantine update alerts
     */
    suspend fun checkQuarantineAlerts(farmerId: String) {
        val now = System.currentTimeMillis()
        val updateCutoff = now - (24 * 60 * 60 * 1000) // 24 hours ago
        
        val overdueUpdates = quarantineRecordDao.getUpdatesOverdueForFarmer(farmerId, updateCutoff)
        if (overdueUpdates.isNotEmpty()) {
            val alert = FarmAlertEntity(
                alertId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                alertType = "QUARANTINE_UPDATE_DUE",
                severity = "HIGH",
                message = "${overdueUpdates.size} quarantine record(s) need update",
                actionRoute = "quarantine_list",
                isRead = false,
                createdAt = now,
                expiresAt = now + (3 * 24 * 60 * 60 * 1000), // 3 days
                dirty = true,
                syncedAt = null
            )
            farmAlertDao.upsert(alert)
            showNotification(
                id = NOTIFICATION_ID_BASE + 3,
                title = "Quarantine Update Required",
                message = alert.message,
                actionRoute = alert.actionRoute
            )
        }
    }

    /**
     * Check for hatching alerts
     */
    suspend fun checkHatchingAlerts(farmerId: String) {
        val now = System.currentTimeMillis()
        val threeDaysFromNow = now + (3 * 24 * 60 * 60 * 1000)
        
        val dueSoon = hatchingBatchDao.getHatchingDueSoon(farmerId, now, threeDaysFromNow)
        if (dueSoon.isNotEmpty()) {
            val alert = FarmAlertEntity(
                alertId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                alertType = "HATCHING_DUE",
                severity = "MEDIUM",
                message = "${dueSoon.size} batch(es) due to hatch soon",
                actionRoute = "hatching_batches",
                isRead = false,
                createdAt = now,
                expiresAt = threeDaysFromNow,
                dirty = true,
                syncedAt = null
            )
            farmAlertDao.upsert(alert)
            showNotification(
                id = NOTIFICATION_ID_BASE + 4,
                title = "Hatching Alert",
                message = alert.message,
                actionRoute = alert.actionRoute
            )
        }
    }

    /**
     * Run all health checks
     */
    suspend fun runAllHealthChecks(farmerId: String) {
        checkVaccinationAlerts(farmerId)
        checkQuarantineAlerts(farmerId)
        checkHatchingAlerts(farmerId)
    }

    /**
     * Show local notification
     */
    private fun showNotification(
        id: Int,
        title: String,
        message: String,
        actionRoute: String?
    ) {
        val intent = Intent(context, com.rio.rostry.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            actionRoute?.let { putExtra("route", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }

    /**
     * Clear read alerts for a farmer
     */
    suspend fun clearReadAlerts(farmerId: String) {
        farmAlertDao.deleteReadAlerts(farmerId)
    }
}
