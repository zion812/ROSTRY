package com.rio.rostry.workers.processors

import android.content.Context
import com.rio.rostry.data.database.dao.FarmEventDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.EventStatus
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processor for generating daily briefing notifications.
 * Sends a summary notification about today's farm events.
 */
@Singleton
class DailyBriefingProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val farmEventDao: FarmEventDao
) : LifecycleProcessor {
    
    override val processorName = "DailyBriefingProcessor"
    
    override suspend fun process(now: Long): Int {
        var briefingsSent = 0
        
        try {
            val endOfDay = getEndOfDay(now)
            val farmerIds = productDao.getDistinctSellerIds()
            
            for (farmerId in farmerIds) {
                val events = farmEventDao.getEventsByDateRange(farmerId, now, endOfDay)
                    .first()
                    .filter { it.status != EventStatus.COMPLETED }
                
                if (events.isNotEmpty()) {
                    FarmNotifier.dailyBriefing(
                        context,
                        events.size,
                        events.first().title
                    )
                    briefingsSent++
                }
            }
            Timber.d("$processorName: Sent $briefingsSent briefings")
        } catch (e: Exception) {
            Timber.e(e, "Error in $processorName")
        }
        
        return briefingsSent
    }
    
    private fun getEndOfDay(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
