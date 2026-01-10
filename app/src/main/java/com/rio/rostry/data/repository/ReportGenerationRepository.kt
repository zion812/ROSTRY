package com.rio.rostry.data.repository

import android.content.Context
import android.net.Uri
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.export.PdfExporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class for monthly report.
 */
data class MonthlyReportData(
    val farmerId: String,
    val farmerName: String,
    val month: Int,
    val year: Int,
    val reportDate: Long = System.currentTimeMillis(),
    
    // Egg Production
    val totalEggsCollected: Int = 0,
    val avgDailyEggs: Double = 0.0,
    val eggProductionTrend: String = "STABLE", // INCREASING, DECREASING, STABLE
    
    // Mortality
    val totalMortality: Int = 0,
    val mortalityRate: Double = 0.0,
    val mortalityCauses: Map<String, Int> = emptyMap(),
    
    // Financials
    val totalExpenses: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val totalFeedCost: Double = 0.0,
    val totalVaccineCost: Double = 0.0,
    
    // Assets
    val totalBirds: Int = 0,
    val topPerformingPens: List<TopPerformerData> = emptyList(),
    
    // Growth
    val avgWeightGain: Double = 0.0,
    val avgFCR: Double = 0.0,
    
    // Vaccination
    val vaccinationsAdministered: Int = 0,
    val vaccinationsDue: Int = 0,
    val vaccinationComplianceRate: Double = 0.0,
    
    // Hatching (if applicable)
    val batchesHatched: Int = 0,
    val avgHatchabilityRate: Double = 0.0
)

data class TopPerformerData(
    val assetId: String,
    val name: String,
    val metric: String,
    val value: Double
)

/**
 * Repository for generating farm reports.
 */
@Singleton
class ReportGenerationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    
    /**
     * Generate monthly farm report.
     */
    suspend fun generateMonthlyReport(
        farmerId: String,
        farmerName: String,
        month: Int,
        year: Int
    ): Resource<Uri> = withContext(Dispatchers.IO) {
        try {
            // Calculate date range
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.timeInMillis
            
            calendar.add(Calendar.MONTH, 1)
            val endDate = calendar.timeInMillis
            
            // Collect data
            val reportData = collectReportData(farmerId, farmerName, month, year, startDate, endDate)
            
            // Generate PDF
            val fileName = "ROSTRY_Monthly_Report_${year}_${month.toString().padStart(2, '0')}.pdf"
            
            val sections = buildReportSections(reportData)
            val uri = PdfExporter.writeReport(
                context = context,
                fileName = fileName,
                docTitle = "Monthly Farm Report - ${getMonthName(month)} $year",
                coverBitmap = null,
                sections = sections
            )
            
            Resource.Success(uri)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate report")
        }
    }
    
    /**
     * Collect all data for the report.
     */
    private suspend fun collectReportData(
        farmerId: String,
        farmerName: String,
        month: Int,
        year: Int,
        startDate: Long,
        endDate: Long
    ): MonthlyReportData {
        // Get assets
        val assets = database.farmAssetDao().getAssetsByFarmer(farmerId).first()
        val totalBirds = assets.sumOf { it.quantity.toInt() }
        
        // Get daily logs for feed and weight
        val dailyLogs = database.dailyLogDao().observeForFarmerBetween(farmerId, startDate, endDate).first()
        val totalFeed = dailyLogs.sumOf { it.feedKg ?: 0.0 }
        
        // Get activity logs for expenses
        val activityLogs = database.farmActivityLogDao().observeForFarmerBetween(farmerId, startDate, endDate).first()
        val expensesByCategory = activityLogs
            .filter { it.amountInr != null && it.amountInr > 0 }
            .groupBy { it.category ?: "Other" }
            .mapValues { entry -> entry.value.sumOf { it.amountInr ?: 0.0 } }
        val totalExpenses = expensesByCategory.values.sum()
        
        // Get mortality records
        val mortalityCount = database.mortalityRecordDao().countForFarmerBetween(farmerId, startDate, endDate)
        val mortalityRate = if (totalBirds > 0) (mortalityCount.toDouble() / totalBirds) * 100 else 0.0
        
        // Get vaccination records
        val vaccinationsAdministered = database.vaccinationRecordDao()
            .countAdministeredBetweenForFarmer(farmerId, startDate, endDate)
        val vaccinationsDue = database.vaccinationRecordDao()
            .countDueForFarmer(farmerId, startDate, endDate)
        val totalVaccinations = vaccinationsAdministered + vaccinationsDue
        val complianceRate = if (totalVaccinations > 0) 
            (vaccinationsAdministered.toDouble() / totalVaccinations) * 100 else 100.0
        
        // Get growth records
        val growthRecords = database.growthRecordDao().countForFarmerBetween(farmerId, startDate, endDate)
        
        return MonthlyReportData(
            farmerId = farmerId,
            farmerName = farmerName,
            month = month,
            year = year,
            totalBirds = totalBirds,
            totalExpenses = totalExpenses,
            expensesByCategory = expensesByCategory,
            totalFeedCost = activityLogs
                .filter { it.activityType == "FEED" }
                .sumOf { it.amountInr ?: 0.0 },
            totalVaccineCost = activityLogs
                .filter { it.activityType == "MEDICATION" }
                .sumOf { it.amountInr ?: 0.0 },
            totalMortality = mortalityCount,
            mortalityRate = mortalityRate,
            vaccinationsAdministered = vaccinationsAdministered,
            vaccinationsDue = vaccinationsDue,
            vaccinationComplianceRate = complianceRate
        )
    }
    
    /**
     * Build report sections for PDF.
     */
    private fun buildReportSections(data: MonthlyReportData): List<PdfExporter.TableSection> {
        val sections = mutableListOf<PdfExporter.TableSection>()
        
        // Executive Summary
        sections.add(
            PdfExporter.TableSection(
                title = "Executive Summary",
                headers = listOf("Metric", "Value"),
                rows = listOf(
                    listOf("Total Birds", data.totalBirds.toString()),
                    listOf("Total Expenses", "₹${String.format("%.2f", data.totalExpenses)}"),
                    listOf("Mortality Rate", "${String.format("%.1f", data.mortalityRate)}%"),
                    listOf("Vaccination Compliance", "${String.format("%.1f", data.vaccinationComplianceRate)}%")
                )
            )
        )
        
        // Financial Summary
        if (data.expensesByCategory.isNotEmpty()) {
            sections.add(
                PdfExporter.TableSection(
                    title = "Expenses by Category",
                    headers = listOf("Category", "Amount (₹)"),
                    rows = data.expensesByCategory.map { (category, amount) ->
                        listOf(category, String.format("%.2f", amount))
                    }
                )
            )
        }
        
        // Mortality Summary
        sections.add(
            PdfExporter.TableSection(
                title = "Mortality Summary",
                headers = listOf("Metric", "Value"),
                rows = listOf(
                    listOf("Total Deaths", data.totalMortality.toString()),
                    listOf("Mortality Rate", "${String.format("%.2f", data.mortalityRate)}%")
                )
            )
        )
        
        // Vaccination Summary
        sections.add(
            PdfExporter.TableSection(
                title = "Vaccination Summary",
                headers = listOf("Status", "Count"),
                rows = listOf(
                    listOf("Administered", data.vaccinationsAdministered.toString()),
                    listOf("Pending/Due", data.vaccinationsDue.toString()),
                    listOf("Compliance Rate", "${String.format("%.1f", data.vaccinationComplianceRate)}%")
                )
            )
        )
        
        return sections
    }
    
    /**
     * Get month name.
     */
    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }
    }
}
