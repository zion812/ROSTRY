package com.rio.rostry.data.repository.admin

import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class AdminMortalityRepositoryImpl @Inject constructor(
    private val mortalityDao: MortalityRecordDao,
    private val userRepository: UserRepository
) : AdminMortalityRepository {

    override fun getRegionalMortalityStats(): Flow<Resource<List<RegionalMortality>>> = flow {
        emit(Resource.Loading())
        try {
            // Fetch records for the last 7 days
            val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            val recentRecords = mortalityDao.getRecentRecords(sevenDaysAgo)
            
            // Collect unique farmer IDs to fetch location data
            val farmerIds = recentRecords.map { it.farmerId }.distinct()
            
            // In a real optimized system, we'd have a specific DAO call for this.
            // Here we iterate (assuming admin use < 1000 active users for MVP)
            // or better, fetch all system users if possible or construct a lookup.
            val users = userRepository.getSystemUsers().data ?: emptyList()
            val userLocationMap = users.associate { it.userId to (it.farmCity ?: "Unknown") }
            
            // Group by Region
            val grouped = recentRecords.groupBy { userLocationMap[it.farmerId] ?: "Unknown" }
            
            val stats = grouped.map { (region, records) ->
                val farmsInRegion = records.map { it.farmerId }.distinct().size
                val deaths24h = records.count { it.occurredAt > System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1) }
                
                // Simplified "Mortality Rate" calculation (Deaths / 100 assumed stock for MVP normalization if stock unknown)
                // Ideally we join with Inventory, but let's stick to raw counts for alerting
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
            
            emit(Resource.Success(stats))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to aggregate stats"))
        }
    }

    override fun getPotentialOutbreaks(): Flow<Resource<List<OutbreakAlert>>> = flow {
        emit(Resource.Loading())
        // Re-use logic or implement specific outbreak detection
        // For MVP, filtering the stats above is sufficient
        getRegionalMortalityStats().collect { resource ->
            if (resource is Resource.Success) {
                val outbreaks = resource.data?.filter { 
                    it.riskLevel == MortalityRiskLevel.HIGH || it.riskLevel == MortalityRiskLevel.CRITICAL 
                }?.map { region ->
                    OutbreakAlert(
                        regionName = region.regionName,
                        affectedFarmsCount = region.totalFarms,
                        detectedAt = System.currentTimeMillis(),
                        severity = region.riskLevel,
                        description = "High mortality detected: ${region.reportedDeaths24h} deaths in 24h"
                    )
                } ?: emptyList()
                emit(Resource.Success(outbreaks))
            } else if (resource is Resource.Error) {
                emit(Resource.Error(resource.message ?: "Error"))
            }
        }
    }
}
