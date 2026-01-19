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
    private val farmEventDao: FarmEventDao
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
