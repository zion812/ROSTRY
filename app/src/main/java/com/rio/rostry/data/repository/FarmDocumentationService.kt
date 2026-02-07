package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Farm-wide Documentation model containing aggregated data across all assets.
 */
data class FarmDocumentation(
    val farmName: String,
    val farmerId: String,
    val generatedAt: Long = System.currentTimeMillis(),
    val assets: List<FarmAssetEntity>,
    val assetsByType: Map<String, List<FarmAssetEntity>>,
    val assetsByStatus: Map<String, List<FarmAssetEntity>>,
    // Aggregated records
    val totalVaccinations: Int,
    val totalGrowthRecords: Int,
    val totalMortality: Int,
    val totalActivities: Int,
    // Financial summaries
    val totalFeedKg: Double,
    val totalExpensesInr: Double,
    val expensesByType: Map<String, Double>,
    // Time range
    val earliestBirthDate: Long?,
    val activeDaysRange: String
)

/**
 * Asset summary for farm-wide reports.
 */
data class AssetSummary(
    val asset: FarmAssetEntity,
    val vaccinationCount: Int,
    val growthRecordCount: Int,
    val mortalityCount: Int,
    val feedKg: Double,
    val expensesInr: Double,
    val currentWeight: Double?,
    val daysActive: Int
)

/**
 * Service to aggregate all farm-level data for comprehensive documentation and export.
 */
@Singleton
class FarmDocumentationService @Inject constructor(
    private val farmAssetDao: FarmAssetDao,
    private val farmActivityLogDao: FarmActivityLogDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val growthRecordDao: GrowthRecordDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val currentUserProvider: CurrentUserProvider
) {

    /**
     * Load complete farm documentation for the current user.
     */
    suspend fun loadFarmDocumentation(): FarmDocumentation? {
        val farmerId = currentUserProvider.userIdOrNull() ?: return null
        val farmerName = "My Farm" // Could be fetched from user profile if needed
        
        // Load all farm assets
        val allAssets = farmAssetDao.getAssetsByFarmer(farmerId).first()
        if (allAssets.isEmpty()) return null
        
        // Group assets by type and status
        val assetsByType = allAssets.groupBy { it.assetType }
        val assetsByStatus = allAssets.groupBy { it.status }
        
        // Load all activities
        val allActivities = farmActivityLogDao.observeForFarmer(farmerId).first()
        
        // Load all vaccinations for all assets
        var totalVaccinations = 0
        var totalGrowthRecords = 0
        var totalMortality = 0
        
        for (asset in allAssets) {
            totalVaccinations += vaccinationRecordDao.observeForProduct(asset.assetId).first().size
            totalGrowthRecords += growthRecordDao.observeForProduct(asset.assetId).first().size
            totalMortality += mortalityRecordDao.getByProduct(asset.assetId).sumOf { it.quantity }
        }
        
        // Calculate financial summaries
        val totalFeedKg = allActivities
            .filter { it.activityType == "FEED" }
            .sumOf { it.quantity ?: 0.0 }
        
        val totalExpensesInr = allActivities
            .filter { it.amountInr != null && it.amountInr > 0 }
            .sumOf { it.amountInr ?: 0.0 }
        
        val expensesByType = allActivities
            .filter { it.amountInr != null && it.amountInr > 0 }
            .groupBy { it.activityType }
            .mapValues { entry -> entry.value.sumOf { it.amountInr ?: 0.0 } }
        
        // Calculate time range
        val earliestBirthDate = allAssets.mapNotNull { it.birthDate }.minOrNull()
        val latestDate = System.currentTimeMillis()
        val activeDaysRange = if (earliestBirthDate != null) {
            val days = ((latestDate - earliestBirthDate) / (1000 * 60 * 60 * 24)).toInt()
            "$days days"
        } else {
            "N/A"
        }
        
        return FarmDocumentation(
            farmName = farmerName,
            farmerId = farmerId,
            assets = allAssets,
            assetsByType = assetsByType,
            assetsByStatus = assetsByStatus,
            totalVaccinations = totalVaccinations,
            totalGrowthRecords = totalGrowthRecords,
            totalMortality = totalMortality,
            totalActivities = allActivities.size,
            totalFeedKg = totalFeedKg,
            totalExpensesInr = totalExpensesInr,
            expensesByType = expensesByType,
            earliestBirthDate = earliestBirthDate,
            activeDaysRange = activeDaysRange
        )
    }

    /**
     * Generate asset summaries for all farm assets.
     */
    suspend fun generateAssetSummaries(): List<AssetSummary> {
        val farmerId = currentUserProvider.userIdOrNull() ?: return emptyList()
        val allAssets = farmAssetDao.getAssetsByFarmer(farmerId).first()
        
        return allAssets.map { asset ->
            val vaccinations = vaccinationRecordDao.observeForProduct(asset.assetId).first()
            val growthRecords = growthRecordDao.observeForProduct(asset.assetId).first()
            val mortalityRecords = mortalityRecordDao.getByProduct(asset.assetId)
            val activities = farmActivityLogDao.observeForProduct(asset.assetId).first()
            
            val feedKg = activities
                .filter { it.activityType == "FEED" }
                .sumOf { it.quantity ?: 0.0 }
            
            val expensesInr = activities
                .filter { it.amountInr != null && it.amountInr > 0 }
                .sumOf { it.amountInr ?: 0.0 }
            
            val currentWeight = growthRecords
                .maxByOrNull { it.createdAt }
                ?.weightGrams
            
            val daysActive = if (asset.birthDate != null) {
                val diffMs = System.currentTimeMillis() - asset.birthDate
                (diffMs / (1000 * 60 * 60 * 24)).toInt()
            } else {
                0
            }
            
            AssetSummary(
                asset = asset,
                vaccinationCount = vaccinations.size,
                growthRecordCount = growthRecords.size,
                mortalityCount = mortalityRecords.sumOf { it.quantity },
                feedKg = feedKg,
                expensesInr = expensesInr,
                currentWeight = currentWeight,
                daysActive = daysActive
            )
        }
    }

    companion object {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        
        fun formatDate(timestamp: Long): String {
            return dateFormat.format(Date(timestamp))
        }
    }
}
