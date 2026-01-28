package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.FarmEventDao
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@HiltWorker
class EventReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val farmEventDao: FarmEventDao,
    private val farmAssetDao: com.rio.rostry.data.database.dao.FarmAssetDao,
    private val vaccinationRecordDao: com.rio.rostry.data.database.dao.VaccinationRecordDao,
    private val vaccinationEngine: com.rio.rostry.domain.monitoring.VaccinationRecommendationEngine,
    private val dewormingEngine: com.rio.rostry.domain.monitoring.DewormingRecommendationEngine
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val user = auth.currentUser
            
            if (user == null) {
                // No user logged in, nothing to remind
                return@withContext Result.success()
            }
            
            val userId = user.uid
            val now = System.currentTimeMillis()
            val lookAhead = now + TimeUnit.MINUTES.toMillis(30)
            
            // getPendingEventsInRange expects (farmerId, startTime, endTime)
            // It returns List<FarmEventEntity> because it is a suspend function (no Flow)
            val events = farmEventDao.getPendingEventsInRange(userId, now, lookAhead)
            
            for (event in events) {
                // Determine if we should notify
                val timeString = Instant.ofEpochMilli(event.scheduledAt)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                    
                FarmNotifier.eventReminder(
                    applicationContext,
                    event.eventId,
                    event.title,
                    timeString
                )
            }
            
            // --- Check Dynamic Recommendations ---
            val assets = farmAssetDao.getActiveAssetsOneShot()
            // Optimization: If no active assets, skip engines
            if (assets.isNotEmpty()) {
                val vacHistory = vaccinationRecordDao.getAllByFarmer(userId)
                val eventHistory = farmEventDao.getCompletedEventsByFarmer(userId)
                
                val vacRecs = vaccinationEngine.generateRecommendations(assets, vacHistory)
                val dewormRecs = dewormingEngine.generateRecommendations(assets, eventHistory)
                
                val allRecs = vacRecs + dewormRecs
                
                for (rec in allRecs) {
                    // Check if recommendation is due NOW or was due recently and not yet notified (simplified: due < lookAhead)
                    // Recommendations often have 'date' as 'dueStart'. 
                    // We only want to notify once or periodically. 
                    // Simple logic: If due date is within [now - 24h, lookAhead] 
                    // AND we haven't spammed them recently (no easy way to track spam without local storage of 'lastNotified')
                    // For now, let's just check if it falls exactly in the window or is overdue?
                    // Better: If 'date' is < lookAhead. 
                    // Problem: This will notify every 15 mins for overdue recommendations.
                    // Solution: Only notify if 'date' is within the strict [now, lookAhead] window.
                    
                    if (rec.date >= now && rec.date <= lookAhead) {
                         FarmNotifier.recommendationAlert(
                            applicationContext,
                            rec.id,
                            rec.title,
                            rec.description
                        )
                    }
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "EventReminderWorker"
        
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<EventReminderWorker>(15, TimeUnit.MINUTES)
                .build()
                
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    UNIQUE_NAME, 
                    ExistingPeriodicWorkPolicy.KEEP, 
                    request
                )
        }
    }
}
