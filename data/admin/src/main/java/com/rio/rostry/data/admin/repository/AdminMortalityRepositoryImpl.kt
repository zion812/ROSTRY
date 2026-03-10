package com.rio.rostry.data.admin.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.MortalityRiskLevel
import com.rio.rostry.core.model.OutbreakAlert
import com.rio.rostry.core.model.RegionalMortality
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.domain.admin.repository.AdminMortalityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of admin mortality operations.
 * Provides aggregated mortality statistics and outbreak detection.
 */
@Singleton
class AdminMortalityRepositoryImpl @Inject constructor(
    private val mortalityDao: MortalityRecordDao
) : AdminMortalityRepository {

    override fun getRegionalMortalityStats(): Flow<Result<List<RegionalMortality>>> = flow {        try {
            // Fetch records for the last 7 days
            val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            val recentRecords = mortalityDao.getRecentRecords(sevenDaysAgo)
            
            // Collect unique farmer IDs to fetch location data
            val farmerIds = recentRecords.map { it.farmerId }.distinct()
            val userLocationMap = mutableMapOf<String, String>()
            
            // Group by Region
            val grouped = recentRecords.groupBy { userLocationMap[it.farmerId] ?: "Unknown" }
            
            val stats = grouped.map { (region, records) ->
                val farmsInRegion = records.map { it.farmerId }.distinct().size
                val deaths24h = records.count { 
                    it.occurredAt > System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1) 
                }
                
                // Simplified "Mortality Rate" calculation
                val avgDeathsPerFarm = records.size.toDouble() / farmsInRegion
                
                val risk = when {
                    deaths24h > 50 -> MortalityRiskLevel.CRITICAL
                    deaths24h > 20 -> MortalityRiskLevel.HIGH
                    deaths24h > 5 -> MortalityRiskLevel.MODERATE
                    else -> MortalityRiskLevel.LOW
                }
                
                RegionalMortality(
                    regionName = region,
                    totalFarms = farmsInRegion,
                    reportedDeaths24h = deaths24h,
                    averageMortalityRate = 0.0, // Placeholder
                    riskLevel = risk
                )
            }.sortedByDescending { it.reportedDeaths24h }
            
            emit(Result.Success(stats))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getPotentialOutbreaks(): Flow<Result<List<OutbreakAlert>>> = flow {        try {
            // Re-use logic from regional stats
            getRegionalMortalityStats().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val outbreaks = result.data.filter { 
                            it.riskLevel == MortalityRiskLevel.HIGH || 
                            it.riskLevel == MortalityRiskLevel.CRITICAL 
                        }.map { region ->
                            OutbreakAlert(
                                regionName = region.regionName,
                                affectedFarmsCount = region.totalFarms,
                                detectedAt = System.currentTimeMillis(),
                                severity = region.riskLevel,
                                description = "High mortality detected: ${region.reportedDeaths24h} deaths in 24h"
                            )
                        }
                        emit(Result.Success(outbreaks))
                    }
                    is Result.Error -> {
                        emit(Result.Error(result.exception))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}




