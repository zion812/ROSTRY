package com.rio.rostry.workers.processors

import android.content.Context
import com.rio.rostry.data.database.dao.DashboardCacheDao
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.entity.DashboardCacheEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processor for populating dashboard caches.
 * Pre-computes stats for instant dashboard loading (Split-Brain Architecture).
 */
@Singleton
class DashboardCacheProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val dashboardCacheDao: DashboardCacheDao,
    private val vaccinationDao: VaccinationRecordDao,
    private val mortalityDao: MortalityRecordDao,
    private val quarantineDao: QuarantineRecordDao,
    private val dailyLogDao: DailyLogDao,
    private val alertDao: FarmAlertDao
) : LifecycleProcessor {
    
    override val processorName = "DashboardCacheProcessor"
    
    override suspend fun process(now: Long): Int {
        val startTime = System.currentTimeMillis()
        val farmerIds = productDao.getDistinctSellerIds()
        var processedCount = 0
        
        for (farmerId in farmerIds) {
            try {
                val startOfMonth = getStartOfMonth(now)
                
                // Aggregate calculations
                val totalBirds = productDao.countActiveByFarmer(farmerId)
                val totalBatches = productDao.countBatchesByFarmer(farmerId)
                val pendingVaccines = vaccinationDao.countPendingByFarmer(farmerId, now)
                val overdueVaccines = vaccinationDao.countOverdueForFarmer(farmerId, now)
                val quarantinedCount = quarantineDao.countActiveForFarmer(farmerId)
                val totalMortalityThisMonth = mortalityDao.countForFarmerBetween(farmerId, startOfMonth, now)
                
                // Calculate FCR
                val totalFeed = dailyLogDao.getTotalFeedBetween(farmerId, startOfMonth, now)
                val avgWeight = dailyLogDao.getAverageWeightBetween(farmerId, startOfMonth, now)
                val avgFcr = if (avgWeight > 0) totalFeed / (avgWeight / 1000.0) else 0.0
                
                val cache = DashboardCacheEntity(
                    cacheId = "cache_$farmerId",
                    farmerId = farmerId,
                    totalBirds = totalBirds,
                    totalBatches = totalBatches,
                    pendingVaccines = pendingVaccines,
                    overdueVaccines = overdueVaccines,
                    avgFcr = avgFcr,
                    totalFeedKgThisMonth = totalFeed,
                    totalMortalityThisMonth = totalMortalityThisMonth,
                    healthyCount = totalBirds - quarantinedCount,
                    quarantinedCount = quarantinedCount,
                    alertCount = alertDao.countUnread(farmerId),
                    computedAt = now,
                    computationDurationMs = System.currentTimeMillis() - startTime
                )
                dashboardCacheDao.upsert(cache)
                processedCount++
                
            } catch (e: Exception) {
                Timber.e(e, "Error computing dashboard cache for farmer=$farmerId")
            }
        }
        
        Timber.d("$processorName: Updated $processedCount caches in ${System.currentTimeMillis() - startTime}ms")
        return processedCount
    }
    
    private fun getStartOfMonth(now: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
