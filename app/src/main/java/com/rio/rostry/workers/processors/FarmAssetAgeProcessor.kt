package com.rio.rostry.workers.processors

import android.content.Context
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.domain.model.LifecycleSubStage
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processor for updating farm asset ages.
 * Calculates and updates ageWeeks for all farm assets with birthDate.
 * Also updates granular sub-stage transitions.
 */
@Singleton
class FarmAssetAgeProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val farmAssetDao: FarmAssetDao
) : LifecycleProcessor {
    
    override val processorName = "FarmAssetAgeProcessor"
    
    override suspend fun process(now: Long): Int {
        val startTime = System.currentTimeMillis()
        var updatedCount = 0
        
        val farmerIds = farmAssetDao.getAllFarmerIds()
        for (farmerId in farmerIds) {
            try {
                val assets = farmAssetDao.getAssetsWithBirthDate(farmerId)
                for (asset in assets) {
                    val birthDate = asset.birthDate ?: continue
                    val ageWeeks = ((now - birthDate) / (7L * 24 * 60 * 60 * 1000)).toInt()
                    
                    // Only update if age changed
                    if (asset.ageWeeks != ageWeeks) {
                        farmAssetDao.updateAgeWeeks(asset.assetId, ageWeeks, now)
                        updatedCount++
                        
                        // Update granular sub-stage
                        val subStage = LifecycleSubStage.fromAge(ageWeeks).name
                        if (asset.lifecycleSubStage != subStage) {
                            farmAssetDao.updateLifecycleSubStage(asset.assetId, subStage, now)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to update ages for farmer=$farmerId")
            }
        }
        
        Timber.d("$processorName: Updated $updatedCount assets in ${System.currentTimeMillis() - startTime}ms")
        return updatedCount
    }
}
