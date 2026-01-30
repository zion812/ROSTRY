package com.rio.rostry.workers.processors

import android.content.Context
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.ui.navigation.Routes
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processor for detecting underperforming birds (proactive culling).
 * Identifies birds in the bottom 10% weight for their age cohort.
 */
@Singleton
class UnderperformingBirdProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val alertDao: FarmAlertDao
) : LifecycleProcessor {
    
    override val processorName = "UnderperformingBirdProcessor"
    
    companion object {
        private const val MIN_COHORT_SIZE = 5
        private const val MIN_FLOCK_SIZE = 10
        private const val PERCENTILE_CUTOFF = 0.1 // Bottom 10%
        private const val MIN_PERCENT_BELOW_AVG = 20
    }
    
    override suspend fun process(now: Long): Int {
        var alertsCreated = 0
        
        try {
            val farmerIds = productDao.getDistinctSellerIds()
            
            for (farmerId in farmerIds) {
                val birds = productDao.getActiveWithWeightByFarmer(farmerId)
                if (birds.size < MIN_FLOCK_SIZE) continue
                
                // Group by age cohort
                val cohorts = birds.groupBy { it.ageWeeks ?: 0 }
                
                for ((ageWeeks, cohort) in cohorts) {
                    if (cohort.size < MIN_COHORT_SIZE) continue
                    
                    // Calculate 10th percentile weight
                    val sortedByWeight = cohort.sortedBy { it.weightGrams ?: 0.0 }
                    val cutoffIndex = (cohort.size * PERCENTILE_CUTOFF).toInt().coerceAtLeast(1)
                    val bottom10Percent = sortedByWeight.take(cutoffIndex)
                    
                    for (bird in bottom10Percent) {
                        val avgWeight = cohort.mapNotNull { it.weightGrams }.average()
                        val birdWeight = bird.weightGrams ?: 0.0
                        val percentBelowAvg = ((avgWeight - birdWeight) / avgWeight * 100).toInt()
                        
                        if (percentBelowAvg >= MIN_PERCENT_BELOW_AVG) {
                            alertDao.upsert(
                                FarmAlertEntity(
                                    alertId = "underperforming_${bird.productId}_${now / (24 * 60 * 60 * 1000)}",
                                    farmerId = farmerId,
                                    alertType = "UNDERPERFORMING_BIRD",
                                    severity = "INFO",
                                    message = "${bird.name ?: "Bird"} is ${percentBelowAvg}% below avg weight for ${ageWeeks}-week birds. Consider culling or separation.",
                                    actionRoute = Routes.Builders.productDetails(bird.productId),
                                    createdAt = now
                                )
                            )
                            alertsCreated++
                        }
                    }
                }
            }
            Timber.d("$processorName: Created $alertsCreated alerts")
        } catch (e: Exception) {
            Timber.e(e, "Error in $processorName")
        }
        
        return alertsCreated
    }
}
