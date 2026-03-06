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
import com.rio.rostry.domain.usecase.FcrWarningUseCase
import com.rio.rostry.domain.usecase.MarketReadyPredictionUseCase
import com.rio.rostry.data.database.entity.FarmAlertEntity
import java.util.UUID
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
    private val alertDao: FarmAlertDao,
    private val fcrWarningUseCase: FcrWarningUseCase,
    private val marketReadyPredictionUseCase: MarketReadyPredictionUseCase
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
                
                // Track predictions for aggregation
                var totalFcr = 0.0
                var fcrCount = 0
                var earliestHarvestDays: Int? = null
                val activeProducts = productDao.getActiveBySellerList(farmerId)

                for (p in activeProducts) {
                    // 1. FCR Predictions
                    val fcrResult = fcrWarningUseCase.execute(p.productId)
                    if (fcrResult.fcr > 0) {
                        totalFcr += fcrResult.fcr
                        fcrCount++
                    }
                    if (fcrResult.isWarning) {
                        alertDao.upsert(
                            FarmAlertEntity(
                                alertId = "fcr_${p.productId}_${now / 86400000}",
                                farmerId = farmerId,
                                alertType = "FCR_WARNING",
                                severity = "WARNING",
                                message = fcrResult.message ?: "Underperforming FCR for ${p.name}",
                                actionRoute = "monitoring/growth/${p.productId}",
                                createdAt = now
                            )
                        )
                    }

                    // 2. Market Readiness Predictions (Target: 2kg for broilers)
                    val targetWeight = 2000.0 // benchmark
                    val marketResult = marketReadyPredictionUseCase.execute(p.productId, targetWeight)
                    if (!marketResult.isReady && marketResult.projectedDaysToTarget != null) {
                        if (earliestHarvestDays == null || marketResult.projectedDaysToTarget < earliestHarvestDays) {
                            earliestHarvestDays = marketResult.projectedDaysToTarget
                        }
                    } else if (marketResult.isReady) {
                        earliestHarvestDays = 0
                    }
                }

                val avgFcr = if (fcrCount > 0) totalFcr / fcrCount else 0.0
                val totalFeed = dailyLogDao.getTotalFeedBetween(farmerId, startOfMonth, now)
                
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
                    daysUntilHarvest = earliestHarvestDays,
                    estimatedHarvestDate = earliestHarvestDays?.let { now + (it.toLong() * 24 * 60 * 60 * 1000) },
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
