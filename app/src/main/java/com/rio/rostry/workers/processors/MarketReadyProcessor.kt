package com.rio.rostry.workers.processors

import android.content.Context
import com.rio.rostry.data.database.dao.FarmAlertDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processor for detecting market-ready batches.
 * Criteria: Age >= 6 weeks AND average weight >= 1500g (broilers).
 * Creates HARVEST_READY alerts for farmers to convert batches to listings.
 */
@Singleton
class MarketReadyProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productDao: ProductDao,
    private val alertDao: FarmAlertDao
) : LifecycleProcessor {
    
    override val processorName = "MarketReadyProcessor"
    
    companion object {
        private const val MIN_AGE_WEEKS = 6
        private const val MIN_WEIGHT_GRAMS = 1500.0
    }
    
    override suspend fun process(now: Long): Int {
        val batches = productDao.getActiveWithBirth().filter { 
            it.isBatch == true && it.lifecycleStatus == "ACTIVE" 
        }
        var harvestReadyCount = 0
        
        for (batch in batches) {
            val birthDate = batch.birthDate ?: continue
            val ageWeeks = TimeUnit.MILLISECONDS.toDays(now - birthDate) / 7
            val avgWeight = batch.weightGrams ?: 0.0
            
            val isMarketReady = ageWeeks >= MIN_AGE_WEEKS && avgWeight >= MIN_WEIGHT_GRAMS
            
            if (isMarketReady) {
                val existingAlerts = alertDao.getByTypeForProduct(batch.productId, "HARVEST_READY")
                if (existingAlerts.isEmpty()) {
                    val alert = FarmAlertEntity(
                        alertId = UUID.randomUUID().toString(),
                        farmerId = batch.sellerId,
                        alertType = "HARVEST_READY",
                        severity = "INFO",
                        message = "${batch.name ?: "Batch"} is market-ready! ${batch.quantity?.toInt() ?: 0} birds, ${avgWeight.toInt()}g avg, ${ageWeeks} weeks old",
                        actionRoute = Routes.Builders.createListingFromAsset(batch.productId),
                        createdAt = now
                    )
                    alertDao.upsert(alert)
                    harvestReadyCount++
                    
                    FarmNotifier.harvestReady(context, batch.productId, batch.name ?: "Batch")
                }
            }
        }
        
        if (harvestReadyCount > 0) {
            Timber.d("$processorName: Detected $harvestReadyCount market-ready batches")
        }
        return harvestReadyCount
    }
}
