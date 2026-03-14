package com.rio.rostry.data.admin.repository

import com.rio.rostry.domain.admin.model.MortalityRiskLevel
import com.rio.rostry.domain.admin.model.OutbreakAlert
import com.rio.rostry.domain.admin.model.RegionalMortality
import com.rio.rostry.data.database.dao.MortalityRecordDao
import com.rio.rostry.domain.admin.repository.AdminMortalityRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMortalityRepositoryImpl @Inject constructor(
    private val mortalityDao: MortalityRecordDao
) : AdminMortalityRepository {

    override fun getRegionalMortalityStats(): Flow<Resource<List<RegionalMortality>>> = flow {
        try {
            val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            val recentRecords = mortalityDao.getRecentRecords(sevenDaysAgo)

            val userLocationMap = mutableMapOf<String, String>()
            val grouped = recentRecords.groupBy { userLocationMap[it.farmerId] ?: "Unknown" }

            val stats = grouped.map { (region, records) ->
                val farmsInRegion = records.map { it.farmerId }.distinct().size
                val deaths24h = records.count {
                    it.occurredAt > System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
                }

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
                    averageMortalityRate = 0.0,
                    riskLevel = risk
                )
            }.sortedByDescending { it.reportedDeaths24h }

            emit(Resource.Success(stats))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load regional mortality stats"))
        }
    }

    override fun getPotentialOutbreaks(): Flow<Resource<List<OutbreakAlert>>> = flow {
        try {
            getRegionalMortalityStats().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val outbreaks = (result.data ?: emptyList()).filter {
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
                        emit(Resource.Success(outbreaks))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Failed to detect outbreaks"))
                    }
                    is Resource.Loading -> {}
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to detect outbreaks"))
        }
    }
}
