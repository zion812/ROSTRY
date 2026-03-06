package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * Predicts if a batch/bird has an underperforming Feed Conversion Ratio (FCR).
 * FCR = Total Feed Consumed / (Final Weight - Initial Weight)
 */
class FcrWarningUseCase @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val productDao: ProductDao
) {
    data class FcrResult(
        val fcr: Double,
        val isWarning: Boolean,
        val message: String? = null
    )

    suspend fun execute(productId: String, lookbackDays: Int = 14): FcrResult {
        val now = System.currentTimeMillis()
        val startDate = now - (lookbackDays.toLong() * 24 * 60 * 60 * 1000)
        
        val product = productDao.findById(productId) ?: return FcrResult(0.0, false)
        val totalFeed = dailyLogDao.getTotalFeedForProduct(productId, startDate, now)
        val initialWeight = dailyLogDao.getInitialWeightForProduct(productId, startDate)
        val finalWeight = dailyLogDao.getFinalWeightForProduct(productId, now)
        
        if (totalFeed <= 0 || initialWeight == null || finalWeight == null || finalWeight <= initialWeight) {
            return FcrResult(0.0, false, "Insufficient data for FCR calculation")
        }
        
        val weightGainKg = (finalWeight - initialWeight) / 1000.0
        val fcr = totalFeed / weightGainKg
        
        // Thresholds based on breed benchmarks (simplified)
        val benchmark = getBenchmarkFcr(product)
        val isWarning = fcr > (benchmark * 1.2) // 20% worse than benchmark
        
        val message = if (isWarning) {
            "FCR is %.2f (Benchmark: %.2f). Efficiency is low.".format(fcr, benchmark)
        } else null
        
        return FcrResult(fcr, isWarning, message)
    }
    
    private fun getBenchmarkFcr(p: ProductEntity): Double {
        // Simplified broiler/layer benchmarks
        return when {
            p.breed?.contains("Broiler", ignoreCase = true) == true -> 1.6
            p.breed?.contains("Layer", ignoreCase = true) == true -> 2.1
            else -> 1.8
        }
    }
}
